package com.example.pantayator;

import org.java_websocket.client.WebSocketClient;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocketClient webSocketClient;

    private WebSocketManager() {
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient client) {
        this.webSocketClient = client;
    }

    public void deleteWebSocketClient(){
        setWebSocketClient(null);
    }
}
