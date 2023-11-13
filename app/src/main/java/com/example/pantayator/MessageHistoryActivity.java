package com.example.pantayator;

import static com.example.pantayator.MainActivity.messageHistory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class MessageHistoryActivity extends AppCompatActivity {
    private ArrayList<String> reversedMessageHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_history);

        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        final Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

        reversedMessageHistory = reverseList(messageHistory);

        // Utiliza un RecyclerView en lugar de un ListView
        RecyclerView recyclerView = findViewById(R.id.lastMessagesLayout);

        // Configura el LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Utiliza un adaptador personalizado para el RecyclerView
        MessageAdapter adapter = new MessageAdapter(reversedMessageHistory);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<String> reverseList(ArrayList<String> originalList) {
        ArrayList<String> reverseList = new ArrayList<>(originalList);
        Collections.reverse(reverseList);
        return reverseList;
    }

    private void saveFile() {
        String fileName = "messageFile.json";
        try {
            JSONArray jsonArray = new JSONArray(reversedMessageHistory);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "messageList");
            jsonObject.put("value", jsonArray);

            String jsonString = jsonObject.toString();
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Toast.makeText(MessageHistoryActivity.this, "Fitxer desat correctament", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMain() {
        Intent goBack = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goBack);
    }
}
