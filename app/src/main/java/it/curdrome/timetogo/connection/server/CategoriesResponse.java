package it.curdrome.timetogo.connection.server;

/**
 * Created by adrian on 18/01/2017.
 *
 * interface used to get the response of the CategoriesAsyncTask
 *
 * @author Drob Adrian Mihai
 * @version 1
 */

public interface CategoriesResponse {
    void taskResult(String[] output);
}
