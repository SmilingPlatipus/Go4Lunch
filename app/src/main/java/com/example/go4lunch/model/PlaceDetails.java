package com.example.go4lunch.model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import static com.example.go4lunch.fragments.map.MapFragment.nearbyRestaurantList;



/***************************************************************************************************************************************************************************************************************************
 *                                                                                                                                                                                                                          *
 *                                          This class purpose is to retrieve the phone number and the photo of a restaurant                                                                                                *
 *                                                                                                                                                                                                                          *
 ****************************************************************************************************************************************************************************************************************************/

public class PlaceDetails extends AsyncTask<Object, Void, String>
{
    private static final String TAG = "PlaceDetails : ";
    String placeDetailsRequest;
    String googlePlaceDetails;
    String placePhotoRequest;
    String photoUrl = null;
    int placeCount;

    @Override
    protected String doInBackground(Object... objects) {
        placeDetailsRequest = (String) objects[0];
        if ( objects[1] != null)
            placePhotoRequest = (String) objects[1];
        else
            placePhotoRequest = null;
        placeCount = (int) objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        Log.i(TAG, "doInBackground: treating place number : " + placeCount);

        try {
            googlePlaceDetails = downloadUrl.readUrl(placeDetailsRequest);

            if (placePhotoRequest != null)
                photoUrl = downloadUrl.readUrl(placePhotoRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceDetails;
    }

    @Override
    protected void onPostExecute(String s) {
        PlacesDetailsDataParser parser = new PlacesDetailsDataParser();
        try {
            HashMap<String, String> buffer = nearbyRestaurantList.get(placeCount);
            buffer.put("phone_number", parser.getRestaurantDetails(s));
            buffer.put("photo_url",photoUrl);
            nearbyRestaurantList.add(placeCount, buffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
