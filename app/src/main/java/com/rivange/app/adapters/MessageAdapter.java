package com.rivange.app.adapters;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.rivange.app.R;
import com.rivange.app.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();

    public void submitList(List<ChatMessage> newList) {
        messages.clear();
        if (newList != null) {
            messages.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout) android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        holder.tvMessageSender.setText(message.getSenderName());
        holder.tvMessageText.setText(message.getText());

        String currentUid = FirebaseAuth.getInstance().getUid();
        boolean isMine = currentUid != null && currentUid.equals(message.getSenderId());

        holder.containerMessage.setGravity(isMine ? Gravity.END : Gravity.START);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout containerMessage;
        TextView tvMessageSender;
        TextView tvMessageText;

        public MessageViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);

            containerMessage = itemView.findViewById(R.id.containerMessage);
            tvMessageSender = itemView.findViewById(R.id.tvMessageSender);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
        }
    }
}
