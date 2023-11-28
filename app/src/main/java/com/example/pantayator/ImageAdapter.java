package com.example.pantayator;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.java_websocket.client.WebSocketClient;

import java.io.ByteArrayOutputStream;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int[] mImageIds;

    WebSocketClient client = WebSocketManager.getInstance().getWebSocketClient();

    public ImageAdapter(Context context, int[] imageIds) {
        mContext = context;
        mImageIds = imageIds;
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // Si la vista no existe, inflarla
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageView = (ImageView) inflater.inflate(R.layout.item_image, parent, false);
        } else {
            imageView = (ImageView) convertView;
        }

        // Establecer la imagen para el elemento de la cuadrícula actual
        imageView.setImageResource(mImageIds[position]);

        // Establecer un clic en la imagen
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                Bitmap bitmap = imageView.getDrawingCache();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageBytes = stream.toByteArray();

                // Codificar los bytes en Base64
                String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                // Enviar la imagen codificada como Base64
                client.send(String.format("{\"type\":\"image\", \"value\": \"%s\"}", encodedImage));
            }
        });
        return imageView;
    }
}