package com.infinity.dev.PlaceDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.infinity.dev.nearby.R;

public class AboutFragment extends Fragment{

    private GoogleMap mMap;
    double lat;
    double lng;
    String name;
    FragmentManager fm;
    SupportMapFragment fragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        lat = getArguments().getDouble("Lat");
        lng = getArguments().getDouble("Lng");
        name = getArguments().getString("Name");
        setUpMapIfNeeded();
        return view;
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            fm = getChildFragmentManager();
            fragment = (SupportMapFragment)fm.findFragmentById(R.id.map);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map, fragment).commit();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            mMap = fragment.getMap();
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15.0f));
        }
    }
}
