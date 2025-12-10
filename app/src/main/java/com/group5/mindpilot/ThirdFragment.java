package com.group5.mindpilot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ThirdFragment extends Fragment {

    private RecyclerView recyclerView;
    private ResourceAdapter adapter;
    private List<ResourceItem> dataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        recyclerView = view.findViewById(R.id.resources_recycler_view);

        dataList = createMockResourceData();

        adapter = new ResourceAdapter(getContext(), dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
    private List<ResourceItem> createMockResourceData() {
        List<ResourceItem> list = new ArrayList<>();

        list.add(new ResourceItem(
                "Combating Academic Burnout",
                "Identify the signs of stress and learn techniques for time management and pacing.",
                R.drawable.book
        ));

        list.add(new ResourceItem(
                "Better Sleep, Better Mind",
                "A comprehensive guide to improving sleep hygiene for mental well-being.",
                R.drawable.sleep
        ));

        list.add(new ResourceItem(
                "5-4-3-2-1 Grounding Technique",
                "A quick exercise to regain control during moments of high anxiety or panic.",
                R.drawable.star
        ));

        list.add(new ResourceItem(
                "Navigating Social Media Stress",
                "Tips on reducing comparison and setting digital boundaries.",
                R.drawable.group
        ));

        list.add(new ResourceItem(
                "National Crisis Hotline Directory",
                "Find immediate, free, and confidential support lines in your region.",
                R.drawable.phone
        ));

        return list;
    }
}