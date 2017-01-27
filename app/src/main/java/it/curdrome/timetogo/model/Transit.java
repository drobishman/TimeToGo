package it.curdrome.timetogo.model;



import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import it.curdrome.timetogo.connection.atac.IdPalinaAsyncTask;
import it.curdrome.timetogo.connection.atac.RTIAsyncTask;

/**
 * Created by adrian on 21/01/2017.
 */

public class Transit {


    private Transit transit = this;

    private int numStops;
    private String departureStop;
    private String headsign;
    private String type;
    private String line;
    private String idPalina;
    private String departureTime;
    private Marker marker;

    public LatLng getPalinaLatLng() {
        return palinaLatLng;
    }

    private LatLng palinaLatLng;

    public Transit(int numStops, String departureStop, String headsign, String type, String line, String departureTime, double lat, double lng){

        this.numStops = numStops;
        this.departureStop = departureStop;
        this.headsign = headsign;
        this.type = type;
        this.line = line;
        this.departureTime = departureTime;
        this.palinaLatLng = new LatLng(lat,lng);

        if(this.type.matches("BUS")) {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            new IdPalinaAsyncTask(this).execute();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            new RTIAsyncTask(transit).execute();
        }

    }

    public int getNumStops() {
        return numStops;
    }

    public void setNumStops(int numStops) {
        this.numStops = numStops;
    }

    public String getDepartureStop() {
        return departureStop;
    }

    public void setDepartureStop(String departureStop) {
        this.departureStop = departureStop;
    }

    public String getHeadsign() {
        return headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getIdPalina() {
        return idPalina;
    }

    public void setIdPalina(String idPalina) {
        this.idPalina = idPalina;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public String toString(){
        return "\n num_stops :"+numStops+", departure_stop :"+departureStop+", headsign :"+headsign+", type: "+type+", line: "+line+", id_palina: "+idPalina +", departure_time: "+departureTime+"";
    }
}
