package com.rivange.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rivange.app.R;
import com.rivange.app.models.ChatRoom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(ChatRoom chatRoom);
    }

    private final List<ChatRoom> chats = new ArrayList<>();
    private final OnChatClickListener listener;

    public ChatAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ChatRoom> newList) {
        chats.clear();
        if (newList != null) {
            chats.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatRoom room = chats.get(position);

        holder.tvChatName.setText(room.getName());
        holder.tvChatLastMessage.setText(room.getLastMessage());

        if (room.getLastMessageAt() > 0) {
            String formattedDate = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                    .format(new Date(room.getLastMessageAt()));
            holder.tvChatDate.setText(formattedDate);
        } else {
            holder.tvChatDate.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView tvChatName;
        TextView tvChatLastMessage;
        TextView tvChatDate;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvChatName = itemView.findViewById(R.id.tvChatName);
            tvChatLastMessage = itemView.findViewById(R.id.tvChatLastMessage);
            tvChatDate = itemView.findViewById(R.id.tvChatDate);
        }
    }
}