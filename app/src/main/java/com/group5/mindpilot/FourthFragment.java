package com.group5.mindpilot;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FourthFragment extends Fragment {

    private static final String TAG = "FourthFragment";
    private RecyclerView recyclerView;
    private EmotionLogAdapter adapter;
    private List<EmotionLog> emotionList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private final String APP_ID_PLACEHOLDER = "local-mindpilot-app";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        recyclerView = view.findViewById(R.id.log_recycler_view);
        emotionList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        adapter = new EmotionLogAdapter(emotionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadEmotionLogs();

        return view;
    }

    private void loadEmotionLogs() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Please sign in to view your logs.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();

        CollectionReference logCollection = db.collection("artifacts").document(APP_ID_PLACEHOLDER)
                .collection("users").document(userId)
                .collection("emotions");

        logCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    emotionList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        EmotionLog log = document.toObject(EmotionLog.class);
                        emotionList.add(log);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + emotionList.size() + " emotion logs.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading logs:", e);
                    Toast.makeText(getContext(), "Failed to load log history.", Toast.LENGTH_LONG).show();
                });
    }
}