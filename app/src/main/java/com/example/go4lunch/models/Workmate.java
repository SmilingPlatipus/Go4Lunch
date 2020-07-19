package com.example.go4lunch.models;

public class Workmate
{
    private String name;
    private String image;
    private String choice;

    public Workmate(){};
    public Workmate(String name, String image, String choice) {
        this.name = name;
        this.image = image;
        this.choice = choice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
