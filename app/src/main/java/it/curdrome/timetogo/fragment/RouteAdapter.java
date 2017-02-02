package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Category;
import it.curdrome.timetogo.model.Poi;
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

        arrival.setText(activity.getString(R.string.arrival_time) + route.getArrivalTime());
        departure.setText(activity.getString(R.string.departure_time) + route.getDepartureTime());
        distance.setText(activity.getString(R.string.distance) + route.getDistance());
        duration.setText(activity.getString(R.string.duration) + route.getDuration());

        String cat = "";
        for(Transit transit: route.getListTransit())
            cat = cat + " " +transit.getDepartureStop();
        busStops.setText(activity.getString(R.string.stops) + cat);



        return convertView;
    }
}
