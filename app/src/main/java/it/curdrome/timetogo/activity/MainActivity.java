package it.curdrome.timetogo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.connection.google.DirectionAsyncTask;
import it.curdrome.timetogo.connection.google.DirectionResponse;
import it.curdrome.timetogo.connection.google.PlacesAsyncTask;
import it.curdrome.timetogo.connection.google.PlacesResponse;
import it.curdrome.timetogo.connection.server.CategoriesAsyncTask;
import it.curdrome.timetogo.connection.server.CategoriesResponse;
import it.curdrome.timetogo.connection.server.PoisByCategoryAsyncTask;
import it.curdrome.timetogo.connection.server.PoisByCategoryResponse;
import it.curdrome.timetogo.fab.FloatingActionButton;
import it.curdrome.timetogo.fab.FloatingActionMenu;
import it.curdrome.timetogo.fragment.PlaceFragment;
import it.curdrome.timetogo.fragment.PoiFragment;
import it.curdrome.timetogo.fragment.RouteFragment;
import it.curdrome.timetogo.fragment.RouteMiniFragment;
import it.curdrome.timetogo.fragment.TransitFragment;
import it.curdrome.timetogo.model.Poi;
import it.curdrome.timetogo.model.Route;
import it.curdrome.timetogo.model.Transit;



/**
 This class is the main class of the "TimeToGo" application.

 This main class
 creates and initialises the map fragment
 handles permission requests, listners of the map



 @author Drob Adrian Mihai
 @version 1.9
 */

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        CategoriesResponse,
        DirectionResponse ,
        PoisByCategoryResponse,
        PlacesResponse ,
        ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String TAG = "MainActivity";
    private static final int MY_REQUEST_POSITION = 0;
    public GoogleApiClient mGoogleApiClient;
    //origin and destination to generate direction
    public LatLng mOrigin;
    public LatLng mDestination;
    public ProgressDialog pDialog; // to show when direction create
    // preferences
    public SharedPreferences sharedPref;
    boolean doubleBackToExitPressedOnce = false;
    // fragment variables
    private SupportMapFragment mapFragment;
    private FragmentManager mFragmentManager;
    private FrameLayout frameLayout;
    private Handler handler = new Handler();
    // types of routes
    private List<Route> walkingRoutes = new ArrayList<>();
    private List<Route> transitRoutes = new ArrayList<>();

    private int transitRouteNr = 0;
    private int walkingRouteNr = 0;
    private Route currentRoute;
    private List<Poi> pois = new ArrayList<>();
    private List<it.curdrome.timetogo.model.Place> places = new ArrayList<>();
    private Poi selectedPoi;
    private Transit selectedTransit;
    private Place selectedPlace;
    private it.curdrome.timetogo.model.Place selectedMyPlace;
    // categories of drawer
    private String[] categories;
    // drawer variables
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private Menu menu;
    // map variables
    private GoogleMap mMap;
    private Marker originMarker;
    private Marker destinationMarker;
    private Marker selectedPlaceMarker;
    private Circle mCircle;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private LatLng defaultLatLng = new LatLng (0,0); //default position
    private float zoomLevel = 11; // default zoom level
    private MainActivity activity = (MainActivity) this;
    // buttons
    private it.curdrome.timetogo.fab.FloatingActionMenu originButton;
    private FloatingActionMenu floatingActionMenu;
    private it.curdrome.timetogo.fab.FloatingActionButton transitButton;
    private it.curdrome.timetogo.fab.FloatingActionButton walkingButton;
    private it.curdrome.timetogo.fab.FloatingActionButton detailsButton;
    private it.curdrome.timetogo.fab.FloatingActionButton closeButton;
    private boolean detailsButtonClicked = false;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private AdView mAdView;



    /**
     * This method check mobile is connected to network.
     *
     * @param context
     * @return true if connected otherwise false.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected();
    }

    /**
     * custom animation for floatingActionMenu
     * @author Alessandro Curreli
     */
    private void animatefloatingActionMenu() {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                floatingActionMenu.getMenuIconView().setImageResource(floatingActionMenu.isOpened()
                        ? android.R.drawable.ic_dialog_map : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        floatingActionMenu.setIconToggleAnimatorSet(set);
    }

    /**
     *Method that creates the main activity and the drawer with its listeners
     *check if internet connection is available
     *creates fragment for map
     *creates fragment for search bar and its listner
     *@param savedInstanceState   instance bundle to be saved
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-4009677260795454/7691723127");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.ad_view);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        // when ad loaded set the view visible
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });


        // getting preferences
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);// preferences

        // creation of the toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        final ImageButton navigationDrawerButton = (ImageButton) findViewById(R.id.navigation_drawer_button);
        navigationDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // creation of the frame layout where loads the map
        frameLayout = (FrameLayout) findViewById(R.id.frame_main);
        detailsButton = (FloatingActionButton) findViewById(R.id.details_fab);
        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!detailsButtonClicked ) {
                    resizeMap(0);
                    detailsButton.setImageResource(android.R.drawable.arrow_down_float);
                    detailsButtonClicked = true;

                    FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                    RouteFragment fragment = new RouteFragment();
                    if(fTransaction.isEmpty()){
                        frameLayout.removeAllViews();
                        fTransaction.add(R.id.frame_main, fragment);
                    }

                    else {
                        frameLayout.removeAllViews();
                        fTransaction.replace(R.id.frame_main, fragment);
                    }
                    fTransaction.commit();
                }else{
                    resizeMap(80);
                    detailsButton.setImageResource(android.R.drawable.arrow_up_float);
                    detailsButtonClicked = false;
                    FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                    RouteMiniFragment fragment = new RouteMiniFragment();
                    if(fTransaction.isEmpty()){
                        frameLayout.removeAllViews();
                        fTransaction.add(R.id.frame_main, fragment);
                    }

                    else {
                        frameLayout.removeAllViews();
                        fTransaction.replace(R.id.frame_main, fragment);
                    }
                    fTransaction.commit();
                }
            }
        });

        // creation of the floating menu and hide it to be ready when a route is generated
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        floatingActionMenu.hideMenuButton(false);

        // creation of the buttons inside the menu
        walkingButton = (it.curdrome.timetogo.fab.FloatingActionButton) findViewById(R.id.walking_button);
        transitButton = (it.curdrome.timetogo.fab.FloatingActionButton) findViewById(R.id.transit_button);


        if(isNetworkAvailable(this)) {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(activity.getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();


            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            CategoriesAsyncTask categoriesAsyncTask = new CategoriesAsyncTask(); //get categories
            categoriesAsyncTask.response = this;
            categoriesAsyncTask.execute();
        }else noInternetMessage();

        // get categories statically
        // categoryList = getResources().getStringArray(R.array.categories_name);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        mFragmentManager = getSupportFragmentManager();
        mapFragment = (SupportMapFragment) mFragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        View mapView = mapFragment.getView();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,0);
        p.weight = 100;
        mapView.setLayoutParams(p);
        mapView.requestLayout();

        FloatingActionButton searchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toolbar search =(Toolbar) findViewById(R.id.search);
                if(search.isShown())
                    search.setVisibility(View.GONE);
                else
                    search.setVisibility(View.VISIBLE);
            }
        });

        // creation of the place autocomplete on left of the toolbar and its listener
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                reset();

                selectedPlace = place;

                selectedPlaceMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), zoomLevel));
                mDestination = place.getLatLng();
                if(isNetworkAvailable(getApplicationContext())){
                    getDirections();
                }else
                    noInternetMessage();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        resizeMap(100);
                        detailsButton.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(Status status) {
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.main),getResources().getString(R.string.error_occured) + status, Snackbar.LENGTH_LONG);

                snackbar.show();
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SubMenu submenu = menu.addSubMenu(0, Menu.FIRST, Menu.NONE, "Geolocalization");
        submenu.add(0, 1, Menu.NONE, "Set position manually");
        submenu.add(0, 2, Menu.NONE, "Use device geolocalization");
        getMenuInflater().inflate(R.menu.main_menu, submenu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        reset();
        SharedPreferences.Editor editor = sharedPref.edit();

        // Handle item selection
        switch (item.getItemId()) {
            case 1:

                editor.putBoolean("geolocalization", false);
                editor.commit();

                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.main),"Select new Origin position", Snackbar.LENGTH_LONG);

                snackbar.show();
                if(originMarker != null && originMarker.isVisible())
                    originMarker.remove();

                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
                positionPermissionDenied();
                return true;
            case 2:

                editor.putBoolean("geolocalization", true);
                editor.commit();

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //position permission has not been granted
                    positionPermissionDenied();
                    requestPositionPermission();
                }
                else {
                    //position permission has been granted
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    setDestination();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *Method used by drawer to set a function for each chosed category, in this case starts the Async Task to get POI/places
     *@param categoryName the position of the chosen category in drawer
     */
    private void selectItem(String categoryName) {

        categoryName = categoryName.replace(" ","_");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                resizeMap(100);
                detailsButton.setVisibility(View.GONE);
            }
        });

        //fragment = null;
        CharSequence title = null;
        mDestination = null;
        mMap.clear();

        if(isNetworkAvailable(getApplicationContext())){
            if(!pois.isEmpty()){
                pois.clear();
            }

            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(activity.getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            PoisByCategoryAsyncTask poisByCategoryAsyncTask= new PoisByCategoryAsyncTask(this, mMap, categoryName); //get pois by category
            poisByCategoryAsyncTask.response = this;
            poisByCategoryAsyncTask.execute();

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            PlacesAsyncTask placesAsyncTask = new PlacesAsyncTask(mOrigin,mMap,this,categoryName);
            placesAsyncTask.response = this;
            placesAsyncTask.execute();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,14));

        }else
            noInternetMessage();
    }
    /**
     *risults of getCategories AsyncTask and set the category list on drawer
     *@param output contains all categories to set in Drawer view
     */

    @Override
    public void taskResult(String[] output) {
        if(output != null) {

            for(int i = 0; i<output.length; i++){
                output[i] = output[i].replace("_"," ");
            }
            categories = output;
            menu = navigationView.getMenu();
            SubMenu subMenu = menu.addSubMenu(R.string.category);
            subMenu.setIcon(android.R.drawable.ic_search_category_default);
            for (int i = 0; i < output.length; i++) {
                subMenu.add(output[i].toString()).setIcon(android.R.drawable.ic_menu_info_details);
            }

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    selectItem(item.toString());
                    Toast.makeText(getApplicationContext(),""+item.toString(),Toast.LENGTH_SHORT).show();
                    DrawerLayout  mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    mDrawerLayout.closeDrawers();

                    return false;
                }
            });
        }else{

            Toast.makeText(getApplicationContext(),R.string.no_categories_loaded,Toast.LENGTH_SHORT).show();
            categories = getResources().getStringArray(R.array.categories_name);
        }

        pDialog.dismiss();
    }

    /**
     *method containing the result of directionAsyncTask
     *@param routes contains the route calculated by Google
     */
    @Override
    public void TaskResultRoutes(List<Route> routes) {

        animatefloatingActionMenu();

        floatingActionMenu.showMenu(true);
        floatingActionMenu.showMenuButton(true);
        floatingActionMenu.getMenuIconView().setImageResource(android.R.drawable.ic_dialog_map);


        // added to disable buttons if there are not one of routes found
        if(transitRoutes == null) {
            transitButton.setEnabled(false);
            transitButton.setLabelVisibility(View.INVISIBLE);
        }
        if(walkingRoutes == null) {
            walkingButton.setEnabled(false);
            walkingButton.setLabelVisibility(View.INVISIBLE);
        }

        if(routes == null || routes.isEmpty()){
            Snackbar.make(activity.findViewById(R.id.main),getText(R.string.route_not_found),Snackbar.LENGTH_SHORT).show();
        }else if(routes.get(0).getMode().matches("transit")) {
            transitRoutes = routes;
            transitButton.show(false);
            transitButton.setVisibility(View.VISIBLE);
            transitButton.setLabelText(routes.get(0).getDuration());
            transitButton.setEnabled(true);
            transitButton.setLabelVisibility(View.VISIBLE);
        }
        else if(routes.get(0).getMode().matches("walking")) {
            walkingRoutes = routes;
            walkingButton.show(false);
            walkingButton.setVisibility(View.VISIBLE);
            walkingButton.setLabelText(routes.get(0).getDuration());
            walkingButton.setEnabled(true);
            walkingButton.setLabelVisibility(View.VISIBLE);
        }

        setOnInfoWindowListener();
    }

    /**
     *method containing the resutlt of poisByCategoryAsyncTask
     *@param pois contains the POIs of the chosen category
     */
    @Override
    public void taskResult(List<Poi> pois) {

        reset();

        if(pois.isEmpty()){
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main),activity.getString(R.string.no_personal_poi_found), Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        this.pois = pois;

        for(Poi poi :pois){
            poi.draw();
        }

        setOnInfoWindowListener();
    }

    /**
     *method containing the resutlt of placesAsyncTask
     *@param places contains the Places of the chosen category
     */
    @Override
    public void TaskResult(List<it.curdrome.timetogo.model.Place> places) {

        if(places.isEmpty()){
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main),activity.getString(R.string.no_Google_POI_found), Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        this.places = places;

        for(it.curdrome.timetogo.model.Place place : places){
            place.draw();
        }

        mCircle = mMap.addCircle(new CircleOptions().radius(1500).center(mOrigin).strokeColor(R.color.colorPrimaryLight).fillColor(0x05689F38).strokeWidth(2));

        setOnInfoWindowListener();
        pDialog.dismiss();
    }

    /**
     *Method that connects with google maps api client
     *and after in view map creates a map fragment
     */

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient == null){
            buildGoogleApiClient();
        }

        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }

        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }


    }

    /**
     *Method used to set what map fragment contains
     */
    public void setUpMap(){

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng,1));

    }

    /**
     *Method that sets up the map when loaded
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Calendar calendar = Calendar.getInstance();

        if(calendar.get(Calendar.HOUR_OF_DAY)>19 && calendar.get(Calendar.HOUR_OF_DAY)<6)
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.night_style_json));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        else
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.day_style_json));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }


        mMap = googleMap;
        setUpMap();

    }

    /**
     * Method that loads the current device position and asks for location permissions
     *creates also the FloatingActionButtons to switch between routes transit/walking and its listner
     *@param bundle used to save state variables
     */
    @Override
    public void onConnected(Bundle bundle) {

        transitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOrigin != null && mDestination != null && transitRoutes != null && !transitRoutes.isEmpty()){
                    if(!transitRoutes.get(transitRouteNr).isDraw)
                        transitRoutes.get(transitRouteNr).draw();
                    if (walkingRoutes != null) {
                        if (walkingRoutes.get(walkingRouteNr).isDraw)
                            walkingRoutes.get(walkingRouteNr).erase();
                    }
                    mMap.setOnMarkerClickListener(null);
                    currentRoute = transitRoutes.get(transitRouteNr);

                    Log.d("MainActivity", currentRoute.toString());

                    FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                    RouteMiniFragment fragment = new RouteMiniFragment();
                    if(fTransaction.isEmpty()){
                        frameLayout.removeAllViews();
                        fTransaction.add(R.id.frame_main, fragment);
                        resizeMap(80);
                        detailsButton.setVisibility(View.VISIBLE);
                        closeButton.setVisibility(View.GONE);
                    }

                    else {
                        frameLayout.removeAllViews();
                        fTransaction.replace(R.id.frame_main, fragment);
                    }
                    fTransaction.commit();

                } else {
                    transitButton.setClickable(false);
                }
            }

        });

        walkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOrigin != null && mDestination != null){
                    if(!walkingRoutes.get(walkingRouteNr).isDraw)
                        walkingRoutes.get(walkingRouteNr).draw();
                    if(transitRoutes!= null  && !transitRoutes.isEmpty() && transitRoutes.get(transitRouteNr).isDraw)
                        transitRoutes.get(transitRouteNr).erase();
                    mMap.setOnMarkerClickListener(null);
                    currentRoute = walkingRoutes.get(walkingRouteNr);

                    FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                    RouteMiniFragment fragment = new RouteMiniFragment();
                    if(fTransaction.isEmpty()){
                        frameLayout.removeAllViews();
                        fTransaction.add(R.id.frame_main, fragment);
                        resizeMap(80);
                        detailsButton.setVisibility(View.GONE);
                        closeButton.setVisibility(View.VISIBLE);
                    }

                    else {
                        frameLayout.removeAllViews();
                        fTransaction.replace(R.id.frame_main, fragment);
                    }
                    fTransaction.commit();

                } else {
                    Toast.makeText(getApplicationContext(),activity.getString(R.string.origin_or_destination_not_set),Toast.LENGTH_SHORT).show();
                }

            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //position permission has not been granted
            positionPermissionDenied();
            requestPositionPermission();
        }
        else {

            if(sharedPref.getBoolean("geolocalization",true)){
                //position permission has been granted
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                setDestination();
            }
            else positionPermissionDenied();

        }
    }

    /**
     *method that set the destination containing its listner
     */
    private void setDestination() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                mDestination = latLng;
                destinationMarker = mMap.addMarker(new MarkerOptions().position(mDestination)
                        .title(activity.getString(R.string.my_destination)).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestination, zoomLevel));
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        resizeMap(100);
                        detailsButton.setVisibility(View.GONE);
                    }
                });

                if(isNetworkAvailable(getApplicationContext())){
                    getDirections();
                }else
                    noInternetMessage();

            }
        });
    }

    /**
     *Listner for location permission denied
     */
    private void positionPermissionDenied(){

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                if (originMarker != null) {
                    originMarker.remove();
                }
                mOrigin = latLng;
                originMarker = mMap.addMarker(new MarkerOptions().position(mOrigin)
                        .title(activity.getString(R.string.my_location)).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                originButton = (it.curdrome.timetogo.fab.FloatingActionMenu)findViewById(R.id.origin_button); //setup layout of fam & fab
                originButton.setVisibility(View.VISIBLE);
                originButton.open(true);
                originButton.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                    @Override
                    public void onMenuToggle(boolean opened) {
                        if (!opened) {
                            //remove previous current location Marker
                            if (originMarker != null) {
                                originMarker.remove();
                            }
                            originMarker = mMap.addMarker(new MarkerOptions().position(mOrigin)
                                    .title(activity.getString(R.string.my_location)).icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, zoomLevel));
                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                    resizeMap(100);
                                    detailsButton.setVisibility(View.GONE);
                                }
                            });
                            originButton.close(true);
                            originButton.setVisibility(View.INVISIBLE);
                            setDestination();
                            Log.d("origin Listner", latLng.toString());
                        }else{
                            originButton.open(true);
                        }
                    }
                });
            }
        });
    }

    /**
     * Requests the Position permission.
     * If the permission has been denied previously, a Alert Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPositionPermission() {
        Log.i(TAG, "Position permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(position_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying position permission rationale to provide additional context.");
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(activity.getString(R.string.permission_necessary));
            alertBuilder.setMessage(activity.getString(R.string.location_permission_is_necessary));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_POSITION);}});

            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {

            // Position permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_REQUEST_POSITION);

        }
        // END_INCLUDE(position_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_POSITION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(getIntent());
            } else {
                // denied permission...
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     *Method used to build a connection to the google api client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     *method used to remove location updates in case app gose on pause mode
     */
    @Override
    protected void onPause(){
        super.onPause();

        if (mGoogleApiClient != null) {
            if(mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            else{
                mGoogleApiClient.connect();
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                }catch (IllegalStateException ise)
                {
                    ise.toString();
                }
            }
        }
    }

    /**
     *method used to update location when location changed
     * @param location the new location of the device
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mOrigin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (originMarker != null) {
            originMarker.remove();
        }
        originMarker = mMap.addMarker(new MarkerOptions().position(mOrigin)
                .title(activity.getString(R.string.my_location)).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        if(defaultLatLng.longitude==0 && defaultLatLng.latitude == 0) {

            defaultLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng,11));

        }
    }

    public void noInternetMessage(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(activity.getString(R.string.internet_necessary));
        alertBuilder.setMessage(activity.getString(R.string.message_internet_is_necessary));
        alertBuilder.setPositiveButton(activity.getString(R.string.retry), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(!isNetworkAvailable(activity)){
                    noInternetMessage();
                }
            }
        });

        alertBuilder.setNegativeButton(activity.getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        //create and show alert dialog
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /**
     * method used to resyze the map when needed
     * @param weight the new weight of the map
     */
    public void resizeMap(int weight){

        View mapView = mapFragment.getView();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,0);
        p.weight = weight;
        mapView.setLayoutParams(p);
        mapView.requestLayout();
    }


    /**
     * AsyncTasc caller to get transit and walking routes
     */
    private void getDirections(){
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        DirectionAsyncTask directionAsyncTaskTransit = new DirectionAsyncTask(mOrigin, mDestination, mMap, activity,"transit");//calcola il percorso
        directionAsyncTaskTransit.execute();
        directionAsyncTaskTransit.response = activity;
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        DirectionAsyncTask directionAsyncTaskWalking = new DirectionAsyncTask(mOrigin, mDestination, mMap, activity,"walking");//calcola il percorso
        directionAsyncTaskWalking.execute();
        directionAsyncTaskWalking.response = activity;
    }

    /**
     * Method used to set the listner on the info window of the markers
     * places or pois or transit
     * when needed creates fragments containing extra info
     */
    public void setOnInfoWindowListener(){

        closeButton= (FloatingActionButton) findViewById(R.id.close_fab);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resizeMap(100);
                closeButton.setVisibility(View.GONE);
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                for(Poi poi : getPois()){
                    if(poi.getMarker().equals(marker)){
                        Log.d("marker clicked", poi.toString());
                        selectedPoi = poi;
                        FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                        PoiFragment fragment = new PoiFragment();
                        if(fTransaction.isEmpty()){
                            frameLayout.removeAllViews();
                            fTransaction.add(R.id.frame_main, fragment);
                            resizeMap(70);
                            detailsButton.setVisibility(View.GONE);
                            closeButton.setVisibility(View.VISIBLE);
                        }

                        else {
                            frameLayout.removeAllViews();
                            fTransaction.replace(R.id.frame_main, fragment);
                        }
                        fTransaction.commit();
                    }
                }

                for(it.curdrome.timetogo.model.Place place : getPlaces()){
                    if(place.getMarker().equals(marker)){
                        Log.d("marker clicked", place.toString());
                        selectedMyPlace = place;
                        FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                        PlaceFragment fragment = new PlaceFragment();
                        if(fTransaction.isEmpty()){
                            frameLayout.removeAllViews();
                            fTransaction.add(R.id.frame_main, fragment);
                            resizeMap(70);
                            detailsButton.setVisibility(View.GONE);
                            closeButton.setVisibility(View.VISIBLE);
                        }

                        else {
                            frameLayout.removeAllViews();
                            fTransaction.replace(R.id.frame_main, fragment);
                        }
                        fTransaction.commit();
                    }
                }


                if(transitRoutes != null && !transitRoutes.isEmpty() && transitRoutes.get(transitRouteNr).isDraw)
                    for(final Transit transit: transitRoutes.get(transitRouteNr).getListTransit()){
                        if(transit.getMarker().equals(marker)){

                            selectedTransit = transit;

                            FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
                            TransitFragment fragment = new TransitFragment();
                            if(fTransaction.isEmpty()){
                                frameLayout.removeAllViews();
                                fTransaction.add(R.id.frame_main, fragment);
                                resizeMap(80);
                                detailsButton.setVisibility(View.GONE);
                                closeButton.setVisibility(View.VISIBLE);

                            }

                            else {
                                frameLayout.removeAllViews();
                                fTransaction.replace(R.id.frame_main, fragment);
                            }
                            fTransaction.commit();

                        }
                    }
            }
        });
    }

    /**
     * Default method overrided to decide when exit from app or just reset
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Snackbar snackbar = Snackbar
                .make(activity.findViewById(R.id.main),activity.getString(R.string.please_click_back_to_exit), Snackbar.LENGTH_LONG);

        snackbar.show();

        if(detailsButtonClicked){
            resizeMap(80);
            detailsButton.setImageResource(android.R.drawable.arrow_up_float);
            detailsButtonClicked = false;
            FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
            RouteMiniFragment fragment = new RouteMiniFragment();
            if(fTransaction.isEmpty()){
                frameLayout.removeAllViews();
                fTransaction.add(R.id.frame_main, fragment);
            }

            else {
                frameLayout.removeAllViews();
                fTransaction.replace(R.id.frame_main, fragment);
            }
            fTransaction.commit();

        }else {
            reset();
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    /**
     * method used to reset all values of the app
     */
    private void reset(){
        if(mDestination != null){
            if(walkingRoutes != null)
                for(Route walkingRoute: walkingRoutes)
                    if(walkingRoute != null && walkingRoute.isDraw){
                        walkingRoute.erase();
                    }
            walkingRoutes = null;
            if(transitRoutes != null)
                for(Route transitRoute: transitRoutes)
                    if(transitRoute != null && transitRoute.isDraw){
                        transitRoute.erase();
                    }
            transitRoutes = null;

            if(!floatingActionMenu.isMenuHidden()) {
                floatingActionMenu.toggle(true);
            }
            floatingActionMenu.hideMenu(true);
            floatingActionMenu.hideMenuButton(true);


            mDestination = null;
            resizeMap(100);
            detailsButton.setVisibility(View.GONE);
            closeButton.setVisibility(View.GONE);
            setDestination();

            if(destinationMarker != null)
                destinationMarker.remove();
        }

        if(!pois.isEmpty() && pois != null) {
            for (Poi poi : pois) {
                if(poi.getMarker().isVisible())
                    poi.getMarker().remove();
            }
            pois.clear();
        }

        if(!places.isEmpty() && places != null) {
            for (it.curdrome.timetogo.model.Place place : places) {
                if(place.getMarker().isVisible())
                    place.getMarker().remove();
            }
            places.clear();
        }
        if(mOrigin!=null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin,11));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0,0),1));

        if(selectedPlaceMarker!=null && selectedPlaceMarker.isVisible())
            selectedPlaceMarker.remove();

        handler.removeCallbacksAndMessages(null);

        if(mCircle!=null && mCircle.isVisible()) {
            mCircle.setVisible(false);
            mCircle.remove();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made

                        this.recreate();

                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            //position permission has not been granted
                            positionPermissionDenied();
                            requestPositionPermission();
                        }
                        else {
                            //position permission has been granted
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.setMyLocationEnabled(true);
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                            setDestination();
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        positionPermissionDenied();
                        Snackbar snackbar = Snackbar
                                .make(activity.findViewById(R.id.main),getResources().getString(R.string.chose_origin), Snackbar.LENGTH_LONG);

                        snackbar.show();

                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public Poi getSelectedPoi() {
        return selectedPoi;
    }

    /*
    other setters and getters
     */

    public Transit getSelectedTransit() {
        return selectedTransit;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public void setmDestination(LatLng mDestination) {
        this.mDestination = mDestination;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public List<it.curdrome.timetogo.model.Place> getPlaces() {
        return places;
    }

    public it.curdrome.timetogo.model.Place getSelectedMyPlace() {
        return selectedMyPlace;
    }

    public int getTransitRouteNr() {
        return transitRouteNr;
    }

    public void setTransitRouteNr(int transitRouteNr) {
        this.transitRouteNr = transitRouteNr;
    }

    public int getWalkingRouteNr() {
        return walkingRouteNr;
    }

    public void setWalkingRouteNr(int walkingRouteNr) {
        this.walkingRouteNr = walkingRouteNr;
    }

    public List<Route> getTransitRoutes() {
        return transitRoutes;
    }

    public void setTransitRoutes(List<Route> transitRoutes) {
        this.transitRoutes = transitRoutes;
    }

    public void setCurrentRoute(Route currentRoute) {
        this.currentRoute = currentRoute;
    }

    public FragmentManager getmFragmentManager() {
        return mFragmentManager;
    }

    public void setmFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    public void setTransitButtonLabel(String label){
        transitButton.setLabelText(label);
    }

    public FrameLayout getFrameLayout() {
        return frameLayout;
    }

    public void setFrameLayout(FrameLayout frameLayout) {
        this.frameLayout = frameLayout;
    }
}