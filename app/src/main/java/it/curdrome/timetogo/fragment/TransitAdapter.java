package it.curdrome.timetogo.fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Transit;

public class TransitAdapter extends ArrayAdapter<Transit> implements Serializable {

    MainActivity activity;

    public TransitAdapter(Context context, int resource, List<Transit> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        activity = mainActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.transit_row, null);

        TextView numStops = (TextView) convertView.findViewById(R.id.num_stops);
        TextView departureStop = (TextView) convertView.findViewById(R.id.departure_stop);
        TextView headsign = (TextView) convertView.findViewById(R.id.headsign);
        TextView type = (TextView) convertView.findViewById(R.id.type);
        TextView line = (TextView) convertView.findViewById(R.id.line);
        TextView idPalina = (TextView) convertView.findViewById(R.id.id_palina);
        TextView departureTime = (TextView) convertView.findViewById(R.id. departure_time);


        final Transit transit = getItem(position);

        numStops.setText("num stops: " + transit.getNumStops());
        departureStop.setText(" stop name: " + transit.getDepartureStop());
        headsign.setText("headsign: "+transit.getHeadsign());
        type.setText("type: "+transit.getType());
        line.setText("line: "+transit.getLine());
        idPalina.setText("id palina: "+transit.getIdPalina());
        departureTime.setText("departure time: "+transit.getDepartureTime());

        return convertView;
    }
}
