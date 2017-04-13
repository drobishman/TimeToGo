package it.curdrome.timetogo.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Route;
import it.curdrome.timetogo.model.Transit;


/**
 *
 */
public class RouteMiniFragment extends android.support.v4.app.Fragment {
    private Route route;
    private MainActivity activity;
    //private BookmarkFragment adapter;

    public RouteMiniFragment() {
        // Required empty public constructor
    }

    ////salvataggio fragment in Bundle
    //@Override
    //public void onSaveInstanceState(Bundle outState) {
    //    outState.putSerializable("bookmarks", this.adapter);
    //    super.onSaveInstanceState(outState);
    //}

    /**
     * default constructor
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    /**
     * default method to crate the view using a inflater
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return a modified inflater
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_mini, container, false);
    }

    /**
     * Default method used to set the List view and to create the adapter
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        route = activity.getCurrentRoute();

        DateFormat df = new SimpleDateFormat("HH:mm", Locale.ITALY);
        String time= df.format(Calendar.getInstance().getTime());

        TextView departureArrival = (TextView) view.findViewById(R.id.departure_arrival_mini);
        TextView distance = (TextView) view.findViewById(R.id.distance_mini);
        LinearLayout transitImages = (LinearLayout) view.findViewById(R.id.transit_images_mini);

        if (route.getDepartureTime()!=null) {
            departureArrival.append(" "+ route.getDepartureTime());
        }else {
            departureArrival.append(" "+time.substring(0,5));
        }

        if (route.getArrivalTime()!=null) {
            departureArrival.append(" - "+ route.getArrivalTime());
        }else{
            departureArrival.append(" - "+setArrivalTime(time, route));
        }
        distance.append(" " + route.getDistance());

        for(Transit transit: route.getListTransit()){
            switch(transit.getType()){
                case "BUS":
                    ImageView bus = new ImageView(activity);
                    bus.setImageResource(R.drawable.ic_directions_bus);
                    transitImages.addView(bus);
                    ImageView chevron1 = new ImageView(activity);
                    chevron1.setImageResource(R.drawable.ic_chevron_right);
                    transitImages.addView(chevron1);
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
                    transitImages.addView(subway);
                    ImageView chevron2 = new ImageView(activity);
                    chevron2.setImageResource(R.drawable.ic_chevron_right);
                    transitImages.addView(chevron2);
                    break;
                case "TRAM":
                    ImageView tram = new ImageView(activity);
                    tram.setImageResource(R.drawable.ic_tram);
                    transitImages.addView(tram);
                    ImageView chevron3 = new ImageView(activity);
                    chevron3.setImageResource(R.drawable.ic_chevron_right);
                    transitImages.addView(chevron3);
                    break;
                case "HEAVY_RAIL":
                    ImageView heavyRail = new ImageView(activity);
                    heavyRail.setImageResource(R.drawable.ic_directions_railway);
                    transitImages.addView(heavyRail);
                    ImageView chevron4 = new ImageView(activity);
                    chevron4.setImageResource(R.drawable.ic_chevron_right);
                    transitImages.addView(chevron4);
                    break;
                default:
                    Toast.makeText(activity,"type fail",Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        ImageView iv5 = new ImageView(activity);
        iv5.setImageResource(R.drawable.ic_check_box);
        transitImages.addView(iv5);
        TextView tv = new TextView(activity);
        tv.setText("\n");
        transitImages.addView(tv);

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
