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
import it.curdrome.timetogo.model.Route;

/**
 *  A fragment containing the infos of the selected Route
 *
 *  @author adrian
 *  @version 1
 */
public class RouteFragment extends android.support.v4.app.Fragment {

    private List<Route> route = new ArrayList<>();
    private MainActivity activity;
    //private BookmarkFragment adapter;

    public RouteFragment() {
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
        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    /**
     * Default method used to set the List view and to create the adapter
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        route.add(activity.getCurrentRoute());

        final ListView mylist = (ListView) view.findViewById(R.id.routes);
        final RouteAdapter adapter = new RouteAdapter(getActivity().getBaseContext(), R.layout.route_row, route, activity);
        mylist.setAdapter(adapter);

    }
}