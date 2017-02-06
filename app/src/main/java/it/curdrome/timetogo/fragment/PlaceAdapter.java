package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Place;

class PlaceAdapter extends ArrayAdapter<Place> implements Serializable {

    private MainActivity activity;

    PlaceAdapter(Context context, int resource, List<Place> objects, MainActivity mainActivity) {
        super(context, resource, objects);
        activity = mainActivity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.place_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView categories = (TextView) convertView.findViewById(R.id.categories);
        TextView openNow = (TextView) convertView.findViewById(R.id.open_now);
        TextView vicinity =(TextView) convertView.findViewById(R.id.vicinity);


        final Place place = getItem(position);

        name.setText(activity.getString(R.string.name) + place.getName());
        if(place.isOpenHoursEnabled() && place.isOpenNow())
            openNow.setText(activity.getString(R.string.now_is_open));
        else if(place.isOpenHoursEnabled() && !place.isOpenNow())
            openNow.setText(activity.getString(R.string.now_is_close));
        else
        openNow.setText(activity.getString(R.string.open_hours_not_available));
        vicinity.setText(activity.getString(R.string.address) + place.getVicinity());
        return convertView;
    }
}
