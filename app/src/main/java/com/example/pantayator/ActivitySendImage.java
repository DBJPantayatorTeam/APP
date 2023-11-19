package com.example.pantayator;

import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySendImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        // Agrega tus recursos de imágenes al array
        int[] imageIds = {R.drawable.stalker,
                R.drawable.stalker,
                R.drawable.stalker,
                R.drawable.stalker1,
                R.drawable.stalker1};

        // Configurar el adaptador con el array de imágenes
        ImageAdapter adapter = new ImageAdapter(this, imageIds);

        // Obtener el GridView y establecer el adaptador
        GridView gridView = findViewById(R.id.gridView);
        gridView.setAdapter(adapter);
    }
}
