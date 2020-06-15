package com.example.go4lunch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.multidex.MultiDex;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String TAG = "MainActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 5487;
    PlacesClient placesClient;
    public static GoogleMap mMap;
    public static FusedLocationProviderClient mFusedLocationProviderClient;
    FirebaseUser user;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    NavigationView mDrawerNavigationView;
    ActionBarDrawerToggle toggle;

    // User Profile Widgets
    ImageView userProfileImage;
    TextView userProfileFirstName, userProfileLastName, userProfileEmail;

    // User Profile data
    Uri photoUrl;
    String firstName, lastName, email;

    // Firebase database reference
    public static FirebaseFirestore firebaseFirestore;
    private CollectionReference workmatesReference;

    // Firestore options for RecyclerView
    public static FirestoreRecyclerOptions<Workmate> options;



    // Multidex purposes
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerNavigationView = findViewById(R.id.drawer_navigationview);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_workmates)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerNavigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.quantum_white_100));
        toggle.syncState();

        initProfileInformations();

        initGooglePlaces();

        // Access the workmates collection on Firestore and keep real time synced
        firebaseFirestore = FirebaseFirestore.getInstance();
        workmatesReference = firebaseFirestore.collection("workmates");
        // Query to database
        Query query = workmatesReference.orderBy("name",Query.Direction.DESCENDING);
        // Recycler Options
        options = new FirestoreRecyclerOptions.Builder<Workmate>()
                .setQuery(query,Workmate.class)
                .build();
    }

    public void initProfileInformations(){
        // Initializing drawer header

        View mHeader =mDrawerNavigationView.getHeaderView(0);
        userProfileImage = mHeader.findViewById(R.id.header_profile_pic);
        userProfileFirstName = mHeader.findViewById(R.id.header_first_name);
        userProfileLastName = mHeader.findViewById(R.id.header_last_name);
        userProfileEmail = mHeader.findViewById(R.id.header_email);
    }
    public void initGooglePlaces(){
        // Initialize the Google Places SDK
        Places.initialize(getApplicationContext(), "AIzaSyBOwjS7p-6cBR9VxkDCO8inN63dlRi5Vyc");
        // Create a new Places client instance
        placesClient = Places.createClient(this);
    }
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkCurrentUser() == null ) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);
        }
        else {
            getUserProfile();
            getProfileData();
            applyProfileDataOnHeader();
        }
    }

    public FirebaseUser checkCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            return user;
        } else {
            // No user is signed in
            return null;
        }
    }

    public void getUserProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getProfileData() {

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                getIdentity(name);
                email = profile.getEmail();
                photoUrl = profile.getPhotoUrl();
            }
        }

    }

    public void getIdentity(String userIdentity){
        firstName = lastName = null;
        int lastSpacePosition = 0;
        int index = 0;

        // Determining where is the last space character before lastname
        for (char currentLetter : userIdentity.toCharArray()) {
            if (currentLetter == ' ')
                lastSpacePosition = index;
            index++;
        }

        // Retrieving firstname and lastname
        firstName = userIdentity.substring(0,lastSpacePosition);
        lastName = userIdentity.substring(lastSpacePosition+1);



    }

    public void applyProfileDataOnHeader(){

        // Apply profile informations into drawer layout
        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userProfileImage);

        userProfileFirstName.setText(firstName);
        userProfileLastName.setText(lastName);
        userProfileEmail.setText(email);
    }

    public void startAutoCompleteActivity(View view) {

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                Toast.makeText(getApplicationContext(),"Place: " + place.getName() + ", " + place.getId(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });



        mToolbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.current_lunch:
                Toast.makeText(this,getString(R.string.drawer_item_your_lunch) + " selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this,getString(R.string.drawer_item_settings)+" selected",Toast.LENGTH_SHORT).show();
                break;
            case R.id.log_out:
                Toast.makeText(this,getString(R.string.drawer_item_log_out)+" selected",Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}