package it.curdrome.timetogo.connection.viaggiatreno;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adrian on 05/04/2017.
 */

public class CercaNumeroTrenoAsyncTask extends AsyncTask<String, String, String> {

    public CercaNumeroTrenoResponse response = null;

    private String idTrain;

    public CercaNumeroTrenoAsyncTask(String idTrain){
        this.idTrain = idTrain;
    }

    @Override
    protected String doInBackground(String... strings) {


        String stringUrl = "http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/cercaNumeroTreno/"+idTrain;

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

        Log.d("id treno e partenza", result);

        String codLocOrig = "";
        try {
            JSONObject res = new JSONObject(result);
            codLocOrig = res.getString("codLocOrig");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.taskResultCercaNumeroTreno(codLocOrig);
    }
}
//sample
// http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/cercaNumeroTrenoTrenoAutocomplete/12256
// la risposta e fatta cosi:
/*
{
  "numeroTreno": "12256",
  "codLocOrig": "S08409",
  "descLocOrig": "ROMA TERMINI",
  "dataPartenza": 1491343200000,
  "corsa": "12256A"
}
 */