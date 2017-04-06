package it.curdrome.timetogo.connection.viaggiatreno;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by adrian on 04/04/2017.
 */

public class SoluzioniViaggioNewAsyncTask extends AsyncTask<String, String, String> {

    public SoluzioniViaggioNewResponse response = null;

    private String idDepartureStation;
    private String idHeadsignStation;
    private String hour;

    public SoluzioniViaggioNewAsyncTask(String idDepartureStation, String idHeadsignStation, String hour){
        this.idDepartureStation = idDepartureStation;
        this.idHeadsignStation = idHeadsignStation;
        this.hour = hour;
    }

    @Override
    protected String doInBackground(String... strings) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        String time= df.format(Calendar.getInstance().getTime());

        try {
            time = time.concat("T"+convertTo24HoursFormat(hour)+":00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        idDepartureStation = idDepartureStation.replace("S","");
        idHeadsignStation = idHeadsignStation.replace("S","");

        String stringUrl = "http://www.viaggiatreno.it/viaggiatrenonew/resteasy/viaggiatreno/soluzioniViaggioNew/"+idDepartureStation+"/"+idHeadsignStation+"/"+time;

        Log.d("soluzioni viaggio", stringUrl);

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

            Log.d("soluzioniViaggioNew", jsonOutput);

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

        String numeroTreno = "";

        try {
            JSONObject res = new JSONObject(result);
            JSONArray soluzioni = res.getJSONArray("soluzioni");
            JSONObject soluzione = soluzioni.getJSONObject(0);
            JSONArray vehicles = soluzione.getJSONArray("vehicles");
            JSONObject vehicle = vehicles.getJSONObject(0);
            numeroTreno = vehicle.getString("numeroTreno");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    response.taskResultSoluzioniViaggioNew(numeroTreno);

    }

    // Replace with KK:mma if you want 0-11 interval
    private static final DateFormat TWELVE_TF = new SimpleDateFormat("hh:mma");
    // Replace with kk:mm if you want 1-24 interval
    private static final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");

    public static String convertTo24HoursFormat(String twelveHourTime) throws ParseException {
        return TWENTY_FOUR_TF.format(
                TWELVE_TF.parse(twelveHourTime));
    }
}