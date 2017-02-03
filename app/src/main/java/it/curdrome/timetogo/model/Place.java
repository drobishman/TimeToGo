package it.curdrome.timetogo.model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.connection.google.DirectionAsyncTask;

/**
 * Created by adrian on 03/02/2017.
 */

public class Place {

    private MainActivity activity;
    private GoogleMap mMap;
    private Marker marker;

    private LatLng geometry;
    private String name;
    private boolean openHoursEnabled;
    private boolean openNow;
    private String placeId;
    private List<Category> categories;
    private String vicinity;

    public Place (GoogleMap mMap,
                  MainActivity activity,
                  LatLng geometry,
                  String name,
                  boolean openHoursEnabled,
                  boolean openNow,
                  String placeId,
                  List<Category> categories,
                  String vicinity){

        this.activity = activity;
        this.mMap = mMap;
        this.geometry = geometry;
        this.name = name;
        this.openHoursEnabled = openHoursEnabled;
        this.openNow = openNow;
        this.placeId = placeId;
        this.categories = categories;
        this.vicinity = vicinity;

    }

    public void draw(){
        marker = mMap.addMarker(new MarkerOptions().position(geometry)
                .title(this.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                activity.setmDestination(marker.getPosition());
                marker.showInfoWindow();

                if(activity.isNetworkAvailable(activity.getApplicationContext())){
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    DirectionAsyncTask directionAsyncTaskTransit = new DirectionAsyncTask(activity.mOrigin, activity.mDestination, mMap, activity,"transit");//calcola il percorso
                    directionAsyncTaskTransit.execute();
                    directionAsyncTaskTransit.response = activity;
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    DirectionAsyncTask directionAsyncTaskWalking = new DirectionAsyncTask(activity.mOrigin, activity.mDestination, mMap, activity,"walking");//calcola il percorso
                    directionAsyncTaskWalking.execute();
                    directionAsyncTaskWalking.response = activity;
                }else
                    activity.noInternetMessage();

                return true;
            }
        });
    }

    public LatLng getGeometry() {
        return geometry;
    }

    public void setGeometry(LatLng geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpenHoursEnabled() {
        return openHoursEnabled;
    }

    public void setOpenHoursEnabled(boolean openHoursEnabled) {
        this.openHoursEnabled = openHoursEnabled;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }



    @Override
    public String toString(){
        return "\n geometry :"+geometry+", name :"+name+", open hours enabled :"+openHoursEnabled+", open now :"+openNow+", place id : "+placeId+", categories: "+categories.toString()+", vicinity: "+vicinity +"";

    }
}
