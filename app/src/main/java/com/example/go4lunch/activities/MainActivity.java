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

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Workmate;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    public static final String TAG = "MainActivity";
    public static final float DEFAULT_ZOOM = 17f;

    // The firebase user logged in the application
    FirebaseUser user;

    // Google maps purpose
    public static GoogleMap mMap;
    public static View mapView;
    public static FusedLocationProviderClient mFusedLocationProviderClient;
    public static Location lastKnownLocation;

    // Google Places autocomplete purpose
    public static PlacesClient placesClient;
    public static List<AutocompletePrediction> predictionList;
    public static MaterialSearchBar materialSearchBar;

    // Navigation drawer widgets
    DrawerLayout mDrawerLayout;
    public static Toolbar mToolbar;
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

    // Firestore options for workmates RecyclerView
    public static FirestoreRecyclerOptions<Workmate> optionsForWorkmatesRecyclerView;



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

        materialSearchBar = findViewById(R.id.searchBar);
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
        optionsForWorkmatesRecyclerView = new FirestoreRecyclerOptions.Builder<Workmate>()
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
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        // Create a new Places client instance
        placesClient = Places.createClient(this);
    }
    public void showAutocompleteSearchBar(View view){

        // Show materialSearchBar, hide regular App Bar
        materialSearchBar.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.GONE);
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener()
        {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(),true,null,true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION){

                }
                else if (buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.disableSearch();
                    materialSearchBar.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setOrigin(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .setCountries("FR")
                        .setTypeFilter(TypeFilter.CITIES)
                        .setSessionToken(token)
                        .setQuery(charSequence.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>()
                {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null){
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List <String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size();i++){
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        }
                        else {
                            Log.i(TAG, "onComplete: prediction failed");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener()
        {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size())
                    return;
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);
                materialSearchBar.clearSuggestions();
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                if (inputManager != null)
                    inputManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);

                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placefields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placefields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>()
                {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "onSuccess: place found is : " + place.getName());
                        LatLng latLngOfPlace = place.getLatLng();
                        if (latLngOfPlace != null)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException){
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i(TAG, "onFailure: place not found : "+ e.getMessage() + " status code : " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
        {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible() || materialSearchBar.isSearchEnabled()) {
                    materialSearchBar.clearSuggestions();
                    materialSearchBar.disableSearch();
                    materialSearchBar.setVisibility(View.GONE);
                    mToolbar.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

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
                logOut();
                onStart();
                break;
        }
        return false;
    }

    public void logOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),getString(R.string.log_out),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}