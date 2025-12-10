package com.group5.mindpilot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSentBy().equals(Message.SENT_BY_USER)) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getSentBy().equals(Message.SENT_BY_USER)) {
            holder.userBubble.setVisibility(View.VISIBLE);
            holder.botBubble.setVisibility(View.GONE);
            holder.userBubble.setText(message.getMessage());
        } else {
            holder.userBubble.setVisibility(View.GONE);
            holder.botBubble.setVisibility(View.VISIBLE);
            holder.botBubble.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView botBubble;
        TextView userBubble;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            botBubble = itemView.findViewById(R.id.bot_message_bubble);
            userBubble = itemView.findViewById(R.id.user_message_bubble);
        }
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
}