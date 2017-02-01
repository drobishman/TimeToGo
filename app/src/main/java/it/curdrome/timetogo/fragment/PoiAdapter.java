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
import it.curdrome.timetogo.model.Poi;

public class PoiAdapter extends ArrayAdapter<Poi> implements Serializable {

    MainActivity activity;

    public PoiAdapter(Context context, int resource, List<Poi> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        activity = mainActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.poi_row, null);

        TextView id = (TextView) convertView.findViewById(R.id.id);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView lat = (TextView) convertView.findViewById(R.id.lat);
        TextView lng = (TextView) convertView.findViewById(R.id.lng);
        TextView description = (TextView) convertView.findViewById(R.id.description);


        final Poi poi = getItem(position);

        id.setText("id: " + poi.getId());
        name.setText("name: " +poi.getName());
        lat.setText("lat: " + poi.getLat());
        lng.setText("lng: " + poi.getLng());
        description.setText("description: " + poi.getDescription());

        return convertView;
    }
}
