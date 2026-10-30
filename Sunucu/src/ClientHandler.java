package Sunucu.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private final Socket client;

    public ClientHandler(Socket client) {
        this.client = client;
    }
    
    @Override
    public void run() {
        try(Socket socket = client;
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );
            BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                    socket.getOutputStream(),
                    StandardCharsets.UTF_8
                )
            )
        ){
            String message;

            while( (message = reader.readLine()) != null ){
                System.out.println("[" + socket.getRemoteSocketAddress() + "] " + message);

                writer.write("ACK: " + message);
                writer.newLine();
                writer.flush();
            }
            System.out.println("Client disconnected.");

        } catch(IOException e){
            System.err.println("Client connection failed: " + e.getMessage());
        }
    }
    
}
