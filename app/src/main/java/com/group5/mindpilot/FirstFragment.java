package com.group5.mindpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;


import java.util.Calendar;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private static final String TAG = "FirstFragment";

    private TextView greetingTextView;
    private Button btnSignOut;
    private Button btnStartChat;
    private Button btnCrisis;
    private Button btnViewLog;
    private final TextView[] moodSelectors = new TextView[5];

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        greetingTextView = view.findViewById(R.id.textViewGreeting);
        btnSignOut = view.findViewById(R.id.btn_sign_out);
        btnStartChat = view.findViewById(R.id.buttonStartChat);
        btnCrisis = view.findViewById(R.id.buttonCrisis);
        btnViewLog = view.findViewById(R.id.btn_view_log);

        moodSelectors[0] = view.findViewById(R.id.mood_terrible); // 😭
        moodSelectors[1] = view.findViewById(R.id.mood_bad);      // 😞
        moodSelectors[2] = view.findViewById(R.id.mood_okay);     // 😐
        moodSelectors[3] = view.findViewById(R.id.mood_good);     // 😊
        moodSelectors[4] = view.findViewById(R.id.mood_great);    // 🤩


        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Could not get Firebase Auth", e);
            updateGreeting("Guest");
            mAuth = null;
        }

        if (mAuth != null) {
            loadUserNameAndGreeting();
        } else {
            updateGreeting("Guest");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAuth != null) {
            btnSignOut.setOnClickListener(v -> signOutAndRedirect());
        }

        btnStartChat.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity activity) {
                activity.setCurrentFragment(new SecondFragment());
                activity.selectBottomNavItem(R.id.chat);
            }
        });

        btnCrisis.setOnClickListener(v -> handleCrisisCall());

        btnViewLog.setOnClickListener(v -> handleViewLog());

        for (TextView moodSelector : moodSelectors) {
            moodSelector.setOnClickListener(this::handleMoodSelection);
        }
    }

    private void loadUserNameAndGreeting() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userName = "Friend";

            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                userName = currentUser.getDisplayName();
            } else if (currentUser.isAnonymous()) {
                userName = "Guest";
            }

            updateGreeting(userName);
        } else {
            updateGreeting("Guest");
        }
    }

    private void updateGreeting(String userName) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String timeOfDay;
        String emoji;

        if (hour >= 5 && hour < 12) {
            timeOfDay = "Good Morning";
            emoji = "☀️";
        } else if (hour >= 12 && hour < 18) {
            timeOfDay = "Good Afternoon";
            emoji = "🌤️";
        } else {
            timeOfDay = "Good Evening";
            emoji = "🌙";
        }

        String displayUserName = userName;
        if (userName.contains(" ")) {
            displayUserName = userName.substring(0, userName.indexOf(" "));
        }

        String fullGreeting = String.format(Locale.getDefault(), "%s, %s %s", timeOfDay, displayUserName, emoji);
        greetingTextView.setText(fullGreeting);
    }

    private void handleMoodSelection(View v) {

        String selectedEmoji = ((TextView) v).getText().toString();
        saveEmotionToFirestore(selectedEmoji);

        v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150).withEndAction(() -> {
            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150);
        });
    }

    private void handleViewLog() {
        if (getActivity() instanceof MainActivity activity) {
            activity.setCurrentFragment(new FourthFragment());
        }
    }

    private void saveEmotionToFirestore(String emotion) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || db == null) return;

        String userId = user.getUid();

        EmotionLog log = new EmotionLog(
                emotion,
                userId,
                Timestamp.now()
        );

        // /artifacts/{APP_ID}/users/{USER_ID}/emotions
        String APP_ID = "local-mindpilot-app";
        CollectionReference logCollection = db.collection("artifacts").document(APP_ID)
                .collection("users").document(userId)
                .collection("emotions");

        logCollection.add(log)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Emotion saved: " + emotion);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error logging emotion:", e);
                });
    }

    private void handleCrisisCall() {
        String url = "https://miasa.org.my/get-help/crisis-help/";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        startActivity(intent);
    }

    private void signOutAndRedirect() {
        if (mAuth != null) {
            mAuth.signOut();
        } else {
            Log.d(TAG, "No user to sign out");
        }
        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}