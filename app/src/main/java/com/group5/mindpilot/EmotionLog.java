package com.group5.mindpilot;

import com.google.firebase.Timestamp;

public class EmotionLog {
    private String emotion;
    private String userId;
    private Timestamp timestamp;

    public EmotionLog() {

    }
    public EmotionLog(String emotion, String userId, Timestamp timestamp) {
        this.emotion = emotion;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getEmotion() {
        return emotion;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}