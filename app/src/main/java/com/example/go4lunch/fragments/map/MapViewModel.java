package com.example.go4lunch.fragments.map;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel
{

    private MutableLiveData<Location> currentLocation;

    // TODO : voir comment conserver et mettre à jour automatiquement la position courante

    public MapViewModel() {
        currentLocation = new MutableLiveData<>();
    }

    public LiveData<Location> getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(MutableLiveData<Location> location) { this.currentLocation = location;}
}