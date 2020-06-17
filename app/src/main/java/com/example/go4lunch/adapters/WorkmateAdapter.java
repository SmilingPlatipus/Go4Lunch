package com.example.go4lunch.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<Workmate, WorkmateAdapter.WorkmateHolder>
{
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<Workmate> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateHolder holder, int position, @NonNull Workmate model) {
        String workmateString = model.getName() + " " + holder.workmateImage.getContext().getString(R.string.workmate_not_chosen);
        holder.workmateName.setText(workmateString);
        holder.workmateName.setTextColor(holder.workmateName.getHintTextColors());

        Glide.with(holder.workmateImage.getContext())
                .load(model.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.workmateImage);
    }

    @NonNull
    @Override
    public WorkmateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recyclerview,parent,false);
        return new WorkmateHolder(v);
    }

    class WorkmateHolder extends RecyclerView.ViewHolder{
        private ImageView workmateImage;
        private TextView workmateName;

        public WorkmateHolder(@NonNull View itemView) {
            super(itemView);

            workmateImage = itemView.findViewById(R.id.cardview_workmate_pic);
            workmateName = itemView.findViewById(R.id.cardview_workmate_informations);
        }
    }

}
