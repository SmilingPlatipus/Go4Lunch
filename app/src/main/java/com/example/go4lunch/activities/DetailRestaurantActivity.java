package com.example.go4lunch.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;

import java.util.HashMap;

import static com.example.go4lunch.fragments.map.MapFragment.RESTAURANT_INDEX;
import static com.example.go4lunch.fragments.map.MapFragment.nearbyRestaurantList;

public class DetailRestaurantActivity extends AppCompatActivity
{
    private ImageView detailRestaurantPic, detailRestaurantStar1, detailRestaurantStar2, detailRestaurantStar3;
    private TextView detailRestaurantName, detailRestaurantAddress;
    private Button detailRestaurantCall, detailRestaurantWebsite, detailRestaurantLike;
    static Intent intent = new Intent();
    int restaurantIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_restaurant);

        detailRestaurantPic = findViewById(R.id.detail_restaurant_pic);
        detailRestaurantStar1 = findViewById(R.id.detail_restaurant_star1);
        detailRestaurantStar2 = findViewById(R.id.detail_restaurant_star2);
        detailRestaurantStar3 = findViewById(R.id.detail_restaurant_star3);
        detailRestaurantName = findViewById(R.id.detail_restaurant_name);
        detailRestaurantAddress = findViewById(R.id.detail_restaurant_address);
        detailRestaurantCall = findViewById(R.id.detail_restaurant_phone);
        detailRestaurantWebsite = findViewById(R.id.detail_restaurant_website);
        detailRestaurantLike = findViewById(R.id.detail_restaurant_like);

        initDetailRestaurantActivity();
        initDetailRestaurantWidgets();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void initDetailRestaurantActivity() {
        intent = getIntent();
        restaurantIndex = intent.getIntExtra(RESTAURANT_INDEX, 0);
    }

    private void initDetailRestaurantWidgets() {
        HashMap<String, String> currentRestaurant;
        currentRestaurant = nearbyRestaurantList.get(restaurantIndex);
        Glide.with(this)
                .load(currentRestaurant.get("photo_url"))
                .apply(new RequestOptions().override(70, 70))
                .into(detailRestaurantPic);

        if (currentRestaurant.get("rating") != null) {
            if (Float.parseFloat(currentRestaurant.get("rating")) <= 2) {
                detailRestaurantStar1.setVisibility(View.VISIBLE);
                detailRestaurantStar2.setVisibility(View.GONE);
                detailRestaurantStar3.setVisibility(View.GONE);
            } else {
                if (Float.parseFloat(currentRestaurant.get("rating")) <= 3.5) {
                    detailRestaurantStar1.setVisibility(View.VISIBLE);
                    detailRestaurantStar2.setVisibility(View.VISIBLE);
                    detailRestaurantStar3.setVisibility(View.GONE);
                } else {
                    detailRestaurantStar1.setVisibility(View.VISIBLE);
                    detailRestaurantStar2.setVisibility(View.VISIBLE);
                    detailRestaurantStar3.setVisibility(View.VISIBLE);
                }
            }
        }

        detailRestaurantName.setText(currentRestaurant.get("place_name"));
        detailRestaurantAddress.setText(currentRestaurant.get("vicinity"));

        detailRestaurantCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + currentRestaurant.get("phone_number"))));
            }
        });
    }


}
