package com.example.go4lunch.utils;

import android.os.AsyncTask;
import android.util.Log;

import static com.example.go4lunch.models.Restaurant.nearbyRestaurantList;
import static com.example.go4lunch.activities.MainActivity.fakeConfig;


import java.io.IOException;

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

    public static int pageCount;

    public NearbyRestaurantsResponse callback = null;

    public NearbyRestaurants(NearbyRestaurantsResponse callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        Log.i(TAG, "NearbyRestaurants treating page number : " + pageCount);
        apiKey = (String) objects[0];
        nearbyPlacesRequest = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
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
        NearbyRestaurantsDataParser parser = new NearbyRestaurantsDataParser();
        nearbyRestaurantList.addAll(parser.parse(s));
        callback.onNearbyRestaurantsCompleted(parser.nextPageToken);
    }

    public interface NearbyRestaurantsResponse
    {
        void onNearbyRestaurantsCompleted(String nextPageToken);
    }

}
