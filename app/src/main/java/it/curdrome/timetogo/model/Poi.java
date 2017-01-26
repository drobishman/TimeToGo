package it.curdrome.timetogo.model;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.connection.google.DirectionAsyncTask;

/**
 * Created by adrian on 16/01/2017.
 */

public class Poi {

    private MainActivity activity;

    private GoogleMap mMap;
    private int id;
    private String placeId;
    private Category category;
    private String name;
    private double lat;
    private double lng;
    private String description;
    private Marker marker;

    public Poi (
            int id,
            String placeId,
            Category category,
            String name,
            double lat,
            double lng,
            String description,
            GoogleMap mMap,
            MainActivity activity){

        this.id = id;
        this.placeId = placeId;
        this.category = category;
        this.name = name;
        this.lat = lat;
        this.lng =lng;
        this.description = description;
        this.mMap = mMap;
        this.activity = activity;

    }

    public void draw(){
       marker = mMap.addMarker(new MarkerOptions().position(new LatLng(this.getLat(),this.getLng()))
                .title(this.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
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

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString(){
        return "id: "+id+", placeId: "+placeId+", name: "+name+", lat: "+lat+", lng: "+ lng+", description: "+description+"";
    }
}
