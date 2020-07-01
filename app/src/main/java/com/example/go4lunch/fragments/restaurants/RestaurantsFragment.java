package com.example.go4lunch.fragments.restaurants;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.adapters.RestaurantAdapter;
import static com.example.go4lunch.fragments.map.MapFragment.nearbyRestaurant;

public class RestaurantsFragment extends Fragment
{
    private RecyclerView restaurantRecyclerview;
    private RestaurantAdapter restaurantAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_restaurants, container, false);
        Context context = root.getContext();
        restaurantRecyclerview = (RecyclerView) root;
        restaurantRecyclerview.setLayoutManager(new LinearLayoutManager(context));

        restaurantAdapter = new RestaurantAdapter(nearbyRestaurant);
        restaurantRecyclerview.setAdapter(restaurantAdapter);
        return root;
    }
}
