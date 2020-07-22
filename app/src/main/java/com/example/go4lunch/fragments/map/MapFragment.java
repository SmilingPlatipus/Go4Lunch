package com.example.go4lunch.fragments.map;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.DetailRestaurantActivity;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.Query;


import java.util.HashMap;

import static com.example.go4lunch.utils.RestaurantMarkersHandler.DEFAULT_MARKER_COLOR;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.SELECTED_MARKER_COLOR;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.addCustomRestaurantMarker;
import static com.example.go4lunch.activities.MainActivity.customRestaurantBitmap;
import static com.example.go4lunch.activities.MainActivity.mMap;
import static com.example.go4lunch.activities.MainActivity.lastKnownLocation;
import static com.example.go4lunch.activities.MainActivity.DEFAULT_ZOOM;
import static com.example.go4lunch.activities.MainActivity.mapView;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurant;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurantList;
import static com.example.go4lunch.activities.MainActivity.optionsForWorkmatesEatingInThisRestaurant;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.example.go4lunch.activities.MainActivity.savedMapState;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    private static final String TAG = "MapFragment";
    public final static String RESTAURANT_INDEX = "RESTAURANT_INDEX";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    @Override
    public void onStop() {
        super.onStop();
        savedMapState = new MapStateManager(getContext());
        savedMapState.clearMapState();
        savedMapState.saveMapState(mMap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (savedMapState == null) {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file, removing businesses.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.map_style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
            mMap = googleMap;
            mMap.setOnMarkerClickListener(this);

            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM));

            // Activating the "MyLocation" button and replacing it at the bottom of the map


            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }

            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);

            // Setting up MyLocation button in the bottom right corner
            if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                layoutParams.setMargins(0, 0, 40, 180);
            }
        }

        else {
            // Restoring basic map state
            mMap = googleMap;
            mMap.setOnMarkerClickListener(this);

            MapStateManager mgr = new MapStateManager(getContext());
            CameraPosition position = mgr.getSavedCameraPosition();
            if (position != null) {
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                mMap.moveCamera(update);

                mMap.setMapType(mgr.getSavedMapType());
            }

            // Then restoring markers previously placed on the map, while Places Requests continues to run in background

            for (Restaurant currentRestaurant : nearbyRestaurant){
                HashMap<String, String> restaurant = currentRestaurant.getInstanceAsHashMap();
                if (currentRestaurant.getWorkmatesCount() > 0)
                    addCustomRestaurantMarker(restaurant, customRestaurantBitmap, SELECTED_MARKER_COLOR);
                else
                    addCustomRestaurantMarker(restaurant, customRestaurantBitmap, DEFAULT_MARKER_COLOR);
            }
        }
    }

    private void initMap() {
            Log.d(TAG, "initMap: initializing map");
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapView = mapFragment.getView();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        final Dialog d = new Dialog(this.getContext());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.custom_info_window_layout);
        TextView restaurantName = d.findViewById(R.id.restaurant_name);
        TextView restaurantAddress = d.findViewById(R.id.restaurant_address);
        ImageView restaurantPic = d.findViewById(R.id.restaurant_pic);
        Button restaurantButton = d.findViewById(R.id.restaurant_detail_button);
        ImageView star1 = d.findViewById(R.id.info_window_restaurant_star1);
        ImageView star2 = d.findViewById(R.id.info_window_restaurant_star2);
        ImageView star3 = d.findViewById(R.id.info_window_restaurant_star3);
                for (HashMap<String, String> currentRestaurant : nearbyRestaurantList){
                    if (currentRestaurant.get("place_name") != null)
                    if (marker.getTitle().compareTo(currentRestaurant.get("place_name")) == 0){
                        restaurantName.setText(currentRestaurant.get("place_name"));
                        restaurantAddress.setText(currentRestaurant.get("vicinity"));
                        Glide.with(getContext())
                                .load(currentRestaurant.get("photo_url"))
                                .apply(new RequestOptions().override(70, 70))
                                .into(restaurantPic);
                        restaurantButton.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {
                                // Starting detailRestaurantActivity, and transmiting currentRestaurant
                                Intent detailRestaurantActivity = new Intent(getContext(), DetailRestaurantActivity.class);
                                detailRestaurantActivity.putExtra(RESTAURANT_INDEX, nearbyRestaurantList.indexOf(currentRestaurant));

                                // Setting options for cloud firestore
                                Query query = workmatesReference.whereEqualTo("choice", currentRestaurant.get("place_id"));
                                // Recycler Options
                                optionsForWorkmatesEatingInThisRestaurant = new FirestoreRecyclerOptions.Builder<Workmate>()
                                        .setQuery(query,Workmate.class)
                                        .build();

                                startActivity(detailRestaurantActivity);
                            }
                        });

                        if (Float.parseFloat(currentRestaurant.get("rating")) <= 2){
                            star1.setVisibility(View.VISIBLE);
                            star2.setVisibility(View.GONE);
                            star3.setVisibility(View.GONE);
                        }
                        else {
                            if (Float.parseFloat(currentRestaurant.get("rating")) <= 3.5) {
                                star1.setVisibility(View.VISIBLE);
                                star2.setVisibility(View.VISIBLE);
                                star3.setVisibility(View.GONE);
                            } else {
                                star1.setVisibility(View.VISIBLE);
                                star2.setVisibility(View.VISIBLE);
                                star3.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

        d.show();

        return true;
    }

}
