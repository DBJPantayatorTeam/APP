package com.example.pantayator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {
    private EditText userEditText;
    private EditText passwordEditText;
    private WebSocketClient client;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        connectToRPI();

        final EditText pswET = findViewById(R.id.passwordEditText);
        final EditText usrET = findViewById(R.id.userEditText);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (!isConnected()){
                    return;
                }

                String password = pswET.getText().toString();
                String username = usrET.getText().toString();

                try{
                    client.send(String.format("{\"type\":\"login\", \"user\": \"%s\", \"password\": \"%s\"}", username, password));
                } catch (WebsocketNotConnectedException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error, no s'ha enviat el missatge", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void connectToRPI() {
        int port = 8888;
        String ip = "192.168.1.112";
        String uri = "ws://" + ip + ":" + port;

        try {
            client = new WebSocketClient(new URI(uri), (Draft) new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to: " + getURI());
                    //client.send("{\"type\":\"connection\", \"version\": \"app\"}");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Message: " + message);
                    try {
                        JSONObject json = new JSONObject(message);
                        String messageType = json.getString("type");

                        switch (messageType) {
                            case "login":
                                handleLoginResponse(json);
                                break;
                            case "otherMessageType":
                                // Realizar acciones para otro tipo de mensaje
                                break;
                            // Agrega más casos según los tipos de mensajes que esperes
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from: " + getURI());

                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error: " + uri + " no es una dirección URI de WebSocket válida", Toast.LENGTH_SHORT).show();
            System.out.println("Error: " + uri + " no es una dirección URI de WebSocket válida");
        }
    }

    private void goToMain() {
        //client.close();
        Intent goMH = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goMH);
    }
    private boolean isConnected() {
        return client != null && client.getConnection().isOpen();
    }

    private void handleLoginResponse(JSONObject json) {
        try {
            boolean loginValue = json.getBoolean("value");
            if (loginValue) {
                // Realizar acciones para el caso de inicio de sesión exitoso
                client.close();
                goToMain();
            } else {
                // Realizar acciones para el caso de inicio de sesión fallido
                Toast.makeText(LoginActivity.this, "Negativo", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}