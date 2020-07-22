package com.example.go4lunch.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.multidex.MultiDex;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.example.go4lunch.fragments.map.MapStateManager;
import com.example.go4lunch.utils.GetCustomMarkerIcon;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.Workmate;
import com.example.go4lunch.utils.RestaurantMarkersHandler;
import com.example.go4lunch.utils.ManageUserAccountOnFirestore;
import com.example.go4lunch.utils.WorkmatesChoiceUpdate;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.libraries.places.api.model.RectangularBounds;
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
import java.util.HashMap;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.go4lunch.fragments.map.MapFragment.RESTAURANT_INDEX;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener
{
    public static final String TAG = "MainActivity";
    public static final float DEFAULT_ZOOM = 15f;
    public static final int PROXIMITY_RADIUS = 10000;
    private static final double SOUTHWEST_LAT_BOUND = 0.055;
    private static final double NORTHEAST_LNG_BOUND = 0.075;
    private static final int REQUEST_CHECK_SETTINGS = 13;
    private static int PERMISSION_ALL = 1;

    public static LocationManager locationManager;

    // The firebase user logged in the application
    FirebaseUser user;

    // Google maps purpose
    public static GoogleMap mMap;
    public static MapStateManager savedMapState;
    public static View mapView;
    public static FusedLocationProviderClient mFusedLocationProviderClient;
    public static Location lastKnownLocation;
    public static Bitmap customRestaurantBitmap = null;
    public static int indexOfRestaurantToGetDetails;

    public static List<HashMap<String, String>> nearbyRestaurantList = new ArrayList();
    public static List<Restaurant> nearbyRestaurant = new ArrayList<>();
    public static String userChoice = null;

    // This is a fake configuration for local testing purposes
    public static boolean fakeConfig = true;
    public static int tokenNumber = 2;

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
    public static Uri userPhotoUrl;
    public static String userFirstName;
    String userLastName, userEmail;

    // Firebase database reference
    public static FirebaseFirestore firebaseFirestore;
    public static CollectionReference workmatesReference;
    public static String userDocumentID = null;

    // Firestore options for workmates RecyclerView
    public static FirestoreRecyclerOptions<Workmate> optionsForWorkmatesRecyclerView;
    public static FirestoreRecyclerOptions<Workmate> optionsForWorkmatesEatingInThisRestaurant;

    // Multidex purposes
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Getting custom Bitmap from URL
        if (customRestaurantBitmap == null) {
            Object customMarkerUrl[] = new Object[1];
            customMarkerUrl[0] = getString(R.string.custom_marker_url);

            GetCustomMarkerIcon getCustomMarkerIcon = new GetCustomMarkerIcon();
            getCustomMarkerIcon.execute(customMarkerUrl);
        }

        // Checking if user is logged on and geolocation permissions are granted.
        // While it's not the case, application is waiting for it

        if (checkCurrentUser() != null && checkPermissions()) {
            // Geolocating and searching for Restaurants in the neighborhood, in background
            getDeviceLocation();
            if (lastKnownLocation == null){
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(createLocationRequest());
                SettingsClient client = LocationServices.getSettingsClient(this);
                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // All location settings are satisfied. The client can initialize
                        // location requests here.
                        getDeviceLocation();
                    }
                });

                task.addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        }
                    }
                });
                this.recreate();
            }
            else {
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

                // Access the workmates collection on Firestore and keep real time synced
                firebaseFirestore = FirebaseFirestore.getInstance();
                workmatesReference = firebaseFirestore.collection("workmates");
                // Query to database
                Query query = workmatesReference.orderBy("name", Query.Direction.DESCENDING);
                // Recycler Options
                optionsForWorkmatesRecyclerView = new FirestoreRecyclerOptions.Builder<Workmate>()
                        .setQuery(query, Workmate.class)
                        .build();

                initGooglePlaces();
                getUserProfile();
                getProfileData();
                applyProfileDataOnHeader();

                // Instancing new utilitary class to create user on Firestore if needed
                ManageUserAccountOnFirestore manageUserAccountOnFirestore = new ManageUserAccountOnFirestore();
                manageUserAccountOnFirestore.checkIfUserIsAlreadyCreated();

                // Instancing new utilitary class to update workmates collection
                WorkmatesChoiceUpdate workmatesChoiceUpdate = new WorkmatesChoiceUpdate();

                // Searching for restaurants in the current area, with Google Places requests
                RestaurantMarkersHandler restaurantMarkersHandler = new RestaurantMarkersHandler(this, workmatesChoiceUpdate);
                restaurantMarkersHandler.getRestaurantsLocations();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            finish();
            startActivity(intent);
        }
        if (!checkPermissions()) {
            this.recreate();
        }
    }

    private boolean checkPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplication().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getApplication().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS,PERMISSION_ALL);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ALL) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,
                                   "Access " + permissions[i] + " permission Granted",
                                   Toast.LENGTH_SHORT)
                            .show();
                }
                else
                    this.recreate();
            }
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    public void initProfileInformations() {
        // Initializing drawer header

        View mHeader = mDrawerNavigationView.getHeaderView(0);
        userProfileImage = mHeader.findViewById(R.id.header_profile_pic);
        userProfileFirstName = mHeader.findViewById(R.id.header_first_name);
        userProfileLastName = mHeader.findViewById(R.id.header_last_name);
        userProfileEmail = mHeader.findViewById(R.id.header_email);
    }

    public void initGooglePlaces() {
        // Initialize the Google Places SDK
        Places.initialize(this, getString(R.string.google_api_key));
        // Create a new Places client instance
        placesClient = Places.createClient(this);
    }

    public void showAutocompleteSearchBar(View view) {
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
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {

                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
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
                RectangularBounds bounds = RectangularBounds.newInstance(
                        new LatLng(lastKnownLocation.getLatitude() - SOUTHWEST_LAT_BOUND, lastKnownLocation.getLongitude() - NORTHEAST_LNG_BOUND),
                        new LatLng(lastKnownLocation.getLatitude() + SOUTHWEST_LAT_BOUND, lastKnownLocation.getLongitude() + NORTHEAST_LNG_BOUND)
                );
                // Use the builder to create a FindAutocompletePredictionsRequest.
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setLocationRestriction(bounds)
                        .setOrigin(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setCountries("FR")
                        .setQuery(charSequence.toString())
                        .build();

                placesClient.findAutocompletePredictions(request).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>()
                {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                                        suggestionsList.add(prediction.getFullText(null).toString());
                                    }
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
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
                    inputManager.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

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
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i(TAG, "onFailure: place not found : " + e.getMessage() + " status code : " + statusCode);
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
                userEmail = profile.getEmail();
                userPhotoUrl = profile.getPhotoUrl();
            }
        }
    }

    public void getIdentity(String userIdentity) {
        userFirstName = userLastName = null;
        int lastSpacePosition = 0;
        int index = 0;

        // Determining where is the last space character before lastname
        for (char currentLetter : userIdentity.toCharArray()) {
            if (currentLetter == ' ')
                lastSpacePosition = index;
            index++;
        }

        // Retrieving firstname and lastname
        userFirstName = userIdentity.substring(0, lastSpacePosition);
        userLastName = userIdentity.substring(lastSpacePosition + 1);
    }

    public void applyProfileDataOnHeader() {
        // Apply profile informations into drawer layout
        Glide.with(this)
                .load(userPhotoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userProfileImage);

        userProfileFirstName.setText(userFirstName);
        userProfileLastName.setText(userLastName);
        userProfileEmail.setText(userEmail);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.current_lunch:
                if (userDocumentID != null){
                    Restaurant currentRestaurant = new Restaurant();
                    currentRestaurant = Restaurant.searchById(userChoice);
                    Intent detailRestaurantActivity = new Intent(this, DetailRestaurantActivity.class);
                    detailRestaurantActivity.putExtra(RESTAURANT_INDEX, nearbyRestaurantList.indexOf(currentRestaurant));

                    // Setting options for cloud firestore
                    Query query = workmatesReference.whereEqualTo("choice", userChoice);
                    // Recycler Options
                    optionsForWorkmatesEatingInThisRestaurant = new FirestoreRecyclerOptions.Builder<Workmate>()
                            .setQuery(query,Workmate.class)
                            .build();

                    startActivity(detailRestaurantActivity);
                }
                break;
            case R.id.settings:
                Toast.makeText(this, getString(R.string.drawer_item_settings) + " selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.log_out:
                logOut();
                Intent intent = new Intent(MainActivity.this, SigninActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }

    public void logOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplication().getApplicationContext(), getString(R.string.log_out), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDeviceLocation() {
        // Setting the location of the user to its current position by default
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: found location : " + location.getResult().toString());
                        lastKnownLocation = (Location) task.getResult();
                    } else {
                        Log.d(TAG, "onComplete: current location not found");
                        Toast.makeText(getApplication().getApplicationContext(), getString(R.string.user_current_location_not_found), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

}