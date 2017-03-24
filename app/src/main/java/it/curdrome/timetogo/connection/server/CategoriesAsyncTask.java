package it.curdrome.timetogo.connection.server;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adrian on 16/01/2017.
 *
 * Class used to get Categories from our servers
 *
 * @author Drob Adrian Mihai
 * @version 2
 *
 */

public class CategoriesAsyncTask extends AsyncTask<String, String, String> {

    public CategoriesResponse response = null;

    private String[] stringArray;

    /**
     * Http request
     *
     * @param strings default parameter
     * @return null
     */
    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://projectis-curdrome.rhcloud.com/listcategories";

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

    /**
     * all categories will be added to a String array and send as response
     * @param result
     */
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
