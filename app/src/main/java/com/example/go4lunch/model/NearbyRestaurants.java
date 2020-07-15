package com.example.go4lunch.model;

import android.os.AsyncTask;
import android.util.Log;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurantList;
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
        Log.i(TAG, "task " + pageCount + " created");
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
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
        Log.i(TAG, "onPostExecute: task " + pageCount + " finishing...");
        PlacesSearchDataParser parser = new PlacesSearchDataParser();
        nearbyRestaurantList.addAll(parser.parse(s));
        callback.onNearbyRestaurantsCompleted(parser.nextPageToken);
    }

    public interface NearbyRestaurantsResponse
    {
        void onNearbyRestaurantsCompleted(String nextPageToken);
    }

}
