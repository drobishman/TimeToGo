package it.curdrome.timetogo.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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

/**
 *
 * Class that defines the adapter for a Place
 *
 * @author Drob Adrian Mihai
 * @version 1
 */

class PlaceAdapter extends ArrayAdapter<Place> implements Serializable {

    // caller activity
    private MainActivity activity;

    /**
     * Default constructor
     * @param context the context of the activity
     * @param resource the reference to the resource
     * @param objects a list cotaining desired data
     * @param mainActivity the caller activity
     */
    PlaceAdapter(Context context, int resource, List<Place> objects, MainActivity mainActivity) {
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
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.place_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView openNow = (TextView) convertView.findViewById(R.id.open_now);
        TextView vicinity =(TextView) convertView.findViewById(R.id.vicinity);


        final Place place = getItem(position);

        name.setText(activity.getString(R.string.name) + place.getName());
        if(place.isOpenHoursEnabled() && place.isOpenNow()) {
            openNow.setText(activity.getString(R.string.now_is_open));
            openNow.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        else if(place.isOpenHoursEnabled() && !place.isOpenNow()){
            openNow.setText(activity.getString(R.string.now_is_close));
            openNow.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light));
        }
        else
            openNow.setText(activity.getString(R.string.open_hours_not_available));
        vicinity.setText(activity.getString(R.string.address) + place.getVicinity());
        return convertView;
    }
}
