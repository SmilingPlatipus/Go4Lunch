package com.example.go4lunch.fragments.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.example.go4lunch.model.NearbyRestaurants;
import com.example.go4lunch.model.PlaceDetails;
import com.example.go4lunch.model.Restaurant;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.go4lunch.activities.MainActivity.mFusedLocationProviderClient;
import static com.example.go4lunch.activities.MainActivity.mMap;
import static com.example.go4lunch.activities.MainActivity.lastKnownLocation;
import static com.example.go4lunch.activities.MainActivity.DEFAULT_ZOOM;
import static com.example.go4lunch.activities.MainActivity.mapView;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, GoogleMap.OnPoiClickListener, NearbyRestaurants.NearbyRestaurantsResponse, PlaceDetails.PlaceDetailsResponse
{
    private static final String TAG = "MapFragment";

    private static final int PERMS_CALL_CODE = 354;
    private static final int PROXIMITY_RADIUS = 10000;

    private LocationManager locationManager;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPermissions();

        initMap();
    }

    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_CODE);
            return;
        }
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,10000,0,this);
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,0,this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMS_CALL_CODE){
            checkPermissions();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;
        getDeviceLocation();
        mMap.setOnPoiClickListener(this);

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
        // This can be changer with the search bar by selecting a place
        // Just press the "Mylocation" button to get back to current place
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

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        /*
        Toast.makeText(getApplicationContext(), "Clicked: " +
                               pointOfInterest.name + "\nPlace ID:" + pointOfInterest.placeId +
                               "\nLatitude:" + pointOfInterest.latLng.latitude +
                               " Longitude:" + pointOfInterest.latLng.longitude,
                       Toast.LENGTH_SHORT).show();

         */
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

    public static void addCustomRestaurantsMarkers(List<HashMap<String, String>> nPlaceList, Bitmap mIcon){
        for (int placeCount =0;placeCount < nPlaceList.size();placeCount++){

            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nPlaceList.get(placeCount);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            // Todo : change bitmap directly into NearbyRestaurants
            /*
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(mIcon,100,150,false);
            changeBitmapTintTo(scaledBitmap, Color.CYAN);
            mIcon = scaledBitmap;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mIcon);

            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);

             */
            Log.i(TAG, "addNearbyPlacesMarkers: element "+placeCount+":"+placeName+" "+vicinity+" "+lat+" "+lng);
        }
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
            /*
            if (currentRestaurant.get("photo_reference") != null && currentRestaurant.get("width") != null)
                transferObject[1] = (String) makePlacesPhotoRequest(currentRestaurant.get("photo_reference"),currentRestaurant.get("width"));
            else
                transferObject[1] = null;

             */
            transferObject[2] = (int) restaurantCount;
            PlaceDetails placesDetails = new PlaceDetails(this);
            placesDetails.execute(transferObject);
            restaurantCount++;
        }
         Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " ending...");
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
    public void onPlaceDetailsCompleted() {
        // Finally creating one by one our restaurants

    }

}
