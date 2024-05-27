package org.oop5.oop_java_5.ServerLogic;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final ServerSocket server;
    public static int PORT;
    public static final String STOP_STRING = "##";
    private final List<ConnectedClient> clients = new ArrayList<>();

    public Server(int newport) throws IOException {
        PORT = newport;
        server = new ServerSocket(PORT);
        while (true) {
            initConnections();
        }
    }

    private void initConnections() throws IOException {
        Socket clientSocket = server.accept();
        if (clientSocket.isConnected()) {
            ConnectedClient client = new ConnectedClient(clientSocket, this);
            clients.add(client);
            new Thread(() -> {
                try {
                    client.readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    clients.remove(client);
                }
            }).start();
        }
    }

    public void broadcastMessage(String message) {
        for (ConnectedClient client : clients) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server(3030);
    }
}
