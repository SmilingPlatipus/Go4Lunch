package com.example.go4lunch.fragments.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.adapters.WorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import static com.example.go4lunch.activities.MainActivity.options;

public class WorkmatesFragment extends Fragment
{

    private WorkmatesViewModel workmatesViewModel;

    // Workmates RecyclerView
    private RecyclerView mWorkmates;
    private FirestoreRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workmatesViewModel = new ViewModelProvider(this).get(WorkmatesViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_workmates, container, false);


        // Creating adapter for RecyclerView
        adapter = new WorkmateAdapter(options);

        // Initializing Recyclerview
        mWorkmates = (RecyclerView) root.findViewById(R.id.workmates_recyclerview);
        mWorkmates.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorkmates.setAdapter(adapter);



        workmatesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
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
