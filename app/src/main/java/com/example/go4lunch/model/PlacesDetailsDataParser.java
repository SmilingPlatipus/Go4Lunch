package com.example.go4lunch.model;


import org.json.JSONException;
import org.json.JSONObject;



/***************************************************************************************************************************************************************************************************************************
 *                                                                                                                                                                                                                          *
 *                                          This class purpose is to parse a Google Details page containing results as a JSON file                                                                                          *
 *                                                                                                                                                                                                                          *
 ****************************************************************************************************************************************************************************************************************************/

public class PlacesDetailsDataParser
{
    public String getRestaurantDetails(String jsonData) throws JSONException {
        JSONObject jsonObject = null;

        String phoneNumber = "";

        try {
            jsonObject.getJSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!jsonObject.getJSONObject("result").getString("formatted_phone_number").isEmpty()) {
            phoneNumber = jsonObject.getJSONObject("result").getString("formatted_phone_number");
        }

        return phoneNumber;
    }
}
