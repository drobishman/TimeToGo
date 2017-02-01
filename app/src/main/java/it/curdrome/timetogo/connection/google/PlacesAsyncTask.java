package it.curdrome.timetogo.connection.google;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adrian on 31/01/2017.
 */

public class PlacesAsyncTask extends AsyncTask<String,String,String> {


    protected String doInBackground(String... args) {
        //Intent i = getIntent();

        String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=";


        String output = null;

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection httpconn = (HttpURLConnection) url
                    .openConnection();
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(httpconn.getInputStream()),
                        8192);
                String strLine;

                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
            }

            output = response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
