package it.curdrome.timetogo.model;

import java.util.List;

/**
 * Created by adrian on 21/01/2017.
 */

public class Route {

    private String points;
    private String arrivalTime;
    private String departureTime;
    private String distance;
    private String duration;
    private List<Transit> busStops;

    public Route (String points, String arrivalTime, String departureTime, String distance, String duration, List<Transit> busStops){

        this.points = points;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.distance = distance;
        this.duration = duration;
        this.busStops = busStops;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Transit> getBusStops() {
        return busStops;
    }

    public void setBusStops(List<Transit> busStops) {
        this.busStops = busStops;
    }

    @Override
    public String toString(){

        return "points : "+points+ "\n"+
                " arrivalTime : "+arrivalTime+
                " departureTime : " + departureTime +
                " distance : " + distance +
                " duration : " + duration +
                " list of transits : \n" + busStops.toString();
    }
}
