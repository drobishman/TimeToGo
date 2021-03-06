package it.curdrome.timetogo.connection.google;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Route;
import it.curdrome.timetogo.model.Transit;

/**
 *
 *class that generates a route direction using google maps api and print it on map
 * @author Drob Adrian Mihai
 * @version 3
 */
public class DirectionAsyncTask extends AsyncTask<String, String, String> {

    // sets response
    public DirectionResponse response = null;

    private ProgressDialog pDialog; // to show when direction create

    private MainActivity activity; // caller activity
    private LatLng mOrigin;
    private LatLng mDestination;
    private List<Route> routes = new ArrayList<>();
    private String mode;

    // bounds
    private LatLng northeast;
    private LatLng southwest;

    private GoogleMap mMap;

    /**
     * Constructor method
     *
     * @param mOrigin lat long for the origin(user position)
     * @param mDestination coordinates of the chosen destination
     * @param mMap the map where decode the result
     * @param activity the caller activity
     * @param mode the mode of the henerated route
     */
    public DirectionAsyncTask (LatLng mOrigin, LatLng mDestination, GoogleMap mMap, MainActivity activity, String mode){

        this.activity = activity;
        this.mOrigin = mOrigin;
        this.mDestination = mDestination;
        this.mMap = mMap;
        this.mode = mode;
    }

    /**
     * method used to start Progress Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(activity.getString(R.string.loading_route_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    /**
     * request method to create a route
     * @param args default param
     * @return an output string containing the route info
     */
    protected String doInBackground(String... args) {

        // creation of the url
        String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin="
                + mOrigin.latitude
                + ","
                + mOrigin.longitude
                + "&destination="
                + mDestination.latitude
                + ","
                + mDestination.longitude + "&sensor=false&mode="+mode
                + "&alternatives=true";
        // response variable
        String output = null;

        Log.d("URL ", stringUrl);

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

    /**
     * Method used tu manipulate the response and create the needed objects and assembly rhe route
     * @param output is the output of the http request
     */
    protected void onPostExecute(String output) {

        JSONObject jsonObject;
        if(output==null){
            response.TaskResultRoutes(null);
        }else
            try {
                jsonObject = new JSONObject(output);
                Log.d("output",jsonObject.toString());

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");

                Log.d("routes json", routesArray.length()+"");
                // Grab the first route

                for(int i=0; i<routesArray.length();i++) {

                    JSONObject route = routesArray.getJSONObject(i);

                    getBounds(route);

                    getRoute(route);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        // dismiss the progress dialog
        pDialog.dismiss();

        if(routes.isEmpty()){
            if (mode.matches("transit")) {
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.main), activity.getString(R.string.route_not_found) + ": " +activity.getString(R.string.transit), Snackbar.LENGTH_LONG);
                snackbar.show();
            }else if(mode.matches("walking")) {
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.main), activity.getString(R.string.route_not_found) + ": " + activity.getString(R.string.walking), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        else{
            for (Route route: routes){
                Log.d("ROUTE",route.toString());
            }
            response.TaskResultRoutes(routes);
        }


    }

    /**
     * method that creates a route
     * @param route a JSONObject containing the main info of the route
     * @throws JSONException when the json object creation fails
     */
    private void getRoute(JSONObject route) throws JSONException {

        JSONArray arrayLegs = route.getJSONArray("legs");
        JSONObject legs = arrayLegs.getJSONObject(0);

        String arrivalTimeText = null;
        String departureTimeText = null;

        if(!mode.matches("walking")) {
            JSONObject arrivalTime = legs.getJSONObject("arrival_time");
            arrivalTimeText = arrivalTime.getString("text");

            JSONObject departureTime = legs.getJSONObject("departure_time");
            departureTimeText = departureTime.getString("text");
        }


        JSONObject distance = legs.getJSONObject("distance");
        String distanceText = distance.getString("text");

        JSONObject duration = legs.getJSONObject("duration");
        String durationText = duration.getString("text");

        JSONObject poly = route.getJSONObject("overview_polyline");
        String points = poly.getString("points");

        routes.add(new Route(
                mMap,
                points,
                arrivalTimeText,
                departureTimeText,
                distanceText,
                durationText,
                getTransit(route),
                southwest,
                northeast,
                mode
                )
        );
    }

    /**
     * method that creates a transit
     * @param route a JSONObject containing the main info of the route and all transits
     * @throws JSONException when the json object creation fails
     */
    private List<Transit> getTransit(JSONObject route) throws JSONException {

        List<Transit> transits = new ArrayList<>();

        JSONArray arrayLegs = route.getJSONArray("legs");
        JSONObject legs = arrayLegs.getJSONObject(0);
        JSONArray stepsArray = legs.getJSONArray("steps");

        for(int i=0; i<stepsArray.length();i++){
            JSONObject steps = stepsArray.getJSONObject(i);
            String travelmode = steps.getString("travel_mode");
            if(travelmode.matches("TRANSIT")){
                JSONObject transitDetails = steps.getJSONObject("transit_details");
                int numStops = transitDetails.getInt("num_stops");
                JSONObject departureStop = transitDetails.getJSONObject("departure_stop");
                String departureStopName = departureStop.getString("name");
                JSONObject location = departureStop.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                String headsign = transitDetails.getString("headsign");
                JSONObject line = transitDetails.getJSONObject("line");
                String shortName = line.getString("short_name");
                JSONObject vehicle = line.getJSONObject("vehicle");
                String vehicleType = vehicle.getString("type");
                JSONObject departureTime = transitDetails.getJSONObject("departure_time");
                String departureTimeText = departureTime.getString("text");
                transits.add(new Transit(
                        numStops,
                        departureStopName,
                        headsign,
                        vehicleType,
                        shortName,
                        departureTimeText,
                        lat,
                        lng));
            }
        }
        return transits;
    }

    /**
     *
     * @param route JSONObject containing the main info of the route and also its bounds on map
     * @throws JSONException when the json object creation fails
     */
    private void getBounds (JSONObject route) throws JSONException {

        JSONObject bounds = route.getJSONObject("bounds");
        JSONObject northeast = bounds.getJSONObject("northeast");
        JSONObject southwest = bounds.getJSONObject("southwest");

        this.northeast = new LatLng( northeast.getDouble("lat"),  northeast.getDouble("lng"));
        this.southwest = new LatLng( southwest.getDouble("lat"),  southwest.getDouble("lng"));
    }
}