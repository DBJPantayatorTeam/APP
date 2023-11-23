package com.example.pantayator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private WebSocketClient client;

    // Login
    private TextView usuariLogintextView;
    private TextView passwordTextView2;
    private EditText userEditText;
    private EditText passwordEditText;
    private Button loginButton;

    // Connect
    private Button connectarLoginButton;
    private TextView ipLoginTextView;
    private EditText ipLoginEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuariLogintextView = findViewById(R.id.usuariLogintextView);
        passwordTextView2 = findViewById(R.id.passwordTextView2);
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        connectarLoginButton = findViewById(R.id.connectarLoginButton);
        ipLoginTextView = findViewById(R.id.ipLoginTextView);
        ipLoginEditText = findViewById(R.id.ipLoginEditText);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                String username = userEditText.getText().toString();
                if (isConnected()){
                    client.send(String.format("{\"type\":\"login\", \"user\": \"%s\", \"password\": \"%s\"}", username, password));
                }else{
                    Toast.makeText(LoginActivity.this, "No estás conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        connectarLoginButton = findViewById(R.id.connectarLoginButton);
        connectarLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipLoginEditText.getText().toString();
                connectToRPI(ip);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (isConnected()){
                    conected();
                }else{
                    Toast.makeText(LoginActivity.this, "IP no existent", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void connectToRPI(String ip) {
        int port = 8888;
        String uri = "ws://" + ip + ":" + port;

        try {
            client = new WebSocketClient(new URI(uri), (Draft) new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to: " + getURI());
                    client.send("{\"type\":\"connection\", \"version\": \"app\"}");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Message: " + message);
                    try {
                        JSONObject json = new JSONObject(message);
                        String messageType = json.getString("type");

                        if (messageType.equalsIgnoreCase("login")){
                            handleLoginResponse(json);
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
        Intent goMH = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goMH);
    }

    private void handleLoginResponse(JSONObject json) {
        try {
            boolean loginValue = json.getBoolean("value");
            if (loginValue) {
                goToMain();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Usuari o contrasenya incorrecte", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void conected(){
        connectarLoginButton.setVisibility(View.INVISIBLE);
        ipLoginTextView.setVisibility(View.INVISIBLE);
        ipLoginEditText.setVisibility(View.INVISIBLE);

        //Login
        loginButton.setVisibility(View.VISIBLE);
        usuariLogintextView.setVisibility(View.VISIBLE);
        passwordTextView2.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        userEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
    }

    private void desconected(){
        connectarLoginButton.setVisibility(View.VISIBLE);
        ipLoginTextView.setVisibility(View.VISIBLE);
        ipLoginEditText.setVisibility(View.VISIBLE);

        //Login
        loginButton.setVisibility(View.INVISIBLE);
        usuariLogintextView.setVisibility(View.INVISIBLE);
        passwordTextView2.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        userEditText.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
    }

    private boolean isConnected() {
        return client != null && client.getConnection().isOpen();
    }
}