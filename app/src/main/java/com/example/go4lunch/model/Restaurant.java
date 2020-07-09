package com.example.go4lunch.model;

import android.location.Location;

import java.util.HashMap;

import javax.annotation.Nullable;

public class Restaurant
{
    String id;
    String placeId;
    String name;
    String address;
    String phoneNumber;
    @Nullable String websiteUrl;
    @Nullable String imageUrl;

    @Nullable int photoMaxWidth;
    double distanceFromUser;
    int workmatesCount;
    float rating;
    boolean isOpenedNow;
    boolean isChosen;


    public Restaurant() {
    }

    public Restaurant(HashMap<String, String> restaurant, Location latLng){
        this.id = restaurant.get("reference");
        this.placeId = restaurant.get("place_id");
        this.name = restaurant.get("place_name");
        this.address = restaurant.get("vicinity");
        this.phoneNumber = restaurant.get("phone_number");
        this.websiteUrl = restaurant.get("website");
        this.imageUrl = restaurant.get("photo_url");
        this.photoMaxWidth = Integer.parseInt(restaurant.get("photo_width"));
        this.workmatesCount = 0;
        this.rating = Float.parseFloat(restaurant.get("rating"));
        this.isOpenedNow = Boolean.parseBoolean(restaurant.get("open_now"));
        this.isChosen = false;

        float [] result = new float[1];
        Location.distanceBetween(latLng.getLatitude(),latLng.getLongitude(),Double.parseDouble(restaurant.get("lat")),Double.parseDouble(restaurant.get("lng")),result);
        this.distanceFromUser = (double) result[0];
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    @Nullable
    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(@Nullable String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@Nullable String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPhotoMaxWidth() {
        return photoMaxWidth;
    }

    public void setPhotoMaxWidth(int photoMaxWidth) {
        this.photoMaxWidth = photoMaxWidth;
    }

    public double getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public int getWorkmatesCount() {
        return workmatesCount;
    }

    public void setWorkmatesCount(int workmatesCount) {
        this.workmatesCount = workmatesCount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOpenedNow() {
        return isOpenedNow;
    }

    public void setOpenedNow(boolean openedNow) {
        isOpenedNow = openedNow;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }
}
