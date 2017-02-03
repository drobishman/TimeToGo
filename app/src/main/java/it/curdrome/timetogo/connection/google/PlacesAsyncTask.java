package it.curdrome.timetogo.connection.google;

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
import it.curdrome.timetogo.model.Category;
import it.curdrome.timetogo.model.Place;

/**
 * Created by adrian on 31/01/2017.
 */

public class PlacesAsyncTask extends AsyncTask<String,String,String> {

    public PlacesResponse response = null;

    private MainActivity activity;
    private LatLng mOrigin;
    private String type;
    private GoogleMap mMap;

    public PlacesAsyncTask (LatLng mOrigin, GoogleMap mMap, MainActivity activity, String type){

        this.activity = activity;
        this.mOrigin = mOrigin;
        this.mMap = mMap;
        this.type = type;
    }

    @Override
    protected String doInBackground(String... args) {

        String stringUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + mOrigin.latitude+","+mOrigin.longitude +
                "&radius=1500" +
                "&type=" + type +
                "&key=AIzaSyC-aixk9aHpKdhiL__VPxZs_xpx4Z-JdRs";

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

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        List<Place> places= new ArrayList<>();

        try {
            JSONObject ja = new JSONObject(s);
            JSONArray results = ja.getJSONArray("results");

            for(int i=0; i<results.length();i++){
                JSONObject jsonPlace = results.getJSONObject(i);
                JSONObject geometry = jsonPlace.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                String name = jsonPlace.getString("name");
                boolean openNow = false;
                boolean openHoursEnabled = false;
                if(jsonPlace.has("opening_hours")){
                    openHoursEnabled = true;
                    JSONObject openingHours = jsonPlace.getJSONObject("opening_hours");
                    openNow = openingHours.getBoolean("open_now");
                }
                String placeId = jsonPlace.getString("place_id");
                String vicinity = jsonPlace.getString("vicinity");
                JSONArray jsonTypes = jsonPlace.getJSONArray("types");
                List<Category> categories = new ArrayList<>();
                for(int j=0; j<jsonTypes.length();j++)
                    categories.add(new Category(j,jsonTypes.getString(j)));

                places.add(new Place(
                        mMap,
                        activity,
                        (new LatLng(location.getDouble("lat"),location.getDouble("lng"))),
                        name,
                        openHoursEnabled,
                        openNow,
                        placeId,
                        categories,
                        vicinity
                ));
            }

            Log.d("PlacesAsyncTask", places.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(places == null) {
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main), "places = null", Snackbar.LENGTH_LONG);

            snackbar.show();
        }else
            response.TaskResult(places);
    }
}
