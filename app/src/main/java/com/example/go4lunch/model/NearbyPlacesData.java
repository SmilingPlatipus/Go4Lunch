package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.go4lunch.fragments.map.MapFragment;
import com.google.android.gms.maps.GoogleMap;

import static android.content.ContentValues.TAG;
import static com.example.go4lunch.activities.MainActivity.mMap;
import static com.example.go4lunch.fragments.map.MapFragment.getPlacesSearchNextPageUrl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyPlacesData extends AsyncTask<Object, String, String>
{
    String googlePlacesData, url;
    final String customMarkerUrl = "https://cdn.pixabay.com/photo/2017/02/17/11/52/icon-2073973_960_720.png";
    public static Bitmap markerIcon;
    public static List<HashMap<String, String>> nearbyPlacesList = new ArrayList();
    public boolean lastTask = false;
    // Todo : trying to find a way to ensure tasks are terminated

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
            markerIcon = getBitmapFromURL(customMarkerUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        DataParser parser = new DataParser();
        nearbyPlacesList.addAll(parser.parse(s));
        if (parser.nextPageToken != null) {
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = mMap;
            dataTransfer[1] = getPlacesSearchNextPageUrl(parser.nextPageToken);

            NearbyPlacesData nearbyPlacesData = new NearbyPlacesData();
            nearbyPlacesData.lastTask = true;
            nearbyPlacesData.execute(dataTransfer);
        }
        // Todo : then calling ui method to refresh UIThread with all results from Google NearbySearch
        else
            if (this.lastTask)
                MapFragment.addNearbyPlacesMarkers(nearbyPlacesList,markerIcon);
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

}
