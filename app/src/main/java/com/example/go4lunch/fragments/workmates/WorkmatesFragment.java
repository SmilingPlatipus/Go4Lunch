package com.example.go4lunch.fragments.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.adapters.WorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import static com.example.go4lunch.activities.MainActivity.optionsForWorkmatesRecyclerView;

public class WorkmatesFragment extends Fragment
{
    // Workmates RecyclerView
    private RecyclerView mWorkmates;
    private FirestoreRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_workmates, container, false);


        // Creating adapter for RecyclerView
        adapter = new WorkmateAdapter(root.getContext(), optionsForWorkmatesRecyclerView);

        // Initializing Recyclerview
        mWorkmates = (RecyclerView) root.findViewById(R.id.workmates_recyclerview);
        mWorkmates.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorkmates.setAdapter(adapter);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

}
