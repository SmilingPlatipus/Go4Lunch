package com.example.go4lunch.fragments.restaurants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.go4lunch.R;

public class RestaurantsFragment extends Fragment
{

    private RestaurantsViewModel restaurantsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        restaurantsViewModel =
                ViewModelProviders.of(this).get(RestaurantsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_restaurants, container, false);
        final TextView textView = root.findViewById(R.id.text_restaurants);
        restaurantsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                Toast.makeText(getContext(),"Value of textView is "+ textView.getText(),Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}
