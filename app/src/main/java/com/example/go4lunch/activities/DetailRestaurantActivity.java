package com.example.go4lunch.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.utils.ReminderBroadcast;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.HashMap;


import static com.example.go4lunch.models.Restaurant.nearbyRestaurant;
import static com.example.go4lunch.models.Restaurant.nearbyRestaurantList;
import static com.example.go4lunch.activities.MainActivity.customRestaurantBitmap;
import static com.example.go4lunch.activities.MainActivity.userChoice;
import static com.example.go4lunch.activities.MainActivity.userDocumentID;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.example.go4lunch.fragments.map.MapFragment.RESTAURANT_INDEX;
import static com.example.go4lunch.utils.ReminderBroadcast.NOTIFICATION_LUNCH;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.DEFAULT_MARKER_COLOR;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.SELECTED_MARKER_COLOR;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.addCustomRestaurantMarker;

public class DetailRestaurantActivity extends AppCompatActivity
{
    private static final int CALL_PHONE_PERMISSION_CODE = 12;
    private ImageView detailRestaurantPic, detailRestaurantStar1, detailRestaurantStar2, detailRestaurantStar3;
    private TextView detailRestaurantName, detailRestaurantAddress;
    private Button detailRestaurantCall, detailRestaurantWebsite, detailRestaurantLike;
    static Intent intent = new Intent();
    NotificationManager notificationManager;
    int restaurantIndex;
    HashMap<String, String> currentRestaurant;
    Restaurant lastChoice;

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
        currentRestaurant = nearbyRestaurantList.get(restaurantIndex);
    }

    private void initDetailRestaurantWidgets() {
        if (currentRestaurant.get("photo_url").compareTo("null") != 0) {
            Glide.with(this)
                    .load(currentRestaurant.get("photo_url"))
                    .apply(new RequestOptions().override(70, 70))
                    .into(detailRestaurantPic);
        }
        else{
            Glide.with(this)
                    .load("https://www.recia.fr/wp-content/uploads/2019/09/no_image.png")
                    .apply(new RequestOptions().override(70, 70))
                    .into(detailRestaurantPic);
        }
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
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", currentRestaurant.get("phone_number"), null)));
                }
                else
                    ActivityCompat.requestPermissions(DetailRestaurantActivity.this, new String[]{
                            Manifest.permission.CALL_PHONE
                    }, CALL_PHONE_PERMISSION_CODE);

            }
        });

        detailRestaurantWebsite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                goToUrl(currentRestaurant.get("website"));
            }
        });

        detailRestaurantLike.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), getString(R.string.detail_restaurant_choice) + currentRestaurant.get("place_name"), Toast.LENGTH_SHORT).show();

                createNotificationChannel();
                lastChoice = new Restaurant();

                if (userChoice != null) {
                    lastChoice = Restaurant.searchByPlaceId(userChoice);
                }
                workmatesReference.document(userDocumentID).update("choice", currentRestaurant.get("place_id"))
                .addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // First changing previous marker color if he has no more participants
                        if (userChoice != null) {
                            if (lastChoice.getWorkmatesCount() == 0)
                                addCustomRestaurantMarker(lastChoice.getInstanceAsHashMap(), customRestaurantBitmap, DEFAULT_MARKER_COLOR);

                            notificationManager.cancelAll();
                        }

                        // Then changing marker color and updating workmatesCount on this restaurant
                        addCustomRestaurantMarker(currentRestaurant, customRestaurantBitmap, SELECTED_MARKER_COLOR);
                        userChoice = currentRestaurant.get("place_id");
                        createAlarm();

                        nearbyRestaurant.get(restaurantIndex).setWorkmatesCount(nearbyRestaurant.get(restaurantIndex).getWorkmatesCount() + 1);

                        // Then updating previous restaurant and marker if needed
                        for (Restaurant currentRestaurant : nearbyRestaurant) {
                            if (currentRestaurant.getName() != null)
                                if (lastChoice.getName().compareTo(currentRestaurant.getName()) == 0) {
                                    currentRestaurant.setWorkmatesCount(currentRestaurant.getWorkmatesCount()-1);
                                    if (currentRestaurant.getWorkmatesCount() == 0)
                                        addCustomRestaurantMarker(currentRestaurant.getInstanceAsHashMap(), customRestaurantBitmap, DEFAULT_MARKER_COLOR);
                                }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(DetailRestaurantActivity.this,
                           "Phone Call permission Granted",
                           Toast.LENGTH_SHORT)
                    .show();

    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void createAlarm(){
        Intent intent = new Intent(DetailRestaurantActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailRestaurantActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 00);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()/1000,pendingIntent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_LUNCH, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
