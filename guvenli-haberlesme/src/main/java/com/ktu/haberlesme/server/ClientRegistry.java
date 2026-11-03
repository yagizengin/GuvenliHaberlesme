package com.ktu.haberlesme.server;

import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public boolean register(String id, ClientHandler handler){
        return clients.putIfAbsent(id, handler) == null;
    }

    public ClientHandler find(String id){
        return clients.get(id);
    }

    public void remove(String id, ClientHandler handler){
        if(id != null) clients.remove(id, handler);
    }
}
