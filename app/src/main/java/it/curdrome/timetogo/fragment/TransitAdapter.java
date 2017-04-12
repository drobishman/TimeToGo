package it.curdrome.timetogo.fragment;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.connection.atac.RTIAsyncTask;
import it.curdrome.timetogo.connection.viaggiatreno.AndamentoTrenoAsyncTask;
import it.curdrome.timetogo.model.Transit;

class TransitAdapter extends ArrayAdapter<Transit> implements Serializable {

    private MainActivity activity;

    /**
     * Default constructor
     * @param context the context of the activity
     * @param resource the reference to the resource
     * @param objects a list cotaining desired data
     * @param mainActivity the caller activity
     */
    TransitAdapter(Context context, int resource, List<Transit> objects, MainActivity mainActivity) {
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
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.transit_row, null);

        TextView numStops = (TextView) convertView.findViewById(R.id.num_stops);
        LinearLayout line = (LinearLayout) convertView.findViewById(R.id.line);
        TextView headsign = (TextView) convertView.findViewById(R.id.headsign);
        TextView departureStop = (TextView) convertView.findViewById(R.id.departure_stop);
        TextView departureTime = (TextView) convertView.findViewById(R.id. departure_time);
        ImageButton refreshButton = (ImageButton) convertView.findViewById(R.id.refresh_button);


        final Transit transit = getItem(position);

        assert transit != null;
        numStops.append(" " + transit.getNumStops());
        //departureStop.append(" "+ transit.getDepartureStop());

        switch (transit.getType()){
            case "BUS":
                ImageView bus = new ImageView(activity);
                bus.setImageResource(R.drawable.ic_directions_bus);
                line.addView(bus);
                TextView tv1 = new TextView(activity);
                tv1.setText(transit.getLine());
                tv1.setTextSize(21);
                line.addView(tv1);
                break;
            case "SUBWAY":
                ImageView subway = new ImageView(activity);
                subway.setImageResource(R.drawable.ic_subway);

                switch(transit.getLine()){
                    case "MEA":
                        subway.setColorFilter(Color.RED);
                        break;
                    case "MEB1":
                        subway.setColorFilter(Color.BLUE);
                        break;
                    case "MEB2":
                        subway.setColorFilter(Color.BLUE);
                        break;
                    case "MEB":
                        subway.setColorFilter(Color.BLUE);
                        break;
                    case "MEC":
                        subway.setColorFilter(Color.GREEN);
                        break;
                    default:
                        break;

                }
                line.addView(subway);
                TextView tv2 = new TextView(activity);
                tv2.setText(transit.getLine());
                tv2.setTextSize(21);
                line.addView(tv2);
                break;
            case "TRAM":
                ImageView tram = new ImageView(activity);
                tram.setImageResource(R.drawable.ic_tram);
                line.addView(tram);
                TextView tv3 = new TextView(activity);
                tv3.setText(transit.getLine());
                tv3.setTextSize(21);
                line.addView(tv3);
                break;
            case "HEAVY_RAIL":
                ImageView heavyRail = new ImageView(activity);
                heavyRail.setImageResource(R.drawable.ic_directions_railway);
                line.addView(heavyRail);
                TextView tv4 = new TextView(activity);
                tv4.setText(transit.getLine());
                tv4.setTextSize(21);
                line.addView(tv4);
                break;
            default:
                Toast.makeText(activity,"Fail",Toast.LENGTH_SHORT).show();
                break;
        }

        headsign.append(" "+transit.getHeadsign());
        if(transit.getHeadsign().length()>25){
            headsign.setTextSize(12);
        }

        departureStop.append(" "+transit.getDepartureStop());
        if(transit.getDepartureStop().length()>25){
            headsign.setTextSize(12);
        }

        if(transit.getIdPalina() != null){
            departureTime.setText(" "+transit.getDepartureTime());
            departureTime.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        }else {
            departureTime.setText(activity.getString(R.string.departure_scheduled) + " "+transit.getDepartureTime());
        }
        if(transit.getCodLocOrig() != null && transit.getIdTrain()!= null) {
            departureTime.append(" " + transit.getDelay() + activity.getString(R.string.delay) );
            departureTime.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }


        if((transit.getType().matches("HEAVY_RAIL") &&
                (transit.getCodLocOrig() == null && transit.getIdTrain() == null))
                || transit.getType().matches("SUBWAY")) {
            refreshButton.setImageResource(R.drawable.ic_sync_disabled);
            refreshButton.setColorFilter(R.color.SecondaryText);
            refreshButton.setEnabled(false);
        } else {
            refreshButton.setImageResource(R.drawable.ic_sync_enabled);
            refreshButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(transit.getIdPalina()!=null) {
                    RTIAsyncTask rtiAsyncTask = new RTIAsyncTask(transit);
                    rtiAsyncTask.execute();
                }else if (transit.getType().matches("BUS") ||  transit.getType().matches("TRAM")) {
                    transit.refreshIDPalina();
                }
                if (transit.getIdTrain() != null && transit.getCodLocOrig() != null) {
                    AndamentoTrenoAsyncTask andamentoTrenoAsyncTask = new AndamentoTrenoAsyncTask(transit);
                    andamentoTrenoAsyncTask.execute();
                }
            }
        });

        return convertView;
    }
}
