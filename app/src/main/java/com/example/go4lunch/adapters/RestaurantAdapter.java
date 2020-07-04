package com.example.go4lunch.adapters;

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
import com.example.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

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
        holder.proximity.setText(String.valueOf(proximity) + " mètres");
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

        Glide.with(holder.restaurantPic.getContext())
                .load(restaurantList.get(position).getImageUrl())
                .apply(new RequestOptions().override(70, 70))
                .into(holder.restaurantPic);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class RestaurantHolder extends RecyclerView.ViewHolder
    {
        private TextView name, address, openingHour, proximity, workmateNumber;
        private ImageView star1, star2, star3, restaurantPic;

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
        }
    }
}
