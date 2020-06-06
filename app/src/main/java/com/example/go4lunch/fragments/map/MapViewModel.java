package com.example.go4lunch.fragments.map;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel
{

    private MutableLiveData<String> mText;
    private Location currentLocation;

    // TODO : voir comment conserver et mettre à jour automatiquement la position courante

    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}