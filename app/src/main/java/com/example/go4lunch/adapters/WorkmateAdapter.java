package com.example.go4lunch.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.DetailRestaurantActivity;
import com.example.go4lunch.models.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurantList;
import static com.example.go4lunch.activities.MainActivity.optionsForWorkmatesEatingInThisRestaurant;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.example.go4lunch.fragments.map.MapFragment.RESTAURANT_INDEX;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<Workmate, WorkmateAdapter.WorkmateHolder>
{
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public WorkmateAdapter(Context context, @NonNull FirestoreRecyclerOptions<Workmate> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateHolder holder, int position, @NonNull Workmate model) {
        StringBuilder workmateString = new StringBuilder(model.getName() + " ");
        if (model.getChoice().compareTo("null") != 0) {
            workmateString.append(context.getString(R.string.workmate_choice));
            workmateString.append(" (");
            for (HashMap<String, String> currentRestaurant : nearbyRestaurantList) {
                if (currentRestaurant.get("place_name") != null)
                    if (model.getChoice().compareTo(currentRestaurant.get("place_id")) == 0)
                        workmateString.append(currentRestaurant.get("place_name"));
            }
            workmateString.append(")");
            holder.workmateName.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark2));
        }
        else{
            workmateString.append(context.getString(R.string.workmate_not_chosen));
            holder.workmateName.setTextColor(holder.workmateName.getHintTextColors());
        }
        holder.workmateName.setText(workmateString.toString());

        Glide.with(holder.workmateImage.getContext())
                .load(model.getImage())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.workmateImage);

        holder.workmateCardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                for (HashMap<String, String> currentRestaurant : nearbyRestaurantList){
                    if (currentRestaurant.get("place_name") != null)
                        if (model.getChoice().compareTo(currentRestaurant.get("place_id")) == 0){
                            Intent detailRestaurantActivity = new Intent(holder.workmateCardView.getContext(), DetailRestaurantActivity.class);
                            detailRestaurantActivity.putExtra(RESTAURANT_INDEX, nearbyRestaurantList.indexOf(currentRestaurant));

                            // Setting options for cloud firestore
                            Query query = workmatesReference.whereEqualTo("choice", currentRestaurant.get("place_id"));
                            // Recycler Options
                            optionsForWorkmatesEatingInThisRestaurant = new FirestoreRecyclerOptions.Builder<Workmate>()
                                    .setQuery(query,Workmate.class)
                                    .build();

                            startActivity(holder.workmateCardView.getContext(),detailRestaurantActivity,null);
                        }
                }
            }
        });
    }

    @NonNull
    @Override
    public WorkmateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_workmates_recyclerview, parent, false);
        return new WorkmateHolder(v);
    }

    class WorkmateHolder extends RecyclerView.ViewHolder{
        private ImageView workmateImage;
        private TextView workmateName;
        private CardView workmateCardView;

        public WorkmateHolder(@NonNull View itemView) {
            super(itemView);

            workmateImage = itemView.findViewById(R.id.cardview_workmate_pic);
            workmateName = itemView.findViewById(R.id.cardview_workmate_informations);
            workmateCardView = itemView.findViewById(R.id.cardview_workmate);
        }
    }

}
