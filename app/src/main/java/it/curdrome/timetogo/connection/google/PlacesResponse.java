package it.curdrome.timetogo.connection.google;

import java.util.List;

import it.curdrome.timetogo.model.Place;

/**
 * Created by adrian on 03/02/2017.
 *
 * Interface used to get the response from the PlacesAsyncTask
 *
 * @author Drob Adrian Mihai
 * @version 1
 */

public interface PlacesResponse {
    void TaskResult(List<Place> places);
}