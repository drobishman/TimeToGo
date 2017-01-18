package it.curdrome.timetogo.connection.server;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.connection.AsyncResponse;
import it.curdrome.timetogo.model.Category;

/**
 * Created by adrian on 16/01/2017.
 */

public class GetCategories extends AsyncTask<String, String, String> {

    public AsyncResponse response = null;

    private String[] stringArray;

    public ProgressDialog pDialog; // to show when direction create

    private MainActivity activity;

    public GetCategories(MainActivity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        super.onPreExecute();
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(activity.getString(R.string.loading_route_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

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

            Log.d("get direction", stringArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        response.taskResult(stringArray);
        pDialog.dismiss();
    }
}
