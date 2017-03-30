package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

        setLessDetails(convertView, position);
        setMoreDetails(convertView, position);

        return convertView;
    }


    /**
     * Sets more details view components
     * @param convertView
     * @param position
     */
    private void setMoreDetails(View convertView, final int position){


        final Route route = getItem(position);
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);

        TextView departureArrivalMore = (TextView) convertView.findViewById(R.id.departure_arrival_more);
        TextView durationMore = (TextView) convertView.findViewById(R.id.duration_more);
        TextView distanceMore = (TextView) convertView.findViewById(R.id.distance_more);

        ListView transitsList = (ListView) convertView.findViewById(R.id.transits_list);
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

        String time= df.format(Calendar.getInstance().getTime());

        if (route.getDepartureTime()!=null) {
            departureArrivalMore.append(" "+ route.getDepartureTime());
        }else {
            departureArrivalMore.append(" "+time.substring(0,5));
        }

        if (route.getArrivalTime()!=null) {
            departureArrivalMore.append(" - "+ route.getArrivalTime());
        }else{
            departureArrivalMore.append(" - "+setArrivalTime(time, route));
        }

        distanceMore.append(" " + route.getDistance());
        durationMore.append(" " + route.getDuration());
    }


    /**
     * Sets the less details view
     * @param convertView
     * @param position
     */
    private void setLessDetails(View convertView, int position){

        TextView departureArrivalLess = (TextView) convertView.findViewById(R.id.departure_arrival_less);
        TextView distanceLess = (TextView) convertView.findViewById(R.id.distance_less);
        LinearLayout transitImagesLess = (LinearLayout) convertView.findViewById(R.id.transit_images_less);


        final Route route = getItem(position);

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String time= df.format(Calendar.getInstance().getTime());

        if (route.getDepartureTime()!=null) {
            departureArrivalLess.append(" "+ route.getDepartureTime());
        }else {
            departureArrivalLess.append(" "+time.substring(0,5));
        }

        if (route.getArrivalTime()!=null) {
            departureArrivalLess.append(" - "+ route.getArrivalTime());
        }else{
            departureArrivalLess.append(" - "+setArrivalTime(time, route));
        }
        distanceLess.append(" " + route.getDistance());

        for(Transit transit: route.getListTransit()){
            switch(transit.getType()){
                case "BUS":
                    ImageView bus = new ImageView(activity);
                    bus.setImageResource(R.drawable.ic_directions_bus);
                    transitImagesLess.addView(bus);
                    ImageView chevron1 = new ImageView(activity);
                    chevron1.setImageResource(R.drawable.ic_chevron_right);
                    transitImagesLess.addView(chevron1);
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
                        case "MEB":
                            subway.setColorFilter(Color.BLUE);
                            break;
                        case "MEC":
                            subway.setColorFilter(Color.GREEN);
                            break;
                        default:
                            break;

                    }
                    transitImagesLess.addView(subway);
                    ImageView chevron2 = new ImageView(activity);
                    chevron2.setImageResource(R.drawable.ic_chevron_right);
                    transitImagesLess.addView(chevron2);
                    break;
                case "TRAM":
                    ImageView tram = new ImageView(activity);
                    tram.setImageResource(R.drawable.ic_tram);
                    transitImagesLess.addView(tram);
                    ImageView chevron3 = new ImageView(activity);
                    chevron3.setImageResource(R.drawable.ic_chevron_right);
                    transitImagesLess.addView(chevron3);
                    break;
                case "HEAVY_RAIL":
                    ImageView heavyRail = new ImageView(activity);
                    heavyRail.setImageResource(R.drawable.ic_directions_railway);
                    transitImagesLess.addView(heavyRail);
                    ImageView chevron4 = new ImageView(activity);
                    chevron4.setImageResource(R.drawable.ic_chevron_right);
                    transitImagesLess.addView(chevron4);
                    break;
                default:
                    Toast.makeText(activity,"type fail",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        ImageView iv5 = new ImageView(activity);
        iv5.setImageResource(R.drawable.ic_check_box);
        transitImagesLess.addView(iv5);
        TextView tv = new TextView(activity);
        tv.setText("\n");
        transitImagesLess.addView(tv);

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
