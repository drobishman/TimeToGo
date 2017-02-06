package it.curdrome.timetogo.connection.google;

import it.curdrome.timetogo.model.Route;

/**
 * Created by adrian on 24/01/2017.
 *
 * interface used to get the response of the DirectionAsyncTask
 *
 * @author Drob Adrian Mihai
 * @version 1
 */

public interface DirectionResponse {
    void TaskResult(Route route);
}
