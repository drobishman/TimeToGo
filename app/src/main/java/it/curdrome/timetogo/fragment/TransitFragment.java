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
import it.curdrome.timetogo.model.Transit;

/**
 *  A fragment containing the infos of the selected Transit
 *
 *  @author adrian
 *  @version 1
 */

public class TransitFragment extends android.support.v4.app.Fragment {

    private List<Transit> transit = new ArrayList<>();
    private MainActivity activity;
    //private BookmarkFragment adapter;

    public TransitFragment() {
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
        return inflater.inflate(R.layout.fragment_transit, container, false);
    }

    /**
     * Default method used to set the List view and to create the adapter
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transit.add(activity.getSelectedTransit());

        final ListView mylist = (ListView) view.findViewById(R.id.transits);
        final TransitAdapter adapter = new TransitAdapter(getActivity().getBaseContext(), R.layout.transit_row, transit, activity);
        mylist.setAdapter(adapter);

    }
}