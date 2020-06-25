package com.example.go4lunch.model;

import android.os.AsyncTask;
import android.util.Log;


import static android.content.ContentValues.TAG;
import static com.example.go4lunch.fragments.map.MapFragment.nearbyPlacesList;

import java.io.IOException;

/***************************************************************************************************************************************************************************************************************************
*                                                                                                                                                                                                                          *
*                                 This class purpose is to fetch nearby restaurants with Google Places Search requests                                                                                                     *
*                                                                                                                                                                                                                          *
****************************************************************************************************************************************************************************************************************************/

public class NearbyPlacesSearch extends AsyncTask<Object, Void, String>
{
    String googlePlacesData, url;
    public static int pageCount;

    public NearbyPlacesSearchResponse callback = null;

    public NearbyPlacesSearch(NearbyPlacesSearchResponse callback) {
        pageCount++;
        Log.i(TAG, "NearbyPlacesSearch: task " + pageCount + " created");
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        url = (String) objects[0];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.i(TAG, "onPostExecute: task " + pageCount + " finishing...");
        PlacesSearchDataParser parser = new PlacesSearchDataParser();
        nearbyPlacesList.addAll(parser.parse(s));
        callback.onNearbyPlacesSearchFinished(parser.nextPageToken);
    }

    public interface NearbyPlacesSearchResponse
    {
        void onNearbyPlacesSearchFinished(String nextPageToken);
    }

}
