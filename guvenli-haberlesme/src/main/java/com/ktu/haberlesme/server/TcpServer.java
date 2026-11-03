package com.ktu.haberlesme.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private final int port;
    private final ClientRegistry registry = new ClientRegistry();

    public TcpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket listener = new ServerSocket(port)){
            System.out.println("Listening on port " + port);
            System.out.println("Waiting for a connection...");
            while(true){
                Socket client = listener.accept();
                System.out.println("Client connected: " + client.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(client, registry);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }
    }
}