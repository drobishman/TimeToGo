package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        TextView departure_arrival = (TextView) convertView.findViewById(R.id.departure_arrival);
        TextView distance = (TextView) convertView.findViewById(R.id.distance);
        TextView duration = (TextView) convertView.findViewById(R.id.duration);
        LinearLayout busStops = (LinearLayout) convertView.findViewById(R.id.bus_stops);


        final Route route = getItem(position);

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String time= df.format(Calendar.getInstance().getTime());

        if (route.getDepartureTime()!=null) {
            departure_arrival.append(" "+ route.getDepartureTime());
        }else {
            departure_arrival.append(" "+time.substring(0,5));
        }

        if (route.getArrivalTime()!=null) {
            departure_arrival.append(" - "+ route.getArrivalTime());
        }else{
            departure_arrival.append(" - "+setArrivalTime(time, route));
        }
        distance.append(" " + route.getDistance());
        duration.append(" " + route.getDuration());



        for(Transit transit: route.getListTransit()){
            switch(transit.getType()){
                case "BUS":
                    ImageView bus = new ImageView(activity);
                    bus.setImageResource(R.drawable.ic_directions_bus);
                    busStops.addView(bus);
                    ImageView chevron1 = new ImageView(activity);
                    chevron1.setImageResource(R.drawable.ic_chevron_right);
                    busStops.addView(chevron1);
                    break;
                case "SUBWAY":
                    ImageView subway = new ImageView(activity);
                    subway.setImageResource(R.drawable.ic_subway);

                    switch(transit.getLine()){
                        case "MEA":
                            subway.setColorFilter(Color.RED);
                            break;
                        case "MEB1":
                            subway.setColorFilter(Color.BLUE);
                            break;
                        case "MEB2":
                            subway.setColorFilter(Color.BLUE);
                            break;
                        case "MEC":
                            subway.setColorFilter(Color.GREEN);
                            break;
                        default:
                            break;

                    }
                    busStops.addView(subway);
                    ImageView chevron2 = new ImageView(activity);
                    chevron2.setImageResource(R.drawable.ic_chevron_right);
                    busStops.addView(chevron2);
                    break;
                case "TRAM":
                    ImageView tram = new ImageView(activity);
                    tram.setImageResource(R.drawable.ic_tram);
                    busStops.addView(tram);
                    ImageView chevron3 = new ImageView(activity);
                    chevron3.setImageResource(R.drawable.ic_chevron_right);
                    busStops.addView(chevron3);
                    break;
                case "HEAVY_RAIL":
                    ImageView heavyRail = new ImageView(activity);
                    heavyRail.setImageResource(R.drawable.ic_directions_railway);
                    busStops.addView(heavyRail);
                    ImageView chevron4 = new ImageView(activity);
                    chevron4.setImageResource(R.drawable.ic_chevron_right);
                    busStops.addView(chevron4);
                    break;
                default:
                    Toast.makeText(activity,"type fail",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        ImageView iv5 = new ImageView(activity);
        iv5.setImageResource(R.drawable.ic_check_box);
        busStops.addView(iv5);
        return convertView;
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
