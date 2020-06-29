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
    public HashMap<String, String> getRestaurantDetails(String jsonData) throws JSONException {
        HashMap<String, String> phoneRow = new HashMap<>();

        String phoneNumber = "";
        String website = "";
        JSONObject jsonObject = new JSONObject(jsonData);

        if (!jsonObject.getJSONObject("result").getString("formatted_phone_number").isEmpty()) {
            phoneNumber = jsonObject.getJSONObject("result").getString("formatted_phone_number");
        }

        if (!jsonObject.getJSONObject("result").getString("website").isEmpty()) {
            website = jsonObject.getJSONObject("result").getString("website");
        }
        phoneRow.put("phone_number",phoneNumber);
        phoneRow.put("website",website);


        return phoneRow;
    }
}
