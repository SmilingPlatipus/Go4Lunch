package com.example.go4lunch.model;


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
    public String getRestaurantDetails(String jsonData) throws JSONException {
        HashMap<String, String> phoneRow = new HashMap<>();

        String phoneNumber = "";
        JSONObject jsonObject = new JSONObject(jsonData);

        if (!jsonObject.getJSONObject("result").getString("formatted_phone_number").isEmpty()) {
            phoneNumber = jsonObject.getJSONObject("result").getString("formatted_phone_number");
        }


        return phoneNumber;
    }
}
