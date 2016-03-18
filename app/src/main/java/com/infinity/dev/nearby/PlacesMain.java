package com.infinity.dev.nearby;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.infinity.dev.PlaceDetail.PlaceDetail;
import com.infinity.dev.Utility.PlaceAutoComplete;

/**
 * Created by suny on 8/8/15.
 */
public class PlacesMain extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener{

    Fragment fragAll;
    private Context context = this;

    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoComplete mAdapter;
    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUNDS_WORLD = new LatLngBounds(new LatLng(0.0, 0.0), new LatLng(0.0, 0.0));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_main);

        fragAll = new PlacesGrid();

        /*mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, 0, this).addApi(Places.GEO_DATA_API).build();

        mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete_places);
        mAutocompleteView.clearFocus();
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutoComplete(this, R.layout.auto_complete_list_item, mGoogleApiClient, BOUNDS_WORLD, null);
        mAutocompleteView.setAdapter(mAdapter);
        */
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.places_grid, fragAll);
        ft.commit();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAutoComplete.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            Intent intent = new Intent(context, PlaceDetail.class);
            intent.putExtra("placeId", placeId);
            startActivity(intent);
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("PlaceDetail", "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }
}
