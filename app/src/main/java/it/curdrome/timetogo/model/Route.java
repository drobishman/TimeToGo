package it.curdrome.timetogo.model;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 21/01/2017.
 * Model Class for the Route
 *
 * @author adrian
 * @version 1
 */

public class Route {

    private List<LatLng> poly;
    private GoogleMap mMap;

    private String points;
    private String arrivalTime;
    private String departureTime;
    private String distance;
    private String duration;
    private List<Transit> listTransit;

    private LatLng northeast;
    private LatLng southwest;

    private String mode;

    public boolean draw = false;

    private List<Polyline> polylines = new ArrayList<>();

    /**
     * Default constructor method
     * @param mMap map to be draw
     * @param points codified polyline points
     * @param arrivalTime the arrival time
     * @param departureTime the departure time
     * @param distance the distance of the route
     * @param duration the duration of the trip
     * @param listTransit the list of stops on our route
     * @param southwest bounds
     * @param northeast bounds
     * @param mode the mode walking/transit
     */
    public Route (GoogleMap mMap,
                  String points,
                  String arrivalTime,
                  String departureTime,
                  String distance,
                  String duration,
                  List<Transit> listTransit,
                  LatLng southwest,
                  LatLng northeast,
                  String mode){

        this.mMap = mMap;
        this.points = points;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.distance = distance;
        this.duration = duration;
        this.listTransit = listTransit;
        this.southwest = southwest;
        this.northeast = northeast;
        this.mode = mode;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    /**
     *  Method to decode polyline points
     *  @param encoded the encoded points
     *  @return a list Of latLng containg each poly to draw
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    /**
     * Method used to draw all marker for transits and polylines on the map
     */
    public void draw(){

        poly = decodePoly(points);

        for (int i = 0; i < poly.size() - 1; i++) {
            LatLng src = poly.get(i);
            LatLng dest = poly.get(i + 1);
            polylines.add(mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude))
                    .width(10).color(Color.RED)));

        }
        for(Transit transit: listTransit){
            transit.setMarker(mMap.addMarker(new MarkerOptions().position(transit.getPalinaLatLng())
                    .title(transit.getDepartureStop()).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast),100));
        draw = true;
    }

    /**
     * Method used to remove all markers and polylines from the map created before
     */
    public void erase (){

        for(Polyline polyline: polylines){
            polyline.remove();
        }

        if(!listTransit.isEmpty() && this.draw)
            for(Transit transit: listTransit){
                transit.getMarker().remove();
            }
        polylines.clear();
    }

    @Override
    public String toString(){

        return "points : "+points+ "\n"+
                " arrivalTime : "+arrivalTime+
                " departureTime : " + departureTime +
                " distance : " + distance +
                " duration : " + duration +
                " list of transits : \n" + listTransit.toString();
    }

    public String getDuration() {
        return duration;
    }

    public String getMode() {
        return mode;
    }

    public List<Transit> getListTransit() {
        return listTransit;
    }

    public void setListTransit(List<Transit> listTransit) {
        this.listTransit = listTransit;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


}
