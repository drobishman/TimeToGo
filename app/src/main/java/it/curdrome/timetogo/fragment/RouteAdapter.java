package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

/**
 * Created by adrian on 02/02/2017.
 */

public class RouteAdapter extends ArrayAdapter<Route> implements Serializable {

    MainActivity activity;


    /**
     * Default constructor
     * @param context the context of the activity
     * @param resource the reference to the resource
     * @param objects a list cotaining desired data
     * @param mainActivity the caller activity
     */
    public RouteAdapter(Context context, int resource, List<Route> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        activity = mainActivity;
    }

    /**
     * Method that creates the view
     * @param position where each element must load
     * @param convertView a view containing each row
     * @param parent the parent view
     * @return the converted view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.route_row, null);

        setDetails(convertView, position);

        return convertView;
    }


    /**
     * Sets more details view components
     * @param convertView
     * @param position
     */
    private void setDetails(View convertView, final int position){


        final Route route = getItem(position);
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String time= df.format(Calendar.getInstance().getTime());

        TextView departureArrival = (TextView) convertView.findViewById(R.id.departure_arrival);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);

        if (route.getDepartureTime()!=null) {
            departureArrival.append(route.getDepartureTime());
        }else {
            departureArrival.append(time.substring(0,5));
        }

        if (route.getArrivalTime()!=null) {
            departureArrival.append(" - "+ route.getArrivalTime());
        }else{
            departureArrival.append(" - "+setArrivalTime(time, route));
        }
        distance.append(" " + route.getDistance());
        duration.append(" " + route.getDuration());

        ImageView logoRomaMobilita = (ImageView) convertView.findViewById(R.id.logo_rm);
        logoRomaMobilita.setImageResource(R.drawable.logo_mobilita_roma);
        ImageView logoViaggiaTreno = (ImageView) convertView.findViewById(R.id.logo_vt);
        logoViaggiaTreno.setImageResource(R.drawable.logo_vt);

        final ListView transitsList = (ListView) convertView.findViewById(R.id.transits_list);
        final TransitAdapter adapter = new TransitAdapter(activity.getBaseContext(), R.layout.transit_row, route.getListTransit(), activity);
        transitsList.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(transitsList);

        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                handler.postDelayed( this, 1000 );
            }
        }, 1000 );

    }

    /**
     * Function that calculates the arrival time in case must be calculated
     * @param time current time
     * @param route calculated route
     * @return a string containing the arrival time
     */
    private String setArrivalTime(String time, Route route){
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
            return (" " + Integer.toString(hour) + ":" + Integer.toString(minute));
        }else if(durationstring.length==2){
            minute = minute + Integer.parseInt(durationstring[0]);
            return (" " + Integer.toString(hour) + ":" + Integer.toString(minute));
        }else if(durationstring.length==6){
            Snackbar snackbar = Snackbar
                    .make(activity.findViewById(R.id.main), activity.getString(R.string.error_occured), Snackbar.LENGTH_LONG);
            snackbar.show();

        }

        return "error occured! :-(";
    }
}
