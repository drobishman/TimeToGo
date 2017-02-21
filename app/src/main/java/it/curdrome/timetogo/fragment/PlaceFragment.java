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
import it.curdrome.timetogo.model.Place;

/**
 * Fragment containing the infos of the selected place
 *
 * @author adrian
 * @version 1
 */
public class PlaceFragment extends android.support.v4.app.Fragment {

    private List<Place> place = new ArrayList<>();
    private MainActivity activity;
    //private BookmarkFragment adapter;

    /**
     * default constructor
     */
    public PlaceFragment() {
        // Required empty public constructor
    }

    ////salvataggio fragment in Bundle
    //@Override
    //public void onSaveInstanceState(Bundle outState) {
    //    outState.putSerializable("bookmarks", this.adapter);
    //    super.onSaveInstanceState(outState);
    //}

    /**
     * Default method used to get the activity
     * @param savedInstanceState the state of the instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    /**
     * Default method to set the inflater
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return a personalized inflater
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place, container, false);
    }

    /**
     * Default method used to set the List view and to create the adapter
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        place.add(activity.getSelectedMyPlace());

        final ListView mylist = (ListView) view.findViewById(R.id.places);
        final PlaceAdapter adapter = new PlaceAdapter(getActivity().getBaseContext(), R.layout.place_row, place, activity);
        mylist.setAdapter(adapter);

    }
}