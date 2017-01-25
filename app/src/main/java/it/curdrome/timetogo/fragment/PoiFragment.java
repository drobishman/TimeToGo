package it.curdrome.timetogo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.model.Poi;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PoiFragment extends android.support.v4.app.Fragment {

    private MainActivity activity;
    //private BookmarkFragment adapter;

    public PoiFragment() {
        // Required empty public constructor
    }

    ////salvataggio fragment in Bundle
    //@Override
    //public void onSaveInstanceState(Bundle outState) {
    //    outState.putSerializable("bookmarks", this.adapter);
    //    super.onSaveInstanceState(outState);
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poi, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Poi> pois = activity.getPois();

        final ListView mylist = (ListView) view.findViewById(R.id.pois);
        final PoiAdapter adapter = new PoiAdapter(getActivity().getBaseContext(), R.layout.poi_row, pois, activity);
        mylist.setAdapter(adapter);

    }
}