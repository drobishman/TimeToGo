package it.curdrome.timetogo.connection.atac;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import it.curdrome.timetogo.model.Transit;
import it.curdrome.timetogo.xmlrpc.XMLRPCClient;
import it.curdrome.timetogo.xmlrpc.XMLRPCException;

/**
 * Created by adrian on 23/01/2017.
 */

public class RTIAsyncTask extends AsyncTask<String, String, String> {

    private Transit transit;

    public RTIAsyncTask(Transit transit){
        this.transit = transit;
    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrlAuth = "http://muovi.roma.it/ws/xml/autenticazione/1";
        String stringUrlPalina = "http://muovi.roma.it/ws/xml/paline/7";
        String key = "8T3U52HFT48N5GRnvImL4hF0rRChrKg9";
        String query = transit.getIdPalina();

        Log.d("RTI nome palina", transit.getDepartureStop());
        Log.d("RTI id palina", transit.getIdPalina());

        try {
            // token request
            XMLRPCClient authClient = new XMLRPCClient(new URL(stringUrlAuth));
            String token = (String) authClient.call("autenticazione.Accedi", key, "");
            Log.i("token for ATAC: ", token);

            //get palina using the name of the bus stop or part of it
            XMLRPCClient getPalina = new XMLRPCClient(new URL(stringUrlPalina));
            HashMap result = (HashMap) getPalina.call("paline.Previsioni", token, query, "ITA");
            JSONObject jsonResult = new JSONObject(result);
            JSONObject risposta = jsonResult.getJSONObject("risposta");

            Log.d("RTIAsyncTask", risposta.toString());

            JSONArray primiPerPalina = risposta.getJSONArray("primi_per_palina");
            for(int i=0; i<primiPerPalina.length();i++) {
                JSONObject palina = primiPerPalina.getJSONObject(i);
                JSONArray arrivi = palina.getJSONArray("arrivi");
                for(int j=0;j<arrivi.length();j++){
                    Log.d("RTI", transit.getLine() +" = "+arrivi.getJSONObject(j).getString("linea"));
                    if(transit.getLine().matches(arrivi.getJSONObject(j).getString("linea")) && arrivi.getJSONObject(j).has("annuncio")){
                        transit.setDepartureTime(arrivi.getJSONObject(j).getString("annuncio"));
                    }
                }
            }

        } catch (XMLRPCException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
