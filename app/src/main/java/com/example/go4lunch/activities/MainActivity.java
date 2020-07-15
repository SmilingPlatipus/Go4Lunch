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
import android.graphics.Color;
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
import com.example.go4lunch.model.GetCustomMarkerIcon;
import com.example.go4lunch.model.NearbyRestaurants;
import com.example.go4lunch.model.PlaceDetails;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.Workmate;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationListener,
                                                               NearbyRestaurants.NearbyRestaurantsResponse, PlaceDetails.PlaceDetailsResponse
{
    public static final String TAG = "MainActivity";
    public static final float DEFAULT_ZOOM = 15f;
    private static final int PROXIMITY_RADIUS = 10000;
    private static final double SOUTHWEST_LAT_BOUND = 0.055;
    private static final double NORTHEAST_LNG_BOUND = 0.075;
    private static final int ACCESS_FINE_LOCATION_CODE = 10;
    private static final int ACCESS_COARSE_LOCATION_CODE = 11;
    private static final int REQUEST_CHECK_SETTINGS = 13;

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
    int indexOfRestaurantToGetDetails = 0;

    public static List<HashMap<String, String>> nearbyRestaurantList = new ArrayList();
    public static List<Restaurant> nearbyRestaurant = new ArrayList<>();

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
    Uri photoUrl;
    String firstName, lastName, email;

    // Firebase database reference
    public static FirebaseFirestore firebaseFirestore;
    public static CollectionReference workmatesReference;

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


                // Searching for restaurants in the current area, with Google Places requests
                getRestaurantsLocations();
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
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
                return true;
            }
            if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
                return true;
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
                return true;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, ACCESS_FINE_LOCATION_CODE);
            return false;
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
                return true;
            }
            if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
                return true;
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
                return true;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, ACCESS_COARSE_LOCATION_CODE);
            return false;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                               "Access fine location permission Granted",
                               Toast.LENGTH_SHORT)
                        .show();
            } else
                checkPermissions();
        }

        if (requestCode == ACCESS_COARSE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                               "Access coarse location permission Granted",
                               Toast.LENGTH_SHORT)
                        .show();
            } else
                checkPermissions();
        }
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
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
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
                email = profile.getEmail();
                photoUrl = profile.getPhotoUrl();
            }
        }

    }

    public void getIdentity(String userIdentity) {
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
        firstName = userIdentity.substring(0, lastSpacePosition);
        lastName = userIdentity.substring(lastSpacePosition + 1);
    }

    public void applyProfileDataOnHeader() {

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
        switch (item.getItemId()) {
            case R.id.current_lunch:
                Toast.makeText(this, getString(R.string.drawer_item_your_lunch) + " selected", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), getString(R.string.log_out), Toast.LENGTH_SHORT).show();
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

    private void getRestaurantsLocations() {
        if (!fakeConfig) {
            String url = getPlacesSearchUrl(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), "restaurant");
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
            dataTransfer[1] = url;

            NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
            NearbyRestaurants.pageCount = 1;
            Log.i(TAG, "getRestaurantsLocations: task : " + NearbyRestaurants.pageCount + " executing...");
            nearbyRestaurants.execute(dataTransfer);
        }
        else{
            Object dataTransfer[] = new Object[2];
            dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
            dataTransfer[1] = loadJSONFromAsset(getApplicationContext(),"places_search_results_cahors_page_1");

            NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
            NearbyRestaurants.pageCount = 1;
            Log.i(TAG, "getRestaurantsLocations: task : " + NearbyRestaurants.pageCount + " executing...");
            nearbyRestaurants.execute(dataTransfer);
        }
    }

    public static void changeBitmapTintTo(Bitmap myBitmap, int color){
        int [] allpixels = new int [myBitmap.getHeight() * myBitmap.getWidth()];

        myBitmap.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());

        for(int i = 0; i < allpixels.length; i++)
        {
            if(allpixels[i] == Color.BLACK)
            {
                allpixels[i] = color;
            }
        }
        myBitmap.setPixels(allpixels,0,myBitmap.getWidth(),0, 0, myBitmap.getWidth(),myBitmap.getHeight());
    }

    public static void addCustomRestaurantMarker(HashMap<String, String> googlePlace, Bitmap mIcon){

        MarkerOptions markerOptions = new MarkerOptions();

        String placeName = googlePlace.get("place_name");
        String vicinity = googlePlace.get("vicinity");
        double lat = Double.parseDouble(googlePlace.get("lat"));
        double lng = Double.parseDouble(googlePlace.get("lng"));

        LatLng latLng = new LatLng(lat,lng);
        markerOptions.position(latLng);
        markerOptions.title(placeName);
        markerOptions.snippet(vicinity);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mIcon,100,150,false);
        changeBitmapTintTo(scaledBitmap, Color.CYAN);
        mIcon = scaledBitmap;
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(mIcon);

        markerOptions.icon(icon);
        mMap.addMarker(markerOptions);
    }

    private String getPlacesSearchUrl(double latitude, double longitude, String nearbyPlace){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + getString(R.string.google_api_key));

        return googlePlaceUrl.toString();
    }

    public String getPlacesSearchNextPageUrl(String nextPageToken){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("&key=" + getString(R.string.google_api_key));
        googlePlaceUrl.append("&pagetoken=" + nextPageToken);

        return googlePlaceUrl.toString();
    }

    private String makePlacesDetailsRequest(String placeId){
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceUrl.append("&key=" + getApplicationContext().getString(R.string.google_api_key));
        googlePlaceUrl.append("&place_id=" + placeId);

        return googlePlaceUrl.toString();
    }

    private String makePlacesPhotoRequest(String photoReference, String maxWidth){
        if (photoReference.compareTo("null") == 0 || maxWidth.compareTo("0") == 0)
            return "null";
        else {
            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
            googlePlaceUrl.append("maxwidth=" + maxWidth);
            googlePlaceUrl.append("&photoreference=" + photoReference);
            googlePlaceUrl.append("&key=" + getApplicationContext().getString(R.string.google_api_key));

            return googlePlaceUrl.toString();
        }
    }

    // Callback method from NearbyPlacesSearch Asynctask
    @Override
    public void onNearbyRestaurantsCompleted(String nextPageToken) {
        // Making Places Details request to get more informations about all restaurants treated in previous thread

        Iterator iterator = nearbyRestaurantList.iterator();
        HashMap<String, String> currentRestaurant = new HashMap<>();
        do{
            currentRestaurant = (HashMap<String, String>) iterator.next();

        }while(currentRestaurant != nearbyRestaurantList.get(indexOfRestaurantToGetDetails));

        while (iterator.hasNext()) {
                Object transferObject[] = new Object[3];
                transferObject[0] = (String) makePlacesDetailsRequest(currentRestaurant.get("place_id"));
                transferObject[1] = (String) makePlacesPhotoRequest(currentRestaurant.get("photo_reference"), currentRestaurant.get("photo_width"));
                transferObject[2] = (int) indexOfRestaurantToGetDetails;
                PlaceDetails placesDetails = new PlaceDetails(this);
                placesDetails.execute(transferObject);
                indexOfRestaurantToGetDetails++;
                currentRestaurant = (HashMap<String, String>) iterator.next();
        }
        Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " ending...");

        // Then making custom Places Search request with nextpagetoken in another thread
        if (nextPageToken != null) {
            if (!fakeConfig) {
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
                dataTransfer[1] = getPlacesSearchNextPageUrl(nextPageToken);

                NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
                Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " executing...");
                nearbyRestaurants.execute(dataTransfer);
            }
            else {
                Object dataTransfer[] = new Object[2];
                dataTransfer[0] = getApplicationContext().getString(R.string.google_api_key);
                dataTransfer[1] = loadJSONFromAsset(getApplicationContext(),"places_search_results_cahors_page_" + tokenNumber);

                NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
                Log.i(TAG, "onProcessFinished: task : " + NearbyRestaurants.pageCount + " executing...");
                nearbyRestaurants.execute(dataTransfer);
                tokenNumber++;
            }
        }
    }

    // This is for fake configuration purpose
    public String loadJSONFromAsset(Context context, String file_name) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(file_name + ".json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    public void onPlaceDetailsCompleted(HashMap<String, String> googlePlace, Bitmap mIcon) {
        // Adding markers, one by one to the map
        addCustomRestaurantMarker(googlePlace,mIcon);
        // Finally creating one by one our restaurants
        nearbyRestaurant.add(new Restaurant(googlePlace, lastKnownLocation));
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

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }
}