package com.example.pantayator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private WebSocketClient client;
    static ArrayList<String> messageHistory = new ArrayList<String>();
    private Button historyButton;

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

        sendButton = findViewById(R.id.enviarButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWebSocketConnected()) {
                    return;
                }

                String message = msgET.getText().toString();
                try {
                    if (isWebSocketConnected()) {
                        client.send(String.format("{\"type\":\"show\", \"value\": \"%s\"}",message));
                        addMessageToHistory(message);
                    } else {
                        // La conexión no está establecida, muestra un mensaje al usuario.
                        Toast.makeText(MainActivity.this, "La conexión no está establecida", Toast.LENGTH_SHORT).show();
                    }
                } catch (WebsocketNotConnectedException e) {
                    e.printStackTrace();
                    // Maneja la excepción de conexión no establecida, por ejemplo, muestra un mensaje al usuario.
                    Toast.makeText(MainActivity.this, "Error: La conexión no está establecida", Toast.LENGTH_SHORT).show();
                }
            }
        });

        historyButton = findViewById(R.id.historialButton);
        historyButton.setVisibility(View.INVISIBLE);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMessageHistory();
            }
        });
    }

    private void addMessageToHistory(String msg){
        boolean repeated = false;
        for (String historicMsg : messageHistory) {
            if (historicMsg.equals(msg)) {
                repeated = true;
                break;
            }
        }

        if (!repeated){
            messageHistory.add(msg);
        }
    }

    public void connectToRPI(String ip) {
        int port = 8888;
        String uri = "ws://" + ip + ":" + port;

        try {
            client = new WebSocketClient(new URI(uri), (Draft) new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to: " + getURI());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendButton.setBackgroundTintList(getColorStateList(R.color.colorVerde));
                            historyButton.setVisibility(View.VISIBLE);
                        }
                    });
                    client.send("{\"type\":\"connection\", \"version\": \"app\"}");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Mensaje: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Desconectado de: " + getURI());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendButton.setBackgroundColor(Color.RED);
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error: " + uri + " no es una dirección URI de WebSocket válida");
        }
    }

    private void goToMessageHistory() {
        Intent goMH = new Intent(getApplicationContext(), MessageHistoryActivity.class);
        startActivity(goMH);
    }

    private boolean isWebSocketConnected() {
        return client != null && client.getConnection().isOpen();
    }
}
