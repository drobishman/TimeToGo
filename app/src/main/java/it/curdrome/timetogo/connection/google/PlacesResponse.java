package it.curdrome.timetogo.connection.google;

import java.util.List;

import it.curdrome.timetogo.model.Place;

/**
 * Created by adrian on 03/02/2017.
 */

public interface PlacesResponse {
    void TaskResult(List<Place> places);
}