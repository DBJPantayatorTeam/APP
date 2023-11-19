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
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button sendButton;
    private Button connectButton;
    private WebSocketClient client;
    WebSocketManager webSocketManager = WebSocketManager.getInstance(); //getWebSocketClient
    static ArrayList<String> messageHistory = new ArrayList<String>();
    private Button historyButton;
    private Button imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText msgET = findViewById(R.id.missatgeEditText);
        final EditText ipET = findViewById(R.id.ipEditText);

        connectButton = findViewById(R.id.connectarButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    String ip = String.valueOf(ipET.getText());
                    connectToRPI(ip);
                } else {
                    //client.close();
                    webSocketManager.getWebSocketClient().close();
                    webSocketManager.deleteWebSocketClient();
                }

            }
        });

        sendButton = findViewById(R.id.enviarButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    return;
                }

                String message = msgET.getText().toString();
                try {
                    client.send(String.format("{\"type\":\"show\", \"value\": \"%s\"}",message));
                    addMessageToHistory(message);
                } catch (WebsocketNotConnectedException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error, no s'ha enviat el missatge", Toast.LENGTH_SHORT).show();
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

        imageButton = findViewById(R.id.imageButton);
        imageButton.setVisibility(View.INVISIBLE);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSendImage();
            }
        });

        if (webSocketManager.getWebSocketClient() != null) {
            client = webSocketManager.getWebSocketClient();
            connectButton.setBackgroundTintList(getColorStateList(R.color.colorRojo));
            connectButton.setText("DESCONNECTAR");
            sendButton.setBackgroundTintList(getColorStateList(R.color.colorVerde));
            historyButton.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "S'ha establert la connexió", Toast.LENGTH_SHORT).show();
        };
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
                            connectButton.setBackgroundTintList(getColorStateList(R.color.colorRojo));
                            connectButton.setText("DESCONNECTAR");
                            sendButton.setBackgroundTintList(getColorStateList(R.color.colorVerde));
                            historyButton.setVisibility(View.VISIBLE);
                            imageButton.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "S'ha establert la connexió", Toast.LENGTH_SHORT).show();
                        }
                    });
                    client.send("{\"type\":\"connection\", \"version\": \"app\"}");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Message: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from: " + getURI());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connectButton.setBackgroundTintList(getColorStateList(R.color.colorAzul));
                            connectButton.setText("CONNECTAR");
                            sendButton.setBackgroundColor(Color.RED);
                            historyButton.setVisibility(View.INVISIBLE);
                            imageButton.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "S'ha desconnectat", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            WebSocketManager.getInstance().setWebSocketClient(client);
            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error: " + uri + " no es una dirección URI de WebSocket válida", Toast.LENGTH_SHORT).show();
            System.out.println("Error: " + uri + " no es una dirección URI de WebSocket válida");
        }
    }

    private void goToMessageHistory() {
        Intent goMH = new Intent(getApplicationContext(), MessageHistoryActivity.class);
        startActivity(goMH);
    }

    private void goToSendImage(){
        Intent goSI = new Intent(getApplicationContext(), ActivitySendImage.class);
        startActivity(goSI);
    }

    private boolean isConnected() {
        //return client != null && client.getConnection().isOpen();
        return webSocketManager.getWebSocketClient() != null && webSocketManager.getWebSocketClient().getConnection().isOpen();
    }
}