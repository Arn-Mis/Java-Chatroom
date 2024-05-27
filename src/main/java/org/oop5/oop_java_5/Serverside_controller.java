package org.oop5.oop_java_5;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class Serverside_controller{

    @FXML
    Button onButton, offButton;
    @FXML
    Label serverPortInfo, serverIPInfo, serverActivityInfo;

    private ServerSocket server;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private static final int DEFAULT_PORT = 3030;
    private static final String DEFAULT_IP = "127.0.0.1";
    public static final String STOP_STRING = "##";

    private Thread listenerThread;

    public void onEnableAction(ActionEvent actionEvent) {
        Server();
        serverPortInfo.setText(String.valueOf(DEFAULT_PORT));
        serverIPInfo.setText(DEFAULT_IP);
        serverActivityInfo.setText("Enabled");
    }

    public void onDisableAction(ActionEvent actionEvent) throws IOException {
        Close();
        serverPortInfo.setText("");
        serverIPInfo.setText("");
        serverActivityInfo.setText("Disabled");
    }

    private void Close() throws IOException {
        if (inStream != null) {
            inStream.close();
        }
        if (server != null) {
            server.close();
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }

    public void Server() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
            listenerThread = new Thread(this::runListener);
            listenerThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runListener() {
        try {
            while (!Thread.interrupted()) {
                Socket clientSocket = server.accept();
                inStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                readMessages(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error during communication: " + e.getMessage());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readMessages(Socket clientSocket) throws IOException {
        String message;
        while ((((message = inStream.readUTF())) != null) && !message.equals(STOP_STRING)) {
            System.out.println("Received message from client: " + message);
        }
        System.out.println("Client disconnected or sent stop message.");
        clientSocket.close();
    }

}
