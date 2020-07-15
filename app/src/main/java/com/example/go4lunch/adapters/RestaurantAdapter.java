package com.example.go4lunch.adapters;

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
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.go4lunch.activities.MainActivity.optionsForWorkmatesEatingInThisRestaurant;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.example.go4lunch.fragments.map.MapFragment.RESTAURANT_INDEX;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurantList;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder>
{
    List<Restaurant> restaurantList = new ArrayList<>();

    public RestaurantAdapter(List<Restaurant> restaurantList) {
        super();
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_restaurants_recyclerview, parent, false);
        return new RestaurantHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHolder holder, int position) {
        holder.name.setText(restaurantList.get(position).getName());
        holder.address.setText(restaurantList.get(position).getAddress());

        if (restaurantList.get(position).isOpenedNow())
            holder.openingHour.setText(R.string.restaurant_status_opened);
        else
            holder.openingHour.setText(R.string.restaurant_status_closed);

        int proximity = (int) restaurantList.get(position).getDistanceFromUser();
        holder.proximity.setText(String.valueOf(proximity) + " m");
        holder.workmateNumber.setText(String.valueOf(restaurantList.get(position).getWorkmatesCount()));

        if (restaurantList.get(position).getRating() <= 2){
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.GONE);
            holder.star3.setVisibility(View.GONE);
        }
        else {
            if (restaurantList.get(position).getRating() <= 3.5) {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.VISIBLE);
                holder.star3.setVisibility(View.GONE);
            } else {
                holder.star1.setVisibility(View.VISIBLE);
                holder.star2.setVisibility(View.VISIBLE);
                holder.star3.setVisibility(View.VISIBLE);
            }
        }
    if (restaurantList.get(position).getImageUrl().compareTo("null") != 0) {
        Glide.with(holder.restaurantPic.getContext())
                .load(restaurantList.get(position).getImageUrl())
                .apply(new RequestOptions().override(70, 70))
                .into(holder.restaurantPic);
    }
    else {
        Glide.with(holder.restaurantPic.getContext())
                .load("https://www.recia.fr/wp-content/uploads/2019/09/no_image.png")
                .apply(new RequestOptions().override(70, 70))
                .into(holder.restaurantPic);
    }
        holder.cardView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                for (HashMap<String, String> currentRestaurant : nearbyRestaurantList){
                    if (currentRestaurant.get("place_name") != null)
                        if (holder.name.getText().toString().compareTo(currentRestaurant.get("place_name")) == 0){
                            Intent detailRestaurantActivity = new Intent(holder.cardView.getContext(), DetailRestaurantActivity.class);
                            detailRestaurantActivity.putExtra(RESTAURANT_INDEX, nearbyRestaurantList.indexOf(currentRestaurant));

                            // Setting options for cloud firestore
                            Query query = workmatesReference.whereEqualTo("choice", currentRestaurant.get("place_id"));
                            // Recycler Options
                            optionsForWorkmatesEatingInThisRestaurant = new FirestoreRecyclerOptions.Builder<Workmate>()
                                    .setQuery(query,Workmate.class)
                                    .build();

                            startActivity(holder.cardView.getContext(),detailRestaurantActivity,null);
                        }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    public class RestaurantHolder extends RecyclerView.ViewHolder
    {
        private TextView name, address, openingHour, proximity, workmateNumber;
        private ImageView star1, star2, star3, restaurantPic;
        private CardView cardView;

        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cardview_restaurant_name);
            address = itemView.findViewById(R.id.cardview_restaurant_address);
            openingHour = itemView.findViewById(R.id.cardview_restaurant_opening_hour);
            proximity = itemView.findViewById(R.id.cardview_restaurant_proximity);
            workmateNumber = itemView.findViewById(R.id.cardview_workmate_number);

            star1 = itemView.findViewById(R.id.cardview_restaurant_star1);
            star2 = itemView.findViewById(R.id.cardview_restaurant_star2);
            star3 = itemView.findViewById(R.id.cardview_restaurant_star3);
            restaurantPic = itemView.findViewById(R.id.cardview_restaurant_pic);

            cardView = itemView.findViewById(R.id.cardview_restaurant);
        }

    }
}
