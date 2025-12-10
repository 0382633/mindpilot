package com.group5.mindpilot;
public class Message {

    public static final String SENT_BY_USER = "user";
    public static final String SENT_BY_BOT = "bot";

    private final String message;
    private final String sentBy;

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public String getSentBy() {
        return sentBy;
    }
}