package it.curdrome.timetogo.connection.server;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.curdrome.timetogo.activity.MainActivity;

/**
 * Created by adrian on 16/01/2017.
 */

public class GetCategories extends AsyncTask<String, String, String> {

    public GetCategoriesResponse response = null;

    private String[] stringArray;

    private MainActivity activity;

    public GetCategories(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://projectis-curdrome.rhcloud.com/android/listcategories";

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
            JSONArray ja = new JSONArray(result);
            stringArray = new String[ja.length()];

            for (int i=0; i<ja.length();i++) {
                stringArray[i]=ja.getJSONObject(i).getString("name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.taskResult(stringArray);
    }
}
