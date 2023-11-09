package com.example.pantayator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText msgET = findViewById(R.id.missatgeEditText);
        final EditText ipET = findViewById(R.id.ipEditText);

        final Button connectButton = findViewById(R.id.connectarButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = String.valueOf(ipET.getText());
                connectToRPI(ip);
            }
        });
    }

    public void connectToRPI(String ip) {

        int port = 8888;
        String uri = "ws://" + ip + ":" + port;
        WebSocketClient client;
        try {
            client = new WebSocketClient(new URI(uri), (Draft) new Draft_6455()) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to: " + getURI());
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Misatge: "+ message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from: " + getURI());
                }

                @Override
                public void onError(Exception ex) { ex.printStackTrace(); }
            };
            client.connect();
            client.send("{\"type\":\"connection\", \"version\": \"app\"}");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error: " + uri + " no és una direcció URI de WebSocket vàlida");
        }

    }
}