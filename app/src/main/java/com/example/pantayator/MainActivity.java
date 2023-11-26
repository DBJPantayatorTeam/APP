package com.example.pantayator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button sendButton, historyButton, imageButton, logoutButton;
    private Button peopleButton;
    private WebSocketClient client;
    WebSocketManager webSocketManager = WebSocketManager.getInstance(); //getWebSocketClient
    static ArrayList<String> messageHistory = new ArrayList<String>();
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        if (!isConnected()) {
            connectToRPI(webSocketManager.ip);
            client = webSocketManager.getWebSocketClient();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText msgET = findViewById(R.id.missatgeEditText);

        sendButton = findViewById(R.id.enviarButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    return;
                }

                String message = msgET.getText().toString();
                try {
                    webSocketManager.getWebSocketClient().send(String.format("{\"type\":\"show\", \"value\": \"%s\"}",message));
                    addMessageToHistory(message);
                } catch (WebsocketNotConnectedException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error, no s'ha enviat el missatge", Toast.LENGTH_SHORT).show();
                }
            }
        });

        historyButton = findViewById(R.id.historialButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMessageHistory();
            }
        });

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSendImage();
            }
        });

        logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                goToLogin();
                client.close();
                webSocketManager.deleteWebSocketClient();
            }
        });

        peopleButton = findViewById(R.id.peopleButton);
        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send("{\"type\":\"usersList\"}");
            }
        });

        if (webSocketManager.getWebSocketClient() != null) {
            int color = Color.parseColor("#4CD964");
            ColorStateList colorStateList = ColorStateList.valueOf(color);
            sendButton.setBackgroundTintList(colorStateList);
            historyButton.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.VISIBLE);
        }


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
                            int color = Color.parseColor("#4CD964");
                            ColorStateList colorStateList = ColorStateList.valueOf(color);
                            sendButton.setBackgroundTintList(colorStateList);
                            historyButton.setVisibility(View.VISIBLE);
                            imageButton.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "S'ha establert la connexió", Toast.LENGTH_SHORT).show();
                        }
                    });
                    client.send("{\"type\":\"connection\", \"version\": \"app\"}");
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JSONObject msn = new JSONObject(message);
                        String type = msn.getString("type");

                        if (type.equals("usersOnline")) {
                            JSONArray usersArray = msn.getJSONArray("value");

                            List<String> userList = new ArrayList<>();

                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject userObject = usersArray.getJSONObject(i);
                                Iterator<String> keys = userObject.keys();

                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    JSONObject user = userObject.getJSONObject(key);

                                    String platform = user.getString("plataforma");
                                    String name = user.getString("usuario");

                                    userList.add("- " + name + " (" + platform + ")");
                                }
                            }

                            makeUsersDialog(userList);
                        }
                        if (type.equals("disconnection")) {
                            int numConnections = msn.getInt("value");
                            showToast("S'ha desconectat un usuari. Total de conexions: "+numConnections);
                        }
                        if (type.equals("connection")) {
                            int numConnections = msn.getInt("value");
                            showToast("S'ha conectat un usuari. Total de conexions: "+ numConnections);
                        }
                        if (type.equals("sendMessage")) {
                            String userName = msn.getString("value");
                            showToast("L'usuari "+ userName +" ha mandat un missatge");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from: " + getURI());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendButton.setBackgroundTintList(getColorStateList(R.color.colorRojo));
                            historyButton.setVisibility(View.INVISIBLE);
                            imageButton.setVisibility(View.INVISIBLE);
                            logoutButton.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "S'ha desconnectat", Toast.LENGTH_SHORT).show();
                        }
                    });
                    goToLogin();
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

    private void goToLogin(){
        Intent goLg = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goLg);
    }

    private boolean isConnected() {
        //return client != null && client.getConnection().isOpen();
        return webSocketManager.getWebSocketClient() != null && webSocketManager.getWebSocketClient().getConnection().isOpen();
    }

    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

        //Per quan rebi missatge
    private void makeUsersDialog(List<String> stringList){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String[] items = stringList.toArray(new String[0]);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Usuaris Conectats")
                        .setItems(items, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }
}