package com.example.pantayator;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
}
