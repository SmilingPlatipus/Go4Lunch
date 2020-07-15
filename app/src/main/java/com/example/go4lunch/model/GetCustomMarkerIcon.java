package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.go4lunch.activities.MainActivity.customRestaurantBitmap;


public class GetCustomMarkerIcon extends AsyncTask<Object, Void, Void>
{
    private static String TAG = "GetCustomMarkerIcon";

    @Override
    protected Void doInBackground(Object... objects) {
        if (customRestaurantBitmap == null) {
        customRestaurantBitmap = getBitmapFromURL((String) objects[0]);
            Log.i(TAG, "doInBackground: getting bitmap from url : "+ (String) objects[0]);
    }
        return null;
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
