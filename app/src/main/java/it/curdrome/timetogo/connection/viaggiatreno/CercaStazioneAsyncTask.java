package it.curdrome.timetogo.connection.viaggiatreno;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.curdrome.timetogo.model.Transit;

/**
 * Created by adrian on 04/04/2017.
 */

public class CercaStazioneAsyncTask extends AsyncTask<String, String, String> {


    public CercaStazioneResponse response = null;
    private Transit transit;
    private String trainStation;

    public CercaStazioneAsyncTask(Transit transit, String stopName){
        this.transit = transit;
        this.trainStation = stopName;
    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/cercaStazione/"+trainStation;

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

        JSONArray ja = null;
        JSONObject trainStation = null;
        String trainStationID = null;
        try {
            ja = new JSONArray(result);
            trainStation = ja.getJSONObject(0);
            trainStationID = trainStation.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.taskResultCercaStazione(trainStationID, this.trainStation);
    }

}
