package com.group5.mindpilot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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



public class SecondFragment extends Fragment {

    private static final String TAG = "SecondFragment";

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    public List<Message> messageList;

    private Executor executor;
    private ChatFutures chatSessionFutures;

    private static final String SYSTEM_PROMPT =
            "You are MindPilot, an empathetic mental health companion for young adults (U30). " +
                    "Your primary role is to listen, validate feelings, and provide non-diagnostic, informational support based on established wellness principles. " +
                    "You MUST NOT give clinical advice or diagnosis. If the user mentions self-harm or crisis, you must immediately reply by emphasizing the need for professional help and suggesting they call an external hotline. " +
                    "Keep responses supportive, brief, and actionable, maintaining a compassionate, non-judgmental tone.";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_messages);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(chatAdapter);

        addToChat("Welcome to MindPilot. I'm here to listen and provide helpful information. What's on your mind today?", Message.SENT_BY_BOT);

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

        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase Gemini SDK.", e);
            addToChat("Error: AI service failed to initialize.", Message.SENT_BY_BOT);
        }

        sendButton.setOnClickListener(v -> {
            String question = messageEditText.getText().toString().trim();
            if (!question.isEmpty()) {
                addToChat(question, Message.SENT_BY_USER);
                messageEditText.setText("");
                callGeminiAPI(question);
            }
        });

        return view;
    }

    private void addToChat(String message, String sentBy) {
        chatAdapter.addMessage(new Message(message, sentBy));
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
    }

    private void callGeminiAPI(String question) {
        if (chatSessionFutures == null) {
            addToChat("Error: AI Model is unavailable.", Message.SENT_BY_BOT);
            return;
        }

        if (question.toLowerCase().contains("self harm") || question.toLowerCase().contains("kill myself")) {
            String crisisResponse = "Thank you for reaching out. It sounds like you are in crisis. Please know that help is available immediately. I strongly recommend you call a local crisis hotline or emergency services right now.";
            addToChat(crisisResponse, Message.SENT_BY_BOT);
            return;
        }

        Message loadingMessage = new Message("MindPilot is thinking...", Message.SENT_BY_BOT);
        chatAdapter.addMessage(loadingMessage);
        recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

        Content.Builder promptBuilder = new Content.Builder()
                .addText(question)
                .setRole("user");
        Content prompt = promptBuilder.build();

        ListenableFuture<GenerateContentResponse> responseFuture = chatSessionFutures.sendMessage(prompt);

        Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(@Nullable GenerateContentResponse response) {
                requireActivity().runOnUiThread(() -> {
                    removeLastMessage();

                    String aiResponse = (response != null) ? response.getText() : null;

                    if (aiResponse != null && !aiResponse.isEmpty()) {
                        addToChat(aiResponse, Message.SENT_BY_BOT);
                    } else {
                        addToChat("Sorry, I received an empty response or content block.", Message.SENT_BY_BOT);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    removeLastMessage();

                    Log.e(TAG, "API failure:", t);
                    addToChat("API Error: Could not connect or model blocked content. (" + t.getLocalizedMessage() + ")", Message.SENT_BY_BOT);
                });
            }
        }, executor);
    }

    private void removeLastMessage() {
        if (!messageList.isEmpty() && getContext() != null) {
            messageList.remove(messageList.size() - 1);
            chatAdapter.notifyItemRemoved(messageList.size());
        }
    }
}