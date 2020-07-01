package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import static com.example.go4lunch.fragments.map.MapFragment.getCustomMarkerUrl;
import static com.example.go4lunch.fragments.map.MapFragment.nearbyRestaurantList;
import static com.example.go4lunch.fragments.map.MapFragment.fakeConfig;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/***************************************************************************************************************************************************************************************************************************
*                                                                                                                                                                                                                          *
*                                 This class purpose is to fetch nearby restaurants with Google Places Search requests                                                                                                     *
*                                                                                                                                                                                                                          *
****************************************************************************************************************************************************************************************************************************/

public class NearbyRestaurants extends AsyncTask<Object, Void, String>
{
    private static final String TAG = "NearbyRestaurants : ";
    static String apiKey;
    String googlePlacesData;
    String nearbyPlacesRequest;
    static Bitmap customRestaurantsBitmap = null;

    public static int pageCount;

    public NearbyRestaurantsResponse callback = null;

    public NearbyRestaurants(NearbyRestaurantsResponse callback) {
        Log.i(TAG, "task " + pageCount + " created");
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        apiKey = (String) objects[0];
        nearbyPlacesRequest = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            // Get once the custom marker that we will use for our restaurants
            if (customRestaurantsBitmap == null){
                customRestaurantsBitmap = getBitmapFromURL(getCustomMarkerUrl());
            }
            if (!fakeConfig)
                googlePlacesData = downloadUrl.readUrl(nearbyPlacesRequest);
            else
                googlePlacesData = nearbyPlacesRequest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i(TAG, "onPostExecute: task " + pageCount + " finishing...");
        PlacesSearchDataParser parser = new PlacesSearchDataParser();
        nearbyRestaurantList.addAll(parser.parse(s));
        callback.onNearbyRestaurantsCompleted(parser.nextPageToken);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromURL: "+e.getMessage() );
            return null;
        }
    }

    public interface NearbyRestaurantsResponse
    {
        void onNearbyRestaurantsCompleted(String nextPageToken);
    }

}
