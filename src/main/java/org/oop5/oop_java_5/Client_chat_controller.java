package org.oop5.oop_java_5;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client_chat_controller implements Initializable {
    @FXML
    TextArea chatTextArea;
    @FXML
    TextField msgTextField;
    @FXML
    Button disconnectButton, sendButton;
    @FXML
    Label usernameDisplay;
    @FXML
    AnchorPane chatStage;


    private Socket socket;
    private DataOutputStream outStream;
    private Scanner in;

    BlockingQueue<Integer> stdInQueue = new LinkedBlockingQueue<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Use Platform.runLater to access the stage object after scene is set
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) chatStage.getScene().getWindow();
                Singleton instance = (Singleton) stage.getUserData();
                UserData user = instance.getUserData();
                socket = new Socket(user.getIp(), Integer.parseInt(user.getPort()));
                outStream = new DataOutputStream(socket.getOutputStream());

                System.setIn(new InputStream() {
                    @Override
                    public int read() throws IOException {
                        try {
                            int c = stdInQueue.take();
                            return c;
                        } catch (InterruptedException exc) {
                            Thread.currentThread().interrupt();
                            return -1;
                        }
                    }
                });

                sendButton.setOnAction(e -> {
                    for (char c : msgTextField.getText().toCharArray()) {
                        stdInQueue.add((int) c);
                    }
                    stdInQueue.add((int) '\n');
                    msgTextField.clear();
                });

                in = new Scanner(socket.getInputStream());  // Read from socket input stream
                Thread readThread = new Thread(this::readMessages);
                readThread.start();
                writeMessages();
            } catch (UnknownHostException e) {
                // Handle UnknownHostException
                System.err.println("Error: Unknown host.");
            } catch (IOException e) {
                // Handle IOException
                System.err.println("Error: IO Exception during connection.");
            }
        });
    }



    private void writeMessages() {
        Thread writeThread = new Thread(() -> {
            try {
                String message;
                while ((message = getMessageFromQueue()) != null) {
                    outStream.writeUTF(message);
                }
            } catch (IOException e) {
                System.err.println("Error: IO Exception during sending message.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        writeThread.start();
    }

    private String getMessageFromQueue() throws InterruptedException {
        StringBuilder messageBuilder = new StringBuilder();
        int c;
        while ((c = stdInQueue.take()) != -1) {
            messageBuilder.append((char) c);
            if (c == '\n') {
                break;
            }
        }
        return messageBuilder.toString().trim();
    }

    private void readMessages() {
        try {
            String line;
            while ((line = in.nextLine()) != null) {
                String finalLine = line;
                Platform.runLater(() -> chatTextArea.appendText(finalLine + "\n"));
            }
        } finally {
            // Close resources (in stream, socket) in a finally block
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

