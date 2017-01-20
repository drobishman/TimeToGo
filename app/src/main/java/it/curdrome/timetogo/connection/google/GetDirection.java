package it.curdrome.timetogo.connection.google;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

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
import it.curdrome.timetogo.connection.atac.GetRTI;

/*
   class that generates a route direction using google maps api and print it on map
     */
public class GetDirection extends AsyncTask<String, String, String> {

    public ProgressDialog pDialog; // to show when direction create

    private MainActivity activity;
    private List<LatLng> polyz;
    private LatLng mOrigin;
    private LatLng mDestination;
    private LatLng northeast;
    private LatLng southwest;

    private GoogleMap mMap;

    public GetDirection(LatLng mOrigin, LatLng mDestination, GoogleMap mMap, MainActivity activity){

        this.activity = activity;
        this.mOrigin = mOrigin;
        this.mDestination = mDestination;
        this.mMap = mMap;
    }

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

    protected String doInBackground(String... args) {
        //Intent i = getIntent();

        String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin="
                + mOrigin.latitude
                + ","
                + mOrigin.longitude
                + "&destination="
                + mDestination.latitude
                + ","
                + mDestination.longitude + "&sensor=false&mode=transit";

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection httpconn = (HttpURLConnection) url
                    .openConnection();
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(httpconn.getInputStream()),
                        8192);
                String strLine = null;

                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
            }

            String jsonOutput = response.toString();

            JSONObject jsonObject = new JSONObject(jsonOutput);

            // routesArray contains ALL routes
            JSONArray routesArray = jsonObject.getJSONArray("routes");
            // Grab the first route
            JSONObject route = routesArray.getJSONObject(0);

            getBounds(route);

            JSONObject poly = route.getJSONObject("overview_polyline");
            String polyline = poly.getString("points");
            polyz = decodePoly(polyline);

        } catch (Exception e) {

        }

        return null;

    }

    private void getBounds (JSONObject route) throws JSONException {

        JSONObject bounds = route.getJSONObject("bounds");
        JSONObject northeast = bounds.getJSONObject("northeast");
        JSONObject southwest = bounds.getJSONObject("southwest");

        this.northeast = new LatLng( northeast.getDouble("lat"),  northeast.getDouble("lng"));
        this.southwest = new LatLng( southwest.getDouble("lat"),  southwest.getDouble("lng"));
    }

    protected void onPostExecute(String file_url) {

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        new GetRTI().execute();

        for (int i = 0; i < polyz.size() - 1; i++) {
            LatLng src = polyz.get(i);
            LatLng dest = polyz.get(i + 1);
            mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude))
                    .width(4).color(Color.RED));

        }
        pDialog.dismiss();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast),100));
    }

    /* Method to decode polyline points */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}