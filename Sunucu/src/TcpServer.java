package Sunucu.src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {
    private final int port;

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

                ClientHandler handler = new ClientHandler(client);
                Thread thread = new Thread(handler);
                thread.start();
            }
        }
    }
}