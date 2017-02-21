package it.curdrome.timetogo.connection.server;

import java.util.List;

import it.curdrome.timetogo.model.Poi;

/**
 * Created by adrian on 24/01/2017.
 *
 * interface used to get the response of the PoisByCategoryAsyncTask
 *
 * @author Drob Adrian Mihai
 * @version 1
 */

public interface PoisByCategoryResponse {
    void taskResult(List<Poi> pois);
}
