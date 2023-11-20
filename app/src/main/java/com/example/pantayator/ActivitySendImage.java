package com.example.pantayator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySendImage extends AppCompatActivity {
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                goToMain();
            }
        });

        // Agrega tus recursos de imágenes al array
        int[] imageIds = {R.drawable.stalker,
                R.drawable.stalker,
                R.drawable.stalker,
                R.drawable.stalker1,
                R.drawable.stalker1,
                R.drawable.stalker,
                R.drawable.stalker1,
                R.drawable.stalker,
                R.drawable.stalker1,
                R.drawable.stalker
        };

        // Configurar el adaptador con el array de imágenes
        ImageAdapter adapter = new ImageAdapter(this, imageIds);

        // Obtener el GridView y establecer el adaptador
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
    }
    private void goToMain() {
        Intent goBack = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(goBack);
    }
}
