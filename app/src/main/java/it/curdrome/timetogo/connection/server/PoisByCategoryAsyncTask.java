package it.curdrome.timetogo.connection.server;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Category;
import it.curdrome.timetogo.model.Poi;

/**
 * Created by adrian on 19/01/2017.
 *
 * Class used to get Pois by category from our servers
 *
 * @author Drob Adrian Mihai
 * @version 2
 *
 */

public class PoisByCategoryAsyncTask extends AsyncTask<String, String, String> {

    private MainActivity activity;
    private GoogleMap mMap;
    private String categoryName;
    public PoisByCategoryResponse response = null;
    private List<Poi> pois = new ArrayList<>();

    /**
     * default constructor method
     * @param activity caller activity
     * @param mMap the map where to ve draw
     * @param categoryName the id of the category
     */
    public PoisByCategoryAsyncTask(MainActivity activity, GoogleMap mMap, String categoryName){

        this.activity = activity;
        this.mMap = mMap;
        this.categoryName = categoryName;
    }


    /**
     * Http request
     *
     * @param strings default parameter
     * @return null
     */
    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://projectis-curdrome.rhcloud.com/listpoisbycategory/"+categoryName+"";

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

    /**
     * all pois will be created and added to a List of Pois and send as response
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if(result==null){
            response.taskResult(null);
        }else
            try {
                JSONArray response = new JSONArray(result);
                for(int i=0; i<response.length();i++){
                    JSONObject poi = response.getJSONObject(i);
                    JSONArray categories = poi.getJSONArray("categories");

                    List<Category> listCategories = new ArrayList<>();
                    for(int j = 0; j<categories.length();j++)
                        listCategories.add(new Category(
                                categories.getJSONObject(j).getInt("id"),
                                categories.getJSONObject(j).getString("name")));

                    pois.add(new Poi(
                            poi.getInt("id"),
                            poi.getString("id_places"),
                            listCategories,
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

        Log.d("POIs", result);
        response.taskResult(pois);
    }
}
