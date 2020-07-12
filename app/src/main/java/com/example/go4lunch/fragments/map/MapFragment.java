package com.example.go4lunch.fragments.map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.activities.DetailRestaurantActivity;
import com.example.go4lunch.model.NearbyRestaurants;
import com.example.go4lunch.model.PlaceDetails;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.go4lunch.activities.MainActivity.mFusedLocationProviderClient;
import static com.example.go4lunch.activities.MainActivity.mMap;
import static com.example.go4lunch.activities.MainActivity.lastKnownLocation;
import static com.example.go4lunch.activities.MainActivity.DEFAULT_ZOOM;
import static com.example.go4lunch.activities.MainActivity.mapView;
import static com.example.go4lunch.activities.MainActivity.optionsForWorkmatesEatingInThisRestaurant;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.facebook.FacebookSdk.getApplicationContext;


public class MapFragment extends Fragment implements OnMapReadyCallback, NearbyRestaurants.NearbyRestaurantsResponse, PlaceDetails.PlaceDetailsResponse,
                                                     GoogleMap.OnMarkerClickListener
{
    private static final String TAG = "MapFragment";

    private static final int PROXIMITY_RADIUS = 10000;
    public final static String RESTAURANT_INDEX = "RESTAURANT_INDEX";

    public static List<HashMap<String, String>> nearbyRestaurantList = new ArrayList();
    public static List<Restaurant> nearbyRestaurant = new ArrayList<>();

    // This is a fake configuration for local testing purposes
    public static boolean fakeConfig = true;
    public static int tokenNumber = 2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;
        getDeviceLocation();
        mMap.setOnMarkerClickListener(this);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file, removing businesses.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getApplicationContext(), R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
    }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
    }

    private void getDeviceLocation(){
        // Setting the location of the user to its current position by default
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try{
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener()
                {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location : " + location.getResult().toString());
                            lastKnownLocation = (Location) task.getResult();
                            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,DEFAULT_ZOOM));

                            // Activating the "MyLocation" button and replacing it at the bottom of the map
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.getUiSettings().setCompassEnabled(true);
                            if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null){
                                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                                layoutParams.setMargins(0, 0, 40, 180);
                            }
                            // Searching for restaurants in the current area, with Google Places requests
                            getRestaurantsLocations();
                        }
                        else{
                            Log.d(TAG, "onComplete: current location not found");
                            Toast.makeText(getContext(), getString(R.string.user_current_location_not_found), Toast.LENGTH_LONG).show();
                        }
                    }
                });

        }catch(SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: "+ e.getMessage());
        }
    }

    private void getRestaurantsLocations() {
        if (!fakeConfig) {
            String url = getPlacesSearchUrl(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), "restaurant");
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
            dataTransfer[1] = url;

            NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
            NearbyRestaurants.pageCount = 1;
            Log.i(TAG, "getRestaurantsLocations: task : " + NearbyRestaurants.pageCount + " executing...");
            nearbyRestaurants.execute(dataTransfer);
        }
        else{
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
            dataTransfer[1] = loadJSONFromAsset(getApplicationContext(),"places_search_results_cahors_page_1");

            NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
            NearbyRestaurants.pageCount = 1;
            Log.i(TAG, "getRestaurantsLocations: task : " + NearbyRestaurants.pageCount + " executing...");
            nearbyRestaurants.execute(dataTransfer);
        }
    }

    public static String getCustomMarkerUrl(){
        return getApplicationContext().getString(R.string.custom_marker_url);
    }

    public static void changeBitmapTintTo(Bitmap myBitmap, int color){
        int [] allpixels = new int [myBitmap.getHeight() * myBitmap.getWidth()];

        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        for(int i = 0; i < allpixels.length; i++)
        {
            if(allpixels[i] == Color.BLACK)
            {
                allpixels[i] = color;
            }
        }
        myBitmap.setPixels(allpixels,0,myBitmap.getWidth(),0, 0, myBitmap.getWidth(),myBitmap.getHeight());
    }

    public static void addCustomRestaurantMarker(HashMap<String, String> googlePlace, Bitmap mIcon){

            MarkerOptions markerOptions = new MarkerOptions();

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(vicinity);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(mIcon,100,150,false);
            changeBitmapTintTo(scaledBitmap, Color.CYAN);
            mIcon = scaledBitmap;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mIcon);

            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);
    }

    private String getPlacesSearchUrl(double latitude, double longitude, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + getString(R.string.google_api_key));

        return googlePlaceUrl.toString();
    }

    public static String getPlacesSearchNextPageUrl(String nextPageToken){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("&key=" + getApplicationContext().getString(R.string.google_api_key));
        googlePlaceUrl.append("&pagetoken=" + nextPageToken);

        return googlePlaceUrl.toString();
    }

    private String makePlacesDetailsRequest(String placeId){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceUrl.append("&key=" + getApplicationContext().getString(R.string.google_api_key));
        googlePlaceUrl.append("&place_id=" + placeId);

        return googlePlaceUrl.toString();
    }

    private String makePlacesPhotoRequest(String photoReference, String maxWidth){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        googlePlaceUrl.append("maxwidth=" + maxWidth);
        googlePlaceUrl.append("&photoreference=" + photoReference);
        googlePlaceUrl.append("&key=" + getApplicationContext().getString(R.string.google_api_key));

        return googlePlaceUrl.toString();
    }

    // Callback method from NearbyPlacesSearch Asynctask
    @Override
    public void onNearbyRestaurantsCompleted(String nextPageToken) {
         // Making Places Details request to get more informations about all restaurants treated in previous thread
        int restaurantCount = 0;
        for (HashMap<String, String> currentRestaurant : nearbyRestaurantList) {
            Object transferObject[] = new Object[3];
            transferObject[0] = (String) makePlacesDetailsRequest(currentRestaurant.get("place_id"));
            transferObject[1] = (String) makePlacesPhotoRequest(currentRestaurant.get("photo_reference"),currentRestaurant.get("photo_width"));
            transferObject[2] = (int) restaurantCount;
            PlaceDetails placesDetails = new PlaceDetails(this);
            placesDetails.execute(transferObject);
            restaurantCount++;
        }
         Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " ending...");

        // Then making custom Places Search request with nextpagetoken in another thread
        if (nextPageToken != null) {
            if (!fakeConfig) {
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
                dataTransfer[1] = getPlacesSearchNextPageUrl(nextPageToken);

                NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
                Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " executing...");
                nearbyRestaurants.execute(dataTransfer);
            }
            else {
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
                dataTransfer[1] = loadJSONFromAsset(getApplicationContext(),"places_search_results_cahors_page_" + tokenNumber);

                NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
                Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " executing...");
                nearbyRestaurants.execute(dataTransfer);
                tokenNumber++;
            }
        }
    }

    // This is for fake configuration purpose
    public String loadJSONFromAsset(Context context, String file_name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(file_name + ".json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    public void onPlaceDetailsCompleted(HashMap<String, String> googlePlace, Bitmap mIcon) {
        // Adding markers, one by one to the map
        addCustomRestaurantMarker(googlePlace,mIcon);
        // Finally creating one by one our restaurants
        nearbyRestaurant.add(new Restaurant(googlePlace,lastKnownLocation));
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
