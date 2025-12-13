package com.group5.mindpilot;

import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.Chat;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.ChatFutures;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatViewModel extends ViewModel {

    private static final String TAG = "ChatViewModel";

    private List<Message> messageList = new ArrayList<>();
    private ChatFutures chatSessionFutures;
    private Executor executor;

    private static final String SYSTEM_PROMPT =
            "You are MindPilot, an empathetic mental health companion for young adults (U30). " +
                    "Your primary role is to listen, validate feelings, and provide non-diagnostic, informational support based on established wellness principles. " +
                    "You MUST NOT give clinical advice or diagnosis. If the user mentions self-harm or crisis, you must immediately reply by emphasizing the need for professional help and suggesting they call an external hotline. " +
                    "Keep responses supportive, brief, and actionable, maintaining a compassionate, non-judgmental tone.";

    public ChatViewModel() {
        initializeChatSession();
    }

    private void initializeChatSession() {
        Log.d(TAG, "Initializing new Chat Session...");
        try {
            executor = Executors.newSingleThreadExecutor();

            GenerativeModel baseModel = FirebaseAI.getInstance(GenerativeBackend.googleAI()).generativeModel(
                    "gemini-2.5-flash", null, null, null, null, new Content.Builder().addText(SYSTEM_PROMPT).build());

            Content.Builder systemContentBuilder = new Content.Builder();
            systemContentBuilder.setRole("user");
            Content systemContent = systemContentBuilder.build();

            List<Content> history = List.of(systemContent);
            Chat chatSession = baseModel.startChat(history);

            chatSessionFutures = ChatFutures.from(chatSession);

            if (messageList.isEmpty()) {
                messageList.add(new Message("Welcome to MindPilot. I'm here to listen and provide helpful information. What's on your mind today?", Message.SENT_BY_BOT));
            }

        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase Gemini SDK.", e);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "Cleared Chat ViewModel");
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public ChatFutures getChatSessionFutures() {
        return chatSessionFutures;
    }

    public Executor getExecutor() {
        return executor;
    }
}