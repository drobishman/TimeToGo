package it.curdrome.timetogo.connection.viaggiatreno;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.curdrome.timetogo.model.Transit;

/**
 * Created by adrian on 05/04/2017.
 */

public class AndamentoTrenoAsyncTask extends AsyncTask<String, String, String> {

    private Transit transit;

    public AndamentoTrenoAsyncTask(Transit transit){
        this.transit = transit;
    }

    @Override
    protected String doInBackground(String... strings) {


        String stringUrl = "http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/andamentoTreno/"+transit.getCodLocOrig()+"/"+transit.getIdTrain();

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

        String ritardo ="";

        try {
            JSONObject res = new JSONObject(result);
            ritardo = res.getString("ritardo");
            transit.setDelay(ritardo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("RTI viaggiotreno",transit.getIdTrain() +" - "+ transit.getDelay());
    }
}

//sample
//http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/andamentoTreno/S08409/12256
    /*
    {
            "tipoTreno": "PG",
             "orientamento": null,
             "codiceCliente": 2,
             "fermateSoppresse": null,
             "dataPartenza": null,
              "fermate": [....]
               "anormalita": null,
            "provvedimenti": null,
             "segnalazioni": null,
              "oraUltimoRilevamento": null,
             "stazioneUltimoRilevamento": "--",
             "idDestinazione": "S08010",
             "idOrigine": "S08409",
             "ritardo": 0,
             .
             .
             .
             .
     */

