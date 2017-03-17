package it.curdrome.timetogo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Poi;

/**
 *  A fragment containing the infos of the selected Poi
 *
 *  @author adrian
 *  @version 1
 */
public class PoiFragment extends android.support.v4.app.Fragment {

    private List<Poi> poi = new ArrayList<>();
    private MainActivity activity;
    //private BookmarkFragment adapter;

    public PoiFragment() {
        // Required empty public constructor
    }

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
        return inflater.inflate(R.layout.fragment_poi, container, false);
    }

    /**
     * Default method used to set the List view and to create the adapter
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        poi.add(activity.getSelectedPoi());

        final ListView mylist = (ListView) view.findViewById(R.id.pois);
        final PoiAdapter adapter = new PoiAdapter(getActivity().getBaseContext(), R.layout.poi_row, poi, activity);
        mylist.setAdapter(adapter);

    }
}