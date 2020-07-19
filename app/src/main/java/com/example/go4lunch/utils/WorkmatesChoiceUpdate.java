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

import static com.example.go4lunch.activities.MainActivity.firebaseFirestore;
import static com.example.go4lunch.activities.MainActivity.nearbyRestaurant;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;

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
                    updateWorkmatesData(list); // *** new ***
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
            // Get a random restaurant in our list
            Restaurant randomRestaurant = new Restaurant();
            if (k != list.size() - 1) {
                int randomChoice = (int) (Math.random() * (nearbyRestaurant.size() + 1));
                randomRestaurant = nearbyRestaurant.get(randomChoice);
                batch.update(ref, "choice", randomRestaurant.getPlaceId());
                nearbyRestaurant.get(randomChoice).setWorkmatesCount(nearbyRestaurant.get(randomChoice).getWorkmatesCount() + 1);
            }
            else {
                // Init last workmate with "null" value, for testing purpose
                batch.update(ref,"choice","null");
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
