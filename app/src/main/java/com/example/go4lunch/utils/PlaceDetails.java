package com.example.go4lunch.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import static com.example.go4lunch.activities.MainActivity.customRestaurantBitmap;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurantList;



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
    int currentIndex;

    public PlaceDetailsResponse callback = null;

    public PlaceDetails(PlaceDetailsResponse callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Object... objects) {
        placeDetailsRequest = (String) objects[0];
        placePhotoRequest = (String) objects[1];
        currentIndex = (int) objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();

        Log.i(TAG, "treating place number : " + currentIndex + " place name : " + nearbyRestaurantList.get(currentIndex).get("place_name"));

        try {
            googlePlaceDetails = downloadUrl.readUrl(placeDetailsRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceDetails;
    }

    @Override
    protected void onPostExecute(String s) {
        PlacesDetailsDataParser parser = new PlacesDetailsDataParser();
        try {
            HashMap<String, String> buffer = nearbyRestaurantList.get(currentIndex);
            HashMap<String, String> placeDetailsRequest = parser.getRestaurantDetails(s);
            buffer.put("phone_number", placeDetailsRequest.get("phone_number"));
            buffer.put("website", placeDetailsRequest.get("website"));
            buffer.put("photo_url",placePhotoRequest);
            Log.i(TAG, "onPostExecute: PlacesDetailsDataParser value for photo_url is : " + buffer.get("photo_url"));
            nearbyRestaurantList.set(currentIndex,buffer);

            callback.onPlaceDetailsCompleted(buffer,customRestaurantBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface PlaceDetailsResponse
    {
        void onPlaceDetailsCompleted(HashMap<String, String> googlePlace, Bitmap mIcon);
    }

}
