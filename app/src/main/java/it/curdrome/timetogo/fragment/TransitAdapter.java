package it.curdrome.timetogo.fragment;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Transit;

class TransitAdapter extends ArrayAdapter<Transit> implements Serializable {

    private MainActivity activity;

    TransitAdapter(Context context, int resource, List<Transit> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        activity = mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.transit_row, null);

        TextView numStops = (TextView) convertView.findViewById(R.id.num_stops);
        TextView departureStop = (TextView) convertView.findViewById(R.id.departure_stop);
        TextView headsign = (TextView) convertView.findViewById(R.id.headsign);
        TextView line = (TextView) convertView.findViewById(R.id.line);
        //TextView idPalina = (TextView) convertView.findViewById(R.id.id_palina);
        TextView departureTime = (TextView) convertView.findViewById(R.id. departure_time);


        final Transit transit = getItem(position);

        //TODO resource for multilanguage

        assert transit != null;
        numStops.setText(activity.getString(R.string.stops) + transit.getNumStops());
        departureStop.setText(activity.getString(R.string.stop_name)+ transit.getDepartureStop());
        headsign.setText(activity.getString(R.string.headsign)+transit.getHeadsign());
        //type.setText("type: "+transit.getType());
        line.setText(activity.getString(R.string.line)+transit.getLine()+" ("+transit.getType()+")");
       /* if(transit.getIdPalina() == null){
            idPalina.setText(activity.getString(R.string.no_bus_arriving));
        }else {
            idPalina.setText(activity.getString(R.string.id_palina) + transit.getIdPalina());
        */
        if(transit.getIdPalina() != null){
            departureTime.setText(activity.getString(R.string.RTI) + transit.getDepartureTime() + activity.getString(R.string.real_time_by_roma_mobilità));
            departureTime.setTextColor(activity.getColor(R.color.Green));
        }else {
            departureTime.setText(activity.getString(R.string.departure_scheduled) + transit.getDepartureTime());
        }

        return convertView;
    }
}
