package com.example.go4lunch.model;

import javax.annotation.Nullable;

public class Restaurant
{
    String id;
    String placeId;
    String name;
    String address;
    @Nullable String photoReference;
    @Nullable String websiteUrl;
    @Nullable String imageUrl;

    @Nullable int photoMaxWidth;
    int distanceFromUser;
    int workmatesCount;
    int rating;
    int phoneNumber;


    // Todo : handle opening hours

    public Restaurant() {
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
    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(@Nullable String photoReference) {
        this.photoReference = photoReference;
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

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
