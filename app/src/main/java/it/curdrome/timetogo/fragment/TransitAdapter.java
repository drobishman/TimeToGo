package it.curdrome.timetogo.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import it.curdrome.timetogo.R;
import it.curdrome.timetogo.activity.MainActivity;
import it.curdrome.timetogo.connection.atac.RTIAsyncTask;
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
    @RequiresApi(api = Build.VERSION_CODES.M)
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
        ImageView imageDivider = (ImageView) convertView.findViewById(R.id.image_divider);


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

        departureStop.append(transit.getDepartureStop());
        if(transit.getIdPalina() != null){
            departureTime.setText(transit.getDepartureTime());
            departureTime.setTextColor(activity.getColor(R.color.colorAccent));
            LinearLayout rtiLogo = (LinearLayout) convertView.findViewById(R.id.rti_logo);
            TextView rtiText = new TextView(activity);
            rtiText.setText(activity.getText(R.string.real_time_by_roma_mobilità)+"  ");
            rtiText.setTextColor(activity.getColor(R.color.colorAccent));
            rtiLogo.addView(rtiText);
            ImageView rtiImage = new ImageView(activity);
            rtiImage.setImageResource(R.drawable.bus_rt);
            rtiImage.setColorFilter(activity.getColor(R.color.colorAccent));
            rtiLogo.addView(rtiImage);

        }else {
            departureTime.setText(activity.getString(R.string.departure_scheduled) + transit.getDepartureTime());
        }

        imageDivider.setImageResource(R.drawable.down_arrows);
        imageDivider.setColorFilter(activity.getColor(R.color.colorPrimary));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(transit.getIdPalina()!=null) {
                    RTIAsyncTask rtiAsyncTask = new RTIAsyncTask(transit);
                    rtiAsyncTask.execute();
                }
            }
        });

        return convertView;
    }
}
