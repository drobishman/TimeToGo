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



            //check if tipo is NOT ambiguous
            if(risposta.getString("tipo").matches("Palina")){
                //here i have found the id_palina
                transit.setIdPalina(risposta.getString("id_palina"));
            }else if (risposta.getString("tipo").matches("Ambiguo")){                           //if tipo is "ambiguo"
                JSONArray paline_semplice = risposta.getJSONArray("paline_semplice");
                if (!paline_semplice.isNull(0)) {                                               //if paline_semplice isn't void
                    for (int i = 0; i < paline_semplice.length(); i++) {                        //for each item in paline_semplice
                        paline.add(paline_semplice.getJSONObject(i).getString("id_palina"));    //add all the id_palina found in paline for future filtering
                    }
                }else {
                    JSONArray paline_extra = risposta.getJSONArray("paline_extra");             //if the paline_semplice is void, i'm looking for the paline_extra array
                    if (!paline_extra.isNull(0)){
                        for (int i = 0; i < paline_extra.length(); i++) {                        //for each item in paline_extra
                            paline.add(paline_extra.getJSONObject(i).getString("id_palina"));    //add all the id_palina found in paline for future filtering
                        }
                    }
                }
                //start the filtering on paline
                for (int i = 0; i < paline.size(); i++) {                           //for each id_palina in paline
                    XMLRPCClient getLinee = new XMLRPCClient(new URL(stringUrlPalina));
                    HashMap palinaLinee = (HashMap) getLinee.call("paline.PalinaLinee", token, paline.get(i),"ITA");
                    JSONObject palinaLineeJson = new JSONObject(palinaLinee);
                    JSONArray linee = palinaLineeJson.getJSONArray("risposta");     //ask which line passing by id_palina
                    for (int j = 0; j < linee.length(); j++) {                      //for each line
                        boolean found=false;
                        if (linee.getJSONObject(j).getString("linea").equalsIgnoreCase(transit.getLine()))  //if line match
                            found=true;                                             //MATCH FOUND!
                        else if (j==linee.length()&&found==false){                  //otherwise if haven't found anything
                            paline.remove(i);                                       //remove the item in i position from list paline
                            i--;                                                    //now the item in i+1 position is passed in position i, so for non jumping check of "new i" element i reduce i to i-1;
                        }
                    }
                }
                for (int i = 0; i < paline.size(); i++){
                    XMLRPCClient getPrevisioni = new XMLRPCClient(new URL(stringUrlPalina));
                    HashMap palinePrevisioni = (HashMap) getPrevisioni.call("paline.Previsioni",token,paline.get(i),"ITA");
                    JSONObject palinePrevisioniJson = new JSONObject(palinePrevisioni);


                    JSONObject rispostaPrevisioni = palinePrevisioniJson.getJSONObject("risposta");
                    JSONArray primi_per_palina = rispostaPrevisioni.getJSONArray("primi_per_palina");
                    for (int j = 0; j < primi_per_palina.length() ; j++) {

                        JSONObject primo_per_palina = primi_per_palina.getJSONObject(j);
                        JSONArray arrivi = primo_per_palina.getJSONArray("arrivi");
                        for (int k = 0; k < arrivi.length(); k++) {
                            if (transit.getLine().matches(arrivi.getJSONObject(k).getString("linea"))){
                                if (arrivi.getJSONObject(k).has("capolinea"))
                                    if (transit.getHeadsign().equalsIgnoreCase(arrivi.getJSONObject(k).getString("capolinea"))){
                                        transit.setIdPalina(arrivi.getJSONObject(k).getString("id_palina"));
                                    }
                            }
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
