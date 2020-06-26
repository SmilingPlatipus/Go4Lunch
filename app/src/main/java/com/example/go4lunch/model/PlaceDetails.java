package com.example.go4lunch.model;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;



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
            parser.getRestaurantDetails(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
