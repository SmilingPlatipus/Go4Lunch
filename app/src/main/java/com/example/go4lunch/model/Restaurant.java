package com.example.go4lunch.model;

public class Restaurant
{
    String name, address, imageUrl;
    int distanceFromUser, workmatesCount, ratings;

    // Todo : handle opening hours

    public Restaurant(String name, String address, String imageUrl, int distanceFromUser, int workmatesCount, int ratings) {
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.distanceFromUser = distanceFromUser;
        this.workmatesCount = workmatesCount;
        this.ratings = ratings;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

}
