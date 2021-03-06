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
import it.curdrome.timetogo.model.Poi;

class PoiAdapter extends ArrayAdapter<Poi> implements Serializable {

    private MainActivity activity;

    /**
     * Default constructor
     * @param context the context of the activity
     * @param resource the reference to the resource
     * @param objects a list cotaining desired data
     * @param mainActivity the caller activity
     */
    PoiAdapter(Context context, int resource, List<Poi> objects, MainActivity mainActivity) {
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
        convertView = inflater.inflate(R.layout.poi_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView description = (TextView) convertView.findViewById(R.id.description);


        final Poi poi = getItem(position);

        name.setText(activity.getString(R.string.name) + poi.getName());
        if (!poi.getDescription().isEmpty())
            description.setText(activity.getString(R.string.description) + poi.getDescription());

        return convertView;
    }
}
