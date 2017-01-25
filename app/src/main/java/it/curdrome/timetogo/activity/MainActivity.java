package it.curdrome.timetogo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import it.curdrome.timetogo.R;

import it.curdrome.timetogo.connection.server.CategoriesResponse;
import it.curdrome.timetogo.connection.google.*;
import it.curdrome.timetogo.connection.server.CategoriesAsyncTask;
import it.curdrome.timetogo.connection.server.PoisByCategoryAsyncTask;
import it.curdrome.timetogo.connection.server.PoisByCategoryResponse;
import it.curdrome.timetogo.fragment.PoiFragment;
import it.curdrome.timetogo.model.Poi;
import it.curdrome.timetogo.model.Route;

/**
 This class is the main class of the "TimeToGo" application.

 This main class creates and initialises the map fragment, handles permission requests and for now creates also the directions,

 @author Drob Adrian Mihai
 @version 13/01/2017
 */

public class MainActivity extends FragmentActivity  implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        CategoriesResponse,
        DirectionResponse ,
        PoisByCategoryResponse{

    private SupportMapFragment mapFragment;
    private FragmentManager mFragmentManager;

    private List<Route> routes = new ArrayList<>();

    public List<Poi> getPois() {
        return pois;
    }

    private List<Poi> pois = new ArrayList<>();

    public static final String TAG = "MainActivity";
    private static final int MY_REQUEST_POSITION = 0;

    // drawer variables
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    // map variables
    private GoogleMap mMap;
    private Marker originMarker;
    private Marker destinationMarker;
    private LocationRequest mLocationRequest; //
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng romeLatLng = new LatLng (41.902783, 12.496366); //rome position
    private float zoomLevel = 11; // default zoom level

    //origin and destination to generate direction
    private LatLng mOrigin;

    public void setmDestination(LatLng mDestination) {
        this.mDestination = mDestination;
    }

    private LatLng mDestination;

    private MainActivity activity = (MainActivity) this;

    private Button originButton;
    private Button directionButton;

    public ProgressDialog pDialog; // to show when direction create
    private boolean wait = true;

    /*
    Method that creates the main activity and the drawer with its listeners
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable(this)) {
            noInternetMessage();
        }

        if(isNetworkAvailable(this)) {
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(activity.getString(R.string.loading_categories_wait));
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
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mFragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) mFragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /*
   Method used by drawer to set a function for each chosed category
    */
    private void selectItem(int position) {

        mMap.setOnMapClickListener(null);

        //fragment = null;
        CharSequence title = null;
        mDestination = null;
        mMap.clear();

        if(isNetworkAvailable(getApplicationContext())){
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            PoisByCategoryAsyncTask poisByCategoryAsyncTask= new PoisByCategoryAsyncTask(this, mMap, position+1); //get pois by category
            poisByCategoryAsyncTask.response = this;
            poisByCategoryAsyncTask.execute();
        }else
            noInternetMessage();


        mDrawerList.setItemChecked(position, true);
        try {
            getActionBar().setTitle(title);
        } catch (NullPointerException npex) {
            Log.d("ERROR", "non è stato possibile egeguire il setTitle() sull'actionBar");
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    // risults of getCategories AsyncTask and set the category list on drawer
    @Override
    public void taskResult(String[] output) {
        if(output != null) {
            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.drawer_list_item, output));
        }else{

            Toast.makeText(getApplicationContext(),R.string.no_categories_loaded,Toast.LENGTH_SHORT).show();
            String[] mStringArray = getResources().getStringArray(R.array.categories_name);

            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.drawer_list_item, mStringArray));
        }
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        pDialog.dismiss();
    }

    @Override
    public void TaskResult(Route route) {
        routes.add(route);
        Log.d("mainActivity routes", routes.size()+"");
        routes.get(0).draw();
    }

    @Override
    public void taskResult(List<Poi> pois) {
        this.pois = pois;
        Log.d("pois",pois.toString());

        FragmentTransaction fTransaction = mFragmentManager.beginTransaction();
        PoiFragment fragment = new PoiFragment();
        fTransaction.add(R.id.frame_main, fragment);
        fTransaction.addToBackStack(null);
        fTransaction.commit();

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    /*
    Method that connects with google maps api client
    and after in view map creates a map fragment
     */

    @Override
    protected void onResume() {
        super.onResume();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()){

            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }

        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /*
    Method used to set what map fragment contains
     */
    public void setUpMap(){

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(romeLatLng,11));

    }


    /*
    Method that sets up the map when loaded
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setUpMap();

    }

    /*
    Method that loads the current device position and asks for location permissions
     */
    @Override
    public void onConnected(Bundle bundle) {

        directionButton = (Button) findViewById(R.id.direction_button);
        directionButton.setVisibility(View.VISIBLE);
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOrigin != null && mDestination != null){
                    if(isNetworkAvailable(getApplicationContext())){
                        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                        DirectionAsyncTask directionAsyncTask = new DirectionAsyncTask(mOrigin, mDestination, mMap, activity);//calcola il percorso
                        directionAsyncTask.execute();
                        directionAsyncTask.response = activity;
                    }else
                        noInternetMessage();

                } else {
                    Toast.makeText(getApplicationContext(),"Origin or destination not set",Toast.LENGTH_SHORT).show();
                }

            }
        });

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

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
    }

    /*
    method that set the destination
     */
    private void setDestination() {

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                mDestination = latLng;
                destinationMarker = mMap.addMarker(new MarkerOptions().position(mDestination)
                        .title(getString(R.string.my_destination)).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestination, zoomLevel));
                mMap.setOnMapClickListener(null);


            }
        });
    }

    /*
    Listner for location permission denied
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
                        .title(getString(R.string.my_location)).icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                originButton = (Button) findViewById(R.id.origin_button);
                originButton.setVisibility(View.VISIBLE);
                originButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //remove previous current location Marker
                        if (originMarker != null){
                            originMarker.remove();
                        }

                        originMarker = mMap.addMarker(new MarkerOptions().position(mOrigin)
                                .title(getString(R.string.my_location)).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, zoomLevel));
                        mMap.setOnMapClickListener(null);
                        originButton.setVisibility(Button.INVISIBLE);
                        setDestination();
                        Log.d("origin Listner", latLng.toString());
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
            alertBuilder.setTitle(getString(R.string.permission_necessary));
            alertBuilder.setMessage(getString(R.string.location_permission_is_necessary));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_REQUEST_POSITION);}});

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
    /*
    Method used to build a connection to the google api client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*
    method used to remove location updates in case app gose on pause mode
     */
    @Override
    protected void onPause(){
        super.onPause();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /*
    method used to update location when location changed
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;mOrigin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if (originMarker != null) {
            originMarker.remove();
        }
        originMarker = mMap.addMarker(new MarkerOptions().position(mOrigin)
                .title(getString(R.string.my_location)).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


    }

    /**
     * This method check mobile is connected to network.
     * @param context
     * @return true if connected otherwise false.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    private void noInternetMessage(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getString(R.string.internet_necessary));
        alertBuilder.setMessage(getString(R.string.message_internet_is_necessary));
        alertBuilder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // restart application
                finish();
                startActivity(getIntent());
            }
        });

        alertBuilder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        //create and show alert dialog
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}