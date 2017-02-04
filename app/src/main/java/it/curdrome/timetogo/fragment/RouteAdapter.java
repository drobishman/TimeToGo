package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Route;
import it.curdrome.timetogo.model.Transit;

/**
 * Created by adrian on 02/02/2017.
 */

public class RouteAdapter extends ArrayAdapter<Route> implements Serializable {

    MainActivity activity;

    public RouteAdapter(Context context, int resource, List<Route> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        activity = mainActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.route_row, null);

        TextView arrival = (TextView) convertView.findViewById(R.id.arrival);
        TextView departure = (TextView) convertView.findViewById(R.id.departure);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        TextView busStops = (TextView) convertView.findViewById(R.id.bus_stops);


        final Route route = getItem(position);

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String time= df.format(Calendar.getInstance().getTime());
        if (route.getArrivalTime()!=null) {
            arrival.setText(activity.getString(R.string.arrival_time) +" "+ route.getArrivalTime());
        }else{
            int hour = Integer.parseInt(time.substring(0,2));
            int minute = Integer.parseInt(time.substring(3,5));
            String[] durationstring= route.getDuration().split(" ");
            if (durationstring.length==4) {
                hour = hour + Integer.parseInt(durationstring[0]);
                if(hour==24){
                    hour=0;
                }
                if(hour>24){
                    hour=hour-24;
                }
                minute = minute + Integer.parseInt(durationstring[2]);
                if (minute==60){
                    minute=0;
                    hour++;
                }
                if (minute>60){
                    minute=minute-60;
                    hour++;
                }
                arrival.setText(activity.getString(R.string.arrival_time) + " " + Integer.toString(hour) + ":" + Integer.toString(minute));
            }else if(durationstring.length==2){
                minute = minute + Integer.parseInt(durationstring[0]);
                arrival.setText(activity.getString(R.string.arrival_time) + " " + Integer.toString(hour) + ":" + Integer.toString(minute));
            }else if(durationstring.length==6){
                Snackbar snackbar = Snackbar
                        .make(activity.findViewById(R.id.main), activity.getString(R.string.error_occured), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        if (route.getDepartureTime()!=null) {
            departure.setText(activity.getString(R.string.departure_time) + route.getDepartureTime());
        }else {
            departure.setText(activity.getString(R.string.departure_time)+" "+time.substring(0,5));

        }
        distance.setText(activity.getString(R.string.distance) + route.getDistance());
        duration.setText(activity.getString(R.string.duration) + route.getDuration());

        String cat = "";
        for(Transit transit: route.getListTransit())
            cat = cat + "\n \t     > " +transit.getDepartureStop();
        busStops.setText(activity.getString(R.string.stops) + cat);



        return convertView;
    }
}
