package it.curdrome.timetogo.connection.atac;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import it.curdrome.timetogo.xmlrpc.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by adrian on 20/01/2017.
 */

public class GetRTI extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... strings) {

        String stringUrlAuth = "http://muovi.roma.it/ws/xml/autenticazione/1";
        String stringUrlGetPalina = "http://muovi.roma.it/ws/xml/paline/7";
        String key = "8T3U52HFT48N5GRnvImL4hF0rRChrKg9";

        String query = "SORBONA";
        JSONObject json =  null;//new JSONObject();

        try {
            XMLRPCClient authClient = new XMLRPCClient(new URL(stringUrlAuth));
            String token = (String) authClient.call("autenticazione.Accedi", key, "");
            Log.d("this is token : ", token);

            XMLRPCClient getPalina = new XMLRPCClient(new URL(stringUrlGetPalina));
            HashMap result = (HashMap) getPalina.call("paline.SmartSearch", token, query);

            json = new JSONObject(result);

            Log.d("GetRTI",json.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XMLRPCException e) {
            e.printStackTrace();
        }


        return null;
    }
}
