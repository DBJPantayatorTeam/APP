package com.example.pantayator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity {
    private WebSocketClient client;
    private CountDownLatch latch;
    private ProgressDialog progressDialog;

    private TextView usuariLogintextView;
    private TextView passwordTextView2;
    private EditText userEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button connectarLoginButton;
    private TextView ipLoginTextView;
    private EditText ipLoginEditText;
    public static String userName = "";

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

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                String username = userEditText.getText().toString();
                userName = userEditText.getText().toString();
                if (isConnected()){
                    client.send(String.format("{\"type\":\"login\", \"user\": \"%s\", \"password\": \"%s\"}", username, password));
                    WebSocketManager.userName = userEditText.getText().toString();
                    WebSocketManager.passwd = passwordEditText.getText().toString();
                } else {
                    Toast.makeText(LoginActivity.this, "No estás conectat", Toast.LENGTH_SHORT).show();
                }
            }
        });

        connectarLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipLoginEditText.getText().toString();
                latch = new CountDownLatch(1);
                connectToRPI(ip);

                progressDialog = ProgressDialog.show(LoginActivity.this, "Conectant", "Si us plau, espera...", true, false);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            latch.await(); // Espera hasta que se conecte o falle la conexión
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss(); // Cerramos el diálogo de progreso
                                    if (isConnected()) {
                                        conected();
                                        WebSocketManager.ip = ip;
                                    } else {
                                        Toast.makeText(LoginActivity.this, "IP no existente", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                    latch.countDown(); // La conexión se ha establecido con éxito

                }

                @Override
                public void onMessage(String message) {
                    // Manejar mensajes recibidos
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
                    latch.countDown(); // Hubo un fallo en la conexión
                }
            };
            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this, "Error: " + uri + " no es una direcció URI de WebSocket valida", Toast.LENGTH_SHORT).show();
            System.out.println("Error: " + uri + " no es una direcció URI de WebSocket valida");
        }
    }

    private void goToMain() {
        Intent goMH = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goMH);
        client.close();
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
                        Toast.makeText(LoginActivity.this, "Usuari o contrasenya incorrectes", Toast.LENGTH_SHORT).show();
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

    private boolean isConnected() {
        return client != null && client.getConnection().isOpen();
    }
}

