package com.example.go4lunch.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static com.example.go4lunch.activities.MainActivity.userChoice;
import static com.example.go4lunch.activities.MainActivity.userFirstName;
import static com.example.go4lunch.activities.MainActivity.userPhotoUrl;
import static com.example.go4lunch.activities.MainActivity.workmatesReference;
import static com.example.go4lunch.activities.MainActivity.userDocumentID;

public class ManageUserAccountOnFirestore
{
    private static final String TAG = "UserChoiceUpdate";

    public void checkIfUserIsAlreadyCreated() {
        workmatesReference.whereEqualTo("name", userFirstName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                userDocumentID = document.getId();
                                userChoice = (String) document.get("choice");
                            }
                            if (userDocumentID == null)
                                createUserOnFirestore();
                        } else {
                            Log.d(TAG, "Creating user on Firestore : ");
                        }
                    }
                });
    }

    public void createUserOnFirestore() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", userFirstName);
        data.put("image", userPhotoUrl.toString());
        data.put("choice", "null");
        workmatesReference.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                userDocumentID = documentReference.getId();
            }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });

    }

}
