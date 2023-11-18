package com.example.pantayator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<String> messages;

    public MessageAdapter(ArrayList<String> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    // En tu adaptador
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            String message = messages.get(position);
            holder.messageTextView.setText(message);

            int backgroundColor = position % 2 == 0 ? R.color.colorBlanco : R.color.colorGris;
            holder.backgroundView.setBackgroundResource(backgroundColor);
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    showConfirmationDialog(holder.itemView.getContext(), message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        View backgroundView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            backgroundView = itemView.findViewById(R.id.backgroundView);
        }
    }

    private void showConfirmationDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmación de reenvío")
                .setMessage("¿Estás seguro de reenviar este mensaje?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Aquí puedes implementar el código para enviar el mensaje al servidor
                        // Puedes llamar a un método en MainActivity para manejar el envío
                        //sendToServer(message);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada si se selecciona "No"
                    }
                })
                .show();
    }
}
