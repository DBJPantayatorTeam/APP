package com.example.pantayator;

import static com.example.pantayator.MainActivity.messageHistory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;

public class MessageHistoryActivity extends AppCompatActivity {

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

        ArrayList<String> reversedMessageHistory = reverseList(messageHistory);

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

    private void goToMain() {
        Intent goBack = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goBack);
    }
}
