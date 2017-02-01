package it.curdrome.timetogo.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

/**
 * Created by adrian on 31/01/2017.
 */

public class Place implements com.google.android.gms.location.places.Place {

    private String id;


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public List<Integer> getPlaceTypes() {
        return null;
    }

    @Override
    public CharSequence getAddress() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public CharSequence getName() {
        return null;
    }

    @Override
    public LatLng getLatLng() {
        return null;
    }

    @Override
    public LatLngBounds getViewport() {
        return null;
    }

    @Override
    public Uri getWebsiteUri() {
        return null;
    }

    @Override
    public CharSequence getPhoneNumber() {
        return null;
    }

    @Override
    public float getRating() {
        return 0;
    }

    @Override
    public int getPriceLevel() {
        return 0;
    }

    @Override
    public CharSequence getAttributions() {
        return null;
    }

    @Override
    public com.google.android.gms.location.places.Place freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}