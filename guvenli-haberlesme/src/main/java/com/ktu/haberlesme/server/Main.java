package com.ktu.haberlesme.server;

import java.io.IOException;

public class Main {
    
    public static void main(String[] args) {
        TcpServer server = new TcpServer(1881);

        try{
            server.start();
        } catch(IOException e){
            System.err.println("Server failed: " + e.getMessage());
        }

    }
}