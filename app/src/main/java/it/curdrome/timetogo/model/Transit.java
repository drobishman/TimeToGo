package it.curdrome.timetogo.model;

import it.curdrome.timetogo.connection.atac.GetIdPalina;
import it.curdrome.timetogo.connection.atac.GetRTI;

/**
 * Created by adrian on 21/01/2017.
 */

public class Transit {
    private int numStops;
    private String departureStop;
    private String headsign;
    private String type;
    private String line;
    private String idPalina;
    private String departureTime;

    public Transit(int numStops, String departureStop, String headsign, String type, String line, String departureTime){

        this.numStops = numStops;
        this.departureStop = departureStop;
        this.headsign = headsign;
        this.type = type;
        this.line = line;
        this.departureTime = departureTime;

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        new GetIdPalina(this).execute();

        RTI();
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

    @Override
    public String toString(){
        return " num_stops :"+numStops+", departure_stop :"+departureStop+", headsign :"+headsign+", type: "+type+", line: "+line+", id_palina: "+idPalina +", departure_time: "+departureTime+"\n";
    }

    public void RTI (){

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        new GetRTI(this).execute();
    }
}
