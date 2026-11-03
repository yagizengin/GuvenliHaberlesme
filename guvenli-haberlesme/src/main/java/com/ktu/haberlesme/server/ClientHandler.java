package com.ktu.haberlesme.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private final Socket client;
    private final ClientRegistry registry;

    private BufferedWriter writer;
    private String clientId;    

    public ClientHandler(Socket client, ClientRegistry registry) {
        this.client = client;
        this.registry = registry;
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
            BufferedWriter output = new BufferedWriter(
                new OutputStreamWriter(
                    socket.getOutputStream(),
                    StandardCharsets.UTF_8
                )
            )
        ){
            writer = output;

            if(!registerClient(reader)){
                return;
            }

            String line;

            while ( ((line = reader.readLine()) != null) ) {
                if(line.equals("QUIT")){
                    break;
                }

                handleMessage(line);
                
            }

        } catch(IOException e){
            System.err.println("Client connection failed: " + e.getMessage());
        } finally {
            registry.remove(clientId, this);
            System.out.println("Connection closed: " + client.getRemoteSocketAddress());
        }
    }

    private boolean registerClient(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        if(line == null) return false;

        if(!line.startsWith("HELLO|")) {
            send("ERROR|EXPECTED_HELLO");
            return false;
        }

        clientId = line.substring(6);

        if(!clientId.matches("[A-Za-z0-9_-]{1,32}")){
            send("ERROR|INVALID_CLIENT_ID");
            return false;
        }

        synchronized (this) {
            if(!registry.register(clientId, this)){
                send("ERROR|ID_ALREADY_USED");
                return false;
            }

            send("WELCOME|" + clientId);
        }

        System.out.println("Registered: " + clientId);
        return true;
    }

    public synchronized void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private void handleMessage(String line) throws IOException {
        String[] parts = line.split("\\|", 3);

        if(parts.length != 3 || !parts[0].equals("TEXT")){
            send("ERROR|EXPECTED_TEXT");
            return;
        }

        String recipientId = parts[1];
        String text = parts[2];

        ClientHandler recipient = registry.find(recipientId);

        if(recipient == null || recipient == this){
            send("ERROR|RECIPIENT_NOT_AVAILABLE");
            return ;
        }

        System.out.println("[" + clientId + " -> " + recipientId + "] " + text);

        try {
            recipient.send("FROM|" + clientId + "|" + text);
        } catch (IOException e) {
            send("ERROR|FORWARD_FAILED");
        }


    }
    
}
