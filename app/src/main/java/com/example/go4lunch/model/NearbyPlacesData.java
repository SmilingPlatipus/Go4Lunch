package com.example.go4lunch.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.ContentValues.TAG;
import static com.example.go4lunch.activities.MainActivity.mMap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class NearbyPlacesData extends AsyncTask<Object, String, String>
{
    String googlePlacesData, url;
    Bitmap markerIcon;
    List<HashMap<String, String>> nearbyPlacesList = null;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
            markerIcon = getBitmapFromURL("https://cdn.pixabay.com/photo/2017/02/17/11/52/icon-2073973_960_720.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        DataParser parser = new DataParser();
        nearbyPlacesList = parser.parse(s);
        addNearbyPlacesMarkers(nearbyPlacesList,markerIcon);
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

    private void addNearbyPlacesMarkers(List<HashMap<String, String>> nPlaceList,Bitmap mIcon){
        for (int i=0;i < nPlaceList.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nPlaceList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(mIcon,100,150,false);
            changeBitmapTintTo(scaledBitmap,Color.CYAN);
            mIcon = scaledBitmap;
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mIcon);

            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);
            Log.i(TAG, "addNearbyPlacesMarkers: element "+i+":"+placeName+" "+vicinity+" "+lat+" "+lng);
        }
    }

    private void changeBitmapTintTo(Bitmap myBitmap, int color){
        int [] allpixels = new int [myBitmap.getHeight() * myBitmap.getWidth()];

        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        for(int i = 0; i < allpixels.length; i++)
        {
            if(allpixels[i] == Color.BLACK)
            {
                allpixels[i] = color;
            }
        }
        myBitmap.setPixels(allpixels,0,myBitmap.getWidth(),0, 0, myBitmap.getWidth(),myBitmap.getHeight());
    }

}
