package com.example.go4lunch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.example.go4lunch.models.Restaurant;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.go4lunch.BuildConfig.API_KEY;
import static com.example.go4lunch.activities.MainActivity.PROXIMITY_RADIUS;
import static com.example.go4lunch.activities.MainActivity.fakeConfig;
import static com.example.go4lunch.activities.MainActivity.indexOfRestaurantToGetDetails;
import static com.example.go4lunch.activities.MainActivity.lastKnownLocation;
import static com.example.go4lunch.activities.MainActivity.mMap;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurant;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurantList;
import static com.example.go4lunch.activities.MainActivity.tokenNumber;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

public class RestaurantMarkersHandler implements NearbyRestaurants.NearbyRestaurantsResponse, PlaceDetails.PlaceDetailsResponse
{
    private static final String TAG = "RestaurantMarkerHandler";
    Context context;

    public RestaurantMarkersHandler(Context context) {
        this.context = context;
    }

    public void getRestaurantsLocations() {
        if (!fakeConfig) {
            String url = getPlacesSearchUrl(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), "restaurant");
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = API_KEY;
            dataTransfer[1] = url;

            NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
            NearbyRestaurants.pageCount = 1;
            Log.i(TAG, "getRestaurantsLocations: task : " + NearbyRestaurants.pageCount + " executing...");
            nearbyRestaurants.execute(dataTransfer);
        }
        else{
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = API_KEY;
            dataTransfer[1] = loadJSONFromAsset(context,"places_search_results_cahors_page_1");

            NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
            NearbyRestaurants.pageCount = 1;
            Log.i(TAG, "getRestaurantsLocations: task : " + NearbyRestaurants.pageCount + " executing...");
            nearbyRestaurants.execute(dataTransfer);
        }
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

        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        markerOptions.title(placeName);
        markerOptions.snippet(vicinity);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mIcon,100,150,false);
        changeBitmapTintTo(scaledBitmap, Color.RED);
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
        googlePlaceUrl.append("&key=" + API_KEY);

        return googlePlaceUrl.toString();
    }

    public String getPlacesSearchNextPageUrl(String nextPageToken){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("&key=" + API_KEY);
        googlePlaceUrl.append("&pagetoken=" + nextPageToken);

        return googlePlaceUrl.toString();
    }

    private String makePlacesDetailsRequest(String placeId){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceUrl.append("&key=" + API_KEY);
        googlePlaceUrl.append("&place_id=" + placeId);

        return googlePlaceUrl.toString();
    }

    private String makePlacesPhotoRequest(String photoReference, String maxWidth){
        if (photoReference.compareTo("null") == 0 || maxWidth.compareTo("0") == 0)
            return "null";
        else {
            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
            googlePlaceUrl.append("maxwidth=" + maxWidth);
            googlePlaceUrl.append("&photoreference=" + photoReference);
            googlePlaceUrl.append("&key=" + API_KEY);

            return googlePlaceUrl.toString();
        }
    }

    // Callback method from NearbyPlacesSearch Asynctask
    @Override
    public void onNearbyRestaurantsCompleted(String nextPageToken) {
        // Making Places Details request to get more informations about all restaurants treated in previous thread

        Iterator iterator = nearbyRestaurantList.iterator();
        HashMap<String, String> currentRestaurant = new HashMap<>();
        do{
            currentRestaurant = (HashMap<String, String>) iterator.next();

        }while(currentRestaurant != nearbyRestaurantList.get(indexOfRestaurantToGetDetails));

        do{
            Log.i(TAG, "onNearbyRestaurantsCompleted: launching PlacesDetails task number : " + indexOfRestaurantToGetDetails);
            Object transferObject[] = new Object[3];
            transferObject[0] = (String) makePlacesDetailsRequest(currentRestaurant.get("place_id"));
            transferObject[1] = (String) makePlacesPhotoRequest(currentRestaurant.get("photo_reference"), currentRestaurant.get("photo_width"));
            transferObject[2] = (int) indexOfRestaurantToGetDetails;
            PlaceDetails placesDetails = new PlaceDetails(this);
            placesDetails.execute(transferObject);
            indexOfRestaurantToGetDetails++;
            currentRestaurant = (HashMap<String, String>) iterator.next();
        }while (iterator.hasNext());

        // Treating the last restaurant of the page, then treating another page
        Log.i(TAG, "onNearbyRestaurantsCompleted: launching PlacesDetails task number : " + indexOfRestaurantToGetDetails);
        Object transferObject[] = new Object[3];
        transferObject[0] = (String) makePlacesDetailsRequest(currentRestaurant.get("place_id"));
        transferObject[1] = (String) makePlacesPhotoRequest(currentRestaurant.get("photo_reference"), currentRestaurant.get("photo_width"));
        transferObject[2] = (int) indexOfRestaurantToGetDetails;
        PlaceDetails placesDetails = new PlaceDetails(this);
        placesDetails.execute(transferObject);
        indexOfRestaurantToGetDetails++;

        Log.i(TAG, "onNearbyRestaurantsCompleted : page number : " + NearbyRestaurants.pageCount + " ending...");

        // Then making custom Places Search request with nextpagetoken in another thread
        if (nextPageToken != null) {
            if (!fakeConfig) {
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = API_KEY;
                dataTransfer[1] = getPlacesSearchNextPageUrl(nextPageToken);

                NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
                Log.i(TAG, "onNearbyRestaurantsCompleted : page number : " + NearbyRestaurants.pageCount + " executing...");
                nearbyRestaurants.execute(dataTransfer);
            }
            else {
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = API_KEY;
                dataTransfer[1] = loadJSONFromAsset(context,"places_search_results_cahors_page_" + tokenNumber);

                NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
                Log.i(TAG, "onNearbyRestaurantsCompleted : page number : " + NearbyRestaurants.pageCount + " executing...");
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
        Log.i(TAG, "onPlaceDetailsCompleted: adding restaurant : " + googlePlace.get("place_name"));
        // Adding markers, one by one to the map
        addCustomRestaurantMarker(googlePlace,mIcon);
        // Finally creating one by one our restaurants
        nearbyRestaurant.add(new Restaurant(googlePlace, lastKnownLocation));
    }
}
