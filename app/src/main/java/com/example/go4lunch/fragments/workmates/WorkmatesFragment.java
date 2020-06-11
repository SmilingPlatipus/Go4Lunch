package com.example.go4lunch.fragments.workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import static com.example.go4lunch.activities.MainActivity.options;

public class WorkmatesFragment extends Fragment
{

    private WorkmatesViewModel workmatesViewModel;

    // Workmates RecyclerView
    private RecyclerView mWorkmates;
    private FirestoreRecyclerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        workmatesViewModel =
                ViewModelProviders.of(this).get(WorkmatesViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_workmates, container, false);


        // Creating adapter for RecyclerView
        adapter = new FirestoreRecyclerAdapter<Workmate, WorkmateViewHolder>(options)
        {
            @NonNull
            @Override
            public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recyclerview, parent, false);
                return new WorkmateViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position, @NonNull Workmate model) {
                holder.workmateName.setText(model.getName() + " " + getString(R.string.workmate_not_chosen));

                Glide.with(root.getContext())
                        .load(model.getImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(holder.workmateImage);


            }
        };
        // Initializing Recyclerview
        mWorkmates = (RecyclerView) root;
        mWorkmates.setAdapter(adapter);
        mWorkmates.setLayoutManager(new LinearLayoutManager(getActivity()));



        workmatesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private class WorkmateViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView workmateImage;
        private TextView workmateName;

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);

            workmateImage = itemView.findViewById(R.id.cardview_workmate_pic);
            workmateName = itemView.findViewById(R.id.cardview_workmate_name);
        }
    }
}
