package com.example.go4lunch.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import static com.example.go4lunch.activities.MainActivity.customRestaurantBitmap;
import static com.example.go4lunch.activities.MainActivity.firebaseFirestore;
import static com.example.go4lunch.models.Restaurant.nearbyRestaurant;
import static com.example.go4lunch.activities.MainActivity.userDocumentID;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.SELECTED_MARKER_COLOR;
import static com.example.go4lunch.utils.RestaurantMarkersHandler.addCustomRestaurantMarker;

public class WorkmatesChoiceUpdate implements RestaurantMarkersHandler.RestaurantMarkersHandlerCallback
{
    private static final String TAG = "WorkmatesChoiceUpdate";

    void getWorkmatesDataThenUpdate() {
        workmatesReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    Log.d(TAG, list.toString());
                    updateWorkmatesData(list);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void updateWorkmatesData(List list) {

        // Get a new write batch
        WriteBatch batch = firebaseFirestore.batch();

        // Iterate through the list
        for (int k = 0; k < list.size(); k++) {
            // Update each list item
            DocumentReference ref = firebaseFirestore.collection("workmates").document((String) list.get(k));

            // If the current document is not the user document, else we do nothing
            if (userDocumentID == null || userDocumentID.compareTo((String) list.get(k)) != 0) {

                int finalK = k;

                    // Get a random restaurant in our list
                    Restaurant randomRestaurant = new Restaurant();
                    int randomChoice = (int) (Math.random() * nearbyRestaurant.size() );
                    randomRestaurant = nearbyRestaurant.get(randomChoice);
                    batch.update(ref, "choice", randomRestaurant.getPlaceId());
                    nearbyRestaurant.get(randomChoice).setWorkmatesCount(nearbyRestaurant.get(randomChoice).getWorkmatesCount() + 1);
                    addCustomRestaurantMarker(randomRestaurant.getInstanceAsHashMap(), customRestaurantBitmap, SELECTED_MARKER_COLOR);
            }
        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "onComplete: workmates choices updated successfully");
            }
        });
    }

    @Override
    public void initRandomWorkmateChoice() {
        getWorkmatesDataThenUpdate();
    }

}
