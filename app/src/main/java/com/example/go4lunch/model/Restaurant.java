package com.example.go4lunch.model;

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
    int distanceFromUser;
    int workmatesCount;
    int rating;
    boolean isOpenedNow;


    public Restaurant() {
    }

    public Restaurant(String id, String placeId, String name, String address, String phoneNumber, @Nullable String websiteUrl, @Nullable String imageUrl,
                      int photoMaxWidth, int distanceFromUser, int workmatesCount, int rating, boolean isOpenedNow) {
        this.id = id;
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.websiteUrl = websiteUrl;
        this.imageUrl = imageUrl;
        this.photoMaxWidth = photoMaxWidth;
        this.distanceFromUser = distanceFromUser;
        this.workmatesCount = workmatesCount;
        this.rating = rating;
        this.isOpenedNow = isOpenedNow;
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

    public int getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(int distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public int getWorkmatesCount() {
        return workmatesCount;
    }

    public void setWorkmatesCount(int workmatesCount) {
        this.workmatesCount = workmatesCount;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
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
}
