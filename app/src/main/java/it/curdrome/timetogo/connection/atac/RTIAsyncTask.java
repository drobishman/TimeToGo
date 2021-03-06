package it.curdrome.timetogo.connection.atac;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

import it.curdrome.timetogo.model.Transit;
import it.curdrome.timetogo.xmlrpc.XMLRPCClient;
import it.curdrome.timetogo.xmlrpc.XMLRPCException;

/**
 * Created by adrian on 23/01/2017.
 * class used to calculate using Roma.mobilita API
 * the RTI informatio
 *
 * @author Drob Adrian Mihai
 * @version 2
 */

public class RTIAsyncTask extends AsyncTask<String, String, String> {

    private Transit transit;

    /**
     * constructor method
     * @param transit to get its RTI
     */
    public RTIAsyncTask(Transit transit){
        this.transit = transit;
    }

    /**
     *
     * Override method to get the RTI infos from muovi.roma
     * parse its result and override google info in case is availble
     *
     * @param strings default parameter
     * @return a transit to debug info
     */
    @Override
    protected String doInBackground(String... strings) {

        String stringUrlAuth = "http://muovi.roma.it/ws/xml/autenticazione/1";
        String stringUrlPalina = "http://muovi.roma.it/ws/xml/paline/7";
        String key = "8T3U52HFT48N5GRnvImL4hF0rRChrKg9";
        String query = transit.getIdPalina();

        try {
            // token request
            XMLRPCClient authClient = new XMLRPCClient(new URL(stringUrlAuth));
            String token = (String) authClient.call("autenticazione.Accedi", key, "");

            //get palina using the name of the bus stop or part of it
            XMLRPCClient getPalina = new XMLRPCClient(new URL(stringUrlPalina));
            HashMap result = (HashMap) getPalina.call("paline.Previsioni", token, query, Locale.getDefault().getDisplayLanguage());
            JSONObject jsonResult = new JSONObject(result);



            return jsonResult.toString();

        } catch (XMLRPCException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jo = new JSONObject(result);
            JSONObject risposta = jo.getJSONObject("risposta");
            JSONArray primiPerPalina = risposta.getJSONArray("primi_per_palina");
            for (int i = 0; i < primiPerPalina.length(); i++) {
                JSONObject palina = primiPerPalina.getJSONObject(i);
                JSONArray arrivi = palina.getJSONArray("arrivi");
                for (int j = 0; j < arrivi.length(); j++) {
                    if (transit.getLine().matches(arrivi.getJSONObject(j).getString("linea")) && arrivi.getJSONObject(j).has("annuncio")) {
                        transit.setDepartureTime(arrivi.getJSONObject(j).getString("annuncio"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("RTI Atac", transit.getLine() +" - "+ transit.getDepartureTime());
    }
}
