package it.curdrome.timetogo.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by adrian on 16/01/2017.
 */

public class Poi {

    private int id;
    private String placeId;
    private Category category;
    private String name;
    private double lat;
    private double lng;
    private String description;

    public Poi (int id, String placeId, Category category, String name, double lat, double lng, String description ){

        this.id = id;
        this.placeId = placeId;
        this.category = category;
        this.name = name;
        this.lat = lat;
        this.lng =lng;
        this.description = description;
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
