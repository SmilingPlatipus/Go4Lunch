package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import static com.example.go4lunch.fragments.map.MapFragment.getCustomMarkerUrl;
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
    int currentIndex;
    Bitmap customRestaurantBitmap = null;

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
        customRestaurantBitmap = getBitmapFromURL(getCustomMarkerUrl());
        Log.i(TAG, "doInBackground: treating place number : " + currentIndex);

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
            nearbyRestaurantList.remove(currentIndex);
            nearbyRestaurantList.add(currentIndex,buffer);
            callback.onPlaceDetailsCompleted(buffer,customRestaurantBitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public interface PlaceDetailsResponse
    {
        void onPlaceDetailsCompleted(HashMap<String, String> googlePlace, Bitmap mIcon);
    }

}
