package org.oop5.oop_java_5.ServerLogic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private Scanner scanner;

    public Client() throws IOException {
        socket = new Socket("127.0.0.1", Server.PORT);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        scanner = new Scanner(System.in);
        new Thread(this::readMessages).start();
        writeMessages();
    }

    private void writeMessages() throws IOException {
        String line = "";
        while (!line.equals(Server.STOP_STRING)) {
            line = scanner.nextLine();
            out.writeUTF(line);
        }
        close();
    }

    private void readMessages() {
        try {
            String line = "";
            while (!line.equals(Server.STOP_STRING)) {
                line = in.readUTF();
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        socket.close();
        scanner.close();
        out.close();
        in.close();
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }
}