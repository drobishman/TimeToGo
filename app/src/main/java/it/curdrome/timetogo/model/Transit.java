package it.curdrome.timetogo.model;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import it.curdrome.timetogo.connection.atac.IdPalinaAsyncTask;
import it.curdrome.timetogo.connection.atac.IdPalinaResponse;
import it.curdrome.timetogo.connection.atac.RTIAsyncTask;
import it.curdrome.timetogo.connection.viaggiatreno.AndamentoTrenoAsyncTask;
import it.curdrome.timetogo.connection.viaggiatreno.CercaNumeroTrenoAsyncTask;
import it.curdrome.timetogo.connection.viaggiatreno.CercaNumeroTrenoResponse;
import it.curdrome.timetogo.connection.viaggiatreno.CercaStazioneAsyncTask;
import it.curdrome.timetogo.connection.viaggiatreno.CercaStazioneResponse;
import it.curdrome.timetogo.connection.viaggiatreno.SoluzioniViaggioNewAsyncTask;
import it.curdrome.timetogo.connection.viaggiatreno.SoluzioniViaggioNewResponse;

/**
 * Created by adrian on 21/01/2017.
 * Model Class for the Transit
 *
 * @author adrian
 * @version 1
 */


public class Transit implements
        IdPalinaResponse,
        CercaStazioneResponse,
        SoluzioniViaggioNewResponse,
        CercaNumeroTrenoResponse{
    private Transit transit = this;

    private int numStops;
    private String departureStop;
    private String headsign;
    private String type;
    private String line;
    private String idPalina;
    private String departureTime;
    private Marker marker;
    private LatLng palinaLatLng;
    private String idDepartureStation;
    private String idHeadsignStation;
    private String idTrain;
    private String codLocOrig;
    private String delay;

    /**
     * Default constructor
     * @param numStops number of stops for this transit
     * @param departureStop name of the departure stop
     * @param headsign the headsign of this line
     * @param type the type of public transport(bus tram...)
     * @param line the name of the line
     * @param departureTime the departure time schedulated
     * @param lat the position of the departure stop
     * @param lng the position of the departure stop
     */
    public Transit(
            int numStops,
            String departureStop,
            String headsign,
            String type,
            String line,
            String departureTime,
            double lat,
            double lng){

        this.numStops = numStops;
        this.departureStop = departureStop;
        this.headsign = headsign;
        this.type = type;
        this.line = line;
        this.departureTime = departureTime;
        this.palinaLatLng = new LatLng(lat,lng);

        switch (this.type){
            case "BUS":
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                IdPalinaAsyncTask idPalinaBus = new IdPalinaAsyncTask(this);
                idPalinaBus.response = this;
                idPalinaBus.execute();
                break;
            case "TRAM":
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                IdPalinaAsyncTask idPalinaTram = new IdPalinaAsyncTask(this);
                idPalinaTram.response = this;
                idPalinaTram.execute();
                break;
            case "HEAVY_RAIL":
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                CercaStazioneAsyncTask idStazionePartenza = new CercaStazioneAsyncTask(this, this.departureStop);
                idStazionePartenza.response = this;
                idStazionePartenza.execute();
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                CercaStazioneAsyncTask idStazioneArrivo = new CercaStazioneAsyncTask(this, this.headsign);
                idStazioneArrivo.response = this;
                idStazioneArrivo.execute();

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

    public LatLng getPalinaLatLng() {
        return palinaLatLng;
    }

    public String getIdDepartureStation() {
        return idDepartureStation;
    }

    public void setIdDepartureStation(String idDepartureStation) {
        this.idDepartureStation = idDepartureStation;
    }

    public String getIdHeadsignStation() {
        return idHeadsignStation;
    }

    public void setIdHeadsignStation(String idHeadsignStation) {
        this.idHeadsignStation = idHeadsignStation;
    }

    public String getIdTrain() {
        return idTrain;
    }

    public void setIdTrain(String idTrain) {
        this.idTrain = idTrain;
    }

    public String getCodLocOrig() {
        return codLocOrig;
    }

    public void setCodLocOrig(String codLocOrig) {
        this.codLocOrig = codLocOrig;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    @Override
    public String toString(){
        return "\n num_stops :"+numStops+", departure_stop :"+departureStop+", headsign :"+headsign+", type: "+type+", line: "+line+", id_palina: "+idPalina +", departure_time: "+departureTime+"";
    }

    @Override
    public void TaskResultIdPalina(final Transit transit) {

        if(transit.getIdPalina()!=null){
            RTIAsyncTask rtiAsyncTask = new RTIAsyncTask(this);
            rtiAsyncTask.execute();
        }
    }

    @Override
    public void taskResultCercaStazione(String trainStationID, String station) {

        if(station.matches(departureStop)){
            idDepartureStation =  trainStationID;
        }else {
            idHeadsignStation =  trainStationID;
        }

        if(idDepartureStation!=null && idHeadsignStation!=null){
            SoluzioniViaggioNewAsyncTask soluzioniViaggioNewAsyncTask = new SoluzioniViaggioNewAsyncTask(idDepartureStation,idHeadsignStation,departureTime);
            soluzioniViaggioNewAsyncTask.response = this;
            soluzioniViaggioNewAsyncTask.execute();
        }
    }

    @Override
    public void taskResultSoluzioniViaggioNew(String idTrain) {

        this.idTrain = idTrain;

        Log.d("id train", idTrain);

        CercaNumeroTrenoAsyncTask cercaNumeroTrenoAsyncTask = new CercaNumeroTrenoAsyncTask(idTrain);
        cercaNumeroTrenoAsyncTask.response = this;
        cercaNumeroTrenoAsyncTask.execute();
    }


    @Override
    public void taskResultCercaNumeroTreno(String codLocOrig) {

            this.codLocOrig = codLocOrig;

        AndamentoTrenoAsyncTask andamentoTrenoAsyncTask = new AndamentoTrenoAsyncTask(this);
        andamentoTrenoAsyncTask.execute();

    }

    public void refreshIDPalina(){
        if(idPalina == null) {
            IdPalinaAsyncTask idPalinaBus = new IdPalinaAsyncTask(this);
            idPalinaBus.response = this;
            idPalinaBus.execute();
        }
    }

}
