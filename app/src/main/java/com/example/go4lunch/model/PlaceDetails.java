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
    String photoReference;
    String photoUrl;
    public static int placeCount = 0;

    @Override
    protected String doInBackground(Object... objects) {
        placeCount++;
        placeDetailsRequest = (String) objects[0];
        photoReference = (String) objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        Log.i(TAG, "doInBackground: treating place number : " + placeCount);

        try {
            googlePlaceDetails = downloadUrl.readUrl(placeDetailsRequest);
            // Todo : this request isn't working fine :
            photoUrl = downloadUrl.readUrl(photoReference);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceDetails;
    }

    @Override
    protected void onPostExecute(String s) {
        PlacesDetailsDataParser parser = new PlacesDetailsDataParser();
        try {

            nearbyRestaurantList.add(placeCount-1,parser.getRestaurantDetails(s));
            HashMap<String, String> photoRow = new HashMap<>();
            photoRow.put("photo_url",photoUrl);
            nearbyRestaurantList.add(placeCount-1,photoRow);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
