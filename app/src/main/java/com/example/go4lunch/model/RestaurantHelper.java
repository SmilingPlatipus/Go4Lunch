package com.example.go4lunch.model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHelper
{
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String uid, String restaurantName, String urlPicture) {
        Restaurant restaurantToCreate = new Restaurant();
        return RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String id){
        return RestaurantHelper.getRestaurantsCollection().document(id).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateRestaurantName(String restaurantName, String id) {
        return RestaurantHelper.getRestaurantsCollection().document(id).update("name", restaurantName);
    }

    public static Task<Void> updateIsMentor(String uid, Boolean isMentor) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).update("isMentor", isMentor);
    }

}
