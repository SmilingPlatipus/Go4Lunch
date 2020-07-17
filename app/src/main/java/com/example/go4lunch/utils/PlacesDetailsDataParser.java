package com.example.go4lunch.utils;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/***************************************************************************************************************************************************************************************************************************
 *                                                                                                                                                                                                                          *
 *                                          This class purpose is to parse a Google Details page containing results as a JSON file                                                                                          *
 *                                                                                                                                                                                                                          *
 ****************************************************************************************************************************************************************************************************************************/

public class PlacesDetailsDataParser
{
    private static final String TAG = "PlacesDetailsDataParser";

    public HashMap<String, String> getRestaurantDetails(String jsonData) throws JSONException {
        HashMap<String, String> detailRow = new HashMap<>();

        String phoneNumber = "";
        String website = "";
        JSONObject jsonObject = new JSONObject(jsonData);

        try{
            if (!jsonObject.getJSONObject("result").getString("formatted_phone_number").isEmpty()) {
                phoneNumber = jsonObject.getJSONObject("result").getString("formatted_phone_number");
                detailRow.put("phone_number",phoneNumber);
            }
        }catch (JSONException e){
            detailRow.put("phone_number","null");
            e.printStackTrace();
        }

        try{
            if (!jsonObject.getJSONObject("result").getString("website").isEmpty()) {
                website = jsonObject.getJSONObject("result").getString("website");
                detailRow.put("website",website);
            }
        }catch (JSONException e){
            detailRow.put("website","null");
            e.printStackTrace();
        }

        Log.i(TAG, "getRestaurantDetails: returning values for this place : phone_number : " + detailRow.get("phone_number") + " website : " + detailRow.get("website"));

        return detailRow;
    }
}
