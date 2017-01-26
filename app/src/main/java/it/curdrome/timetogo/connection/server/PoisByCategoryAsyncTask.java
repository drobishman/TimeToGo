package it.curdrome.timetogo.connection.server;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Category;
import it.curdrome.timetogo.model.Poi;

/**
 * Created by adrian on 19/01/2017.
 */

public class PoisByCategoryAsyncTask extends AsyncTask<String, String, String> {

    private MainActivity activity;
    private GoogleMap mMap;
    private int categoryId;
    public PoisByCategoryResponse response = null;
    private List<Poi> pois = new ArrayList<>();

    public PoisByCategoryAsyncTask(MainActivity activity, GoogleMap mMap, int categoryId){

        this.activity = activity;
        this.mMap = mMap;
        this.categoryId = categoryId;
    }


    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://projectis-curdrome.rhcloud.com//android/listpoisbycategory/"+categoryId+"";

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

            String jsonOutput = response.toString();

            return jsonOutput;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        try {
            JSONArray response = new JSONArray(result);
            for(int i=0; i<response.length();i++){
                JSONObject poi = response.getJSONObject(i);
                Log.d("id dei poi",poi.getInt("id")+"");
                pois.add(new Poi(
                        poi.getInt("id"),
                        null,
                        (new Category(poi.getInt("category.id"), poi.getString("category.name"))),
                        poi.getString("name"),
                        poi.getDouble("lat"),
                        poi.getDouble("lng"),
                        poi.getString("description"),
                        mMap,
                        activity
                        ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("response", result);
/*
        Gson gson = new Gson();
        Type type = new TypeToken<List<Poi>>(){}.getType();
        pois = gson.fromJson(result, type);
*/
        for (Poi poi : pois){

        }





        response.taskResult(pois);
    }
}
