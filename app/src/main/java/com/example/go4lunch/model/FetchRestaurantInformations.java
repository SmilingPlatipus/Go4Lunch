package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/***************************************************************************************************************************************************************************************************************************
*                                                                                                                                                                                                                          *
*                              This class purpose is to fetch restaurant informations with Google Places Details and Photos requests                                                                                       *
*                                                                                                                                                                                                                          *
****************************************************************************************************************************************************************************************************************************/

public class FetchRestaurantInformations extends AsyncTask<Object, String, String>
{
    public static Bitmap markerIcon;

    String markerUrl;


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

    @Override
    protected String doInBackground(Object... objects) {
        markerUrl = (String) objects[0];
        markerIcon = getBitmapFromURL(markerUrl);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
