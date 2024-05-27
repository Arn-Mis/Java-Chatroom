package org.oop5.oop_java_5.ServerLogic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConnectedClient {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;

    public ConnectedClient(Socket clientSocket, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.server = server;
        this.in = new DataInputStream(clientSocket.getInputStream());
        this.out = new DataOutputStream(clientSocket.getOutputStream());
    }

    public void readMessages() throws IOException {
        String line = "";
        while (!line.equals(Server.STOP_STRING)) {
            line = in.readUTF();
            System.out.println(line);
            server.broadcastMessage(line);
        }
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public void close() throws IOException {
        clientSocket.close();
        in.close();
        out.close();
    }
}
