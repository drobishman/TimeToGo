package it.curdrome.timetogo.connection.atac;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.curdrome.timetogo.model.Transit;
import it.curdrome.timetogo.xmlrpc.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adrian on 20/01/2017.
 */

public class IdPalinaAsyncTask extends AsyncTask<String, String, String> {

    private Transit transit;
    private List<String> paline = new ArrayList<>();

    public IdPalinaAsyncTask(Transit transit){
        this.transit = transit;
    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrlAuth = "http://muovi.roma.it/ws/xml/autenticazione/1";
        String stringUrlPalina = "http://muovi.roma.it/ws/xml/paline/7";
        String key = "8T3U52HFT48N5GRnvImL4hF0rRChrKg9";

        String query = transit.getDepartureStop();

        if(query.contains("-")) {
            String splitted[] = query.split("-");
            query = splitted[1];
        }
        JSONObject jsonResult;//new JSONObject();

        try {

            // token request
            XMLRPCClient authClient = new XMLRPCClient(new URL(stringUrlAuth));
            String token = (String) authClient.call("autenticazione.Accedi", key, "");
            Log.i("token for ATAC: ", token);

            //get palina using the name of the bus stop or part of it
            XMLRPCClient getPalina = new XMLRPCClient(new URL(stringUrlPalina));
            HashMap result = (HashMap) getPalina.call("paline.SmartSearch", token, query);
            jsonResult = new JSONObject(result);
            JSONObject risposta = jsonResult.getJSONObject("risposta");
            JSONArray palineExtra = risposta.getJSONArray("paline_extra");

            // cycle to get palina id
            for (int i =0; i< palineExtra.length(); i++) {
                JSONObject palina = palineExtra.getJSONObject(i);
                JSONArray lineeInfo = palina.getJSONArray("linee_info");
                paline.add(palina.getString("id_palina"));

                // cycle to get infos of the line and if headsign matches check if our line pass it in case of multiple answers
                for (int j = 0; j < lineeInfo.length(); j++) {
                    JSONObject info = lineeInfo.getJSONObject(j);

                    // check for our direction
                    if (info.getString("direzione").equalsIgnoreCase(transit.getHeadsign())){

                        XMLRPCClient getLinee = new XMLRPCClient(new URL(stringUrlPalina));
                        HashMap palinaLinee = (HashMap) getLinee.call("paline.PalinaLinee", token, palina.getString("id_palina"),"ITA");
                        JSONObject palinaLineeJson = new JSONObject(palinaLinee);
                        JSONArray linee = palinaLineeJson.getJSONArray("risposta");

                        // check for every stop if our line pass there
                        for(int k =0;k<linee.length();k++){
                            JSONObject linea = linee.getJSONObject(k);
                            // if yes assign the id to our transit
                            if(linea.getString("linea").equals(transit.getLine()))
                                transit.setIdPalina(paline.get(i));
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XMLRPCException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return transit.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Log.d("onPostExecute", s);
    }
}
