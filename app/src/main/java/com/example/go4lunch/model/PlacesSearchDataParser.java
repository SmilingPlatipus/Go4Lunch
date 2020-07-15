package com.example.go4lunch.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/***************************************************************************************************************************************************************************************************************************
*                                                                                                                                                                                                                          *
*                                          This class purpose is to parse a Google Places Search page containing results as a JSON file                                                                                    *
*                                                                                                                                                                                                                          *
****************************************************************************************************************************************************************************************************************************/
public class PlacesSearchDataParser
{
    String nextPageToken = null;

    // Parsing each place details, fetching all informations needed

    private HashMap<String, String> getPlace(JSONObject googlePlaceJSON){
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "";
        String vicinity = "";
        String latitude = "";
        String longitude = "";
        String placeId = "";
        String photoReference = "";
        String photoWidth = "";
        String rating = "";
        String reference = "";
        String openNow = "";

        try {
            if (!googlePlaceJSON.getString("name").isEmpty()){
                placeName = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.getString("vicinity").isEmpty()){
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");


            placeId = googlePlaceJSON.getString("place_id");
            reference = googlePlaceJSON.getString("reference");

            // Preparing the GooglePlacesMap to return
            googlePlacesMap.put("place_name",placeName);
            googlePlacesMap.put("vicinity",vicinity);
            googlePlacesMap.put("lat",latitude);
            googlePlacesMap.put("lng",longitude);
            googlePlacesMap.put("place_id",placeId);
            googlePlacesMap.put("reference",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if (googlePlaceJSON.has("rating")){
                rating = googlePlaceJSON.getString("rating");
                googlePlacesMap.put("rating",rating);
            }
        } catch (JSONException e) {
            rating = "0";
            googlePlacesMap.put("rating",rating);
            e.printStackTrace();
        }


        try {
            if (googlePlaceJSON.getJSONObject("opening_hours").has("open_now")) {
                openNow = googlePlaceJSON.getJSONObject("opening_hours").getString("open_now");
                googlePlacesMap.put("open_now",openNow);
            }
        } catch (JSONException e) {
            openNow = "false";
            googlePlacesMap.put("open_now",openNow);
            e.printStackTrace();
        }

        try {
            if (!googlePlaceJSON.getJSONArray("photos").getJSONObject(0).isNull("photo_reference")) {
                photoReference = googlePlaceJSON.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                googlePlacesMap.put("photo_reference", photoReference);
            }
        } catch (JSONException e) {
            photoReference = "null";
            googlePlacesMap.put("photo_reference",photoReference);
            e.printStackTrace();
        }

        try {
            if (!googlePlaceJSON.getJSONArray("photos").getJSONObject(0).isNull("width")) {
                photoWidth = googlePlaceJSON.getJSONArray("photos").getJSONObject(0).getString("width");
                googlePlacesMap.put("photo_width", photoWidth);
            }
        } catch (JSONException e) {
            photoWidth = "0";
            googlePlacesMap.put("photo_width", photoWidth);
            e.printStackTrace();
        }

        return googlePlacesMap;
    }

    // Parsing all places in JSON results

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray){
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for(int i =0;i < count;i++){
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return placesList;
    }

    // Parsing entire JSON file, fetching for "next_page_token" and "results" tags

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonData);
            if (!jsonObject.isNull("next_page_token"))
                nextPageToken = jsonObject.getString("next_page_token");
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPlaces(jsonArray);
    }

}
