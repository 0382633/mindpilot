package com.group5.mindpilot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EmotionLogAdapter extends RecyclerView.Adapter<EmotionLogAdapter.LogViewHolder> {

    private final List<EmotionLog> emotionList;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());


    public EmotionLogAdapter(List<EmotionLog> emotionList) {
        this.emotionList = emotionList;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emotion_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        EmotionLog log = emotionList.get(position);

        holder.emotionText.setText(log.getEmotion());

        if (log.getTimestamp() != null) {
            holder.timeText.setText("Logged at " + timeFormat.format(log.getTimestamp().toDate()));
            holder.dateText.setText(dateFormat.format(log.getTimestamp().toDate()));
        } else {
            holder.timeText.setText("Time unknown");
            holder.dateText.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return emotionList.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView emotionText;
        TextView timeText;
        TextView dateText;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            emotionText = itemView.findViewById(R.id.text_emotion);
            timeText = itemView.findViewById(R.id.text_time);
            dateText = itemView.findViewById(R.id.text_date);
        }
    }
}