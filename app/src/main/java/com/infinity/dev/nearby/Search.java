package com.infinity.dev.nearby;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;

import com.infinity.dev.PlaceDetail.PlaceDetail;
import com.infinity.dev.Utility.FetchFromServerTask;
import com.infinity.dev.Utility.FetchFromServerUser;
import com.infinity.dev.Utility.GooglePlacesBean;
import com.infinity.dev.Utility.GooglePlacesParser;
import com.infinity.dev.Utility.Locatable;
import com.infinity.dev.Utility.Locator;

import java.util.ArrayList;
import java.util.List;

public class Search extends FragmentActivity implements FetchFromServerUser, Locatable{

    private AutoCompleteTextView mAutocompleteView;

    public static final String GOOGLE_PLACES_URL = "maps.googleapis.com/maps/api/place/autocomplete/json";
    public static final int SEARCH_RADIUS = 1000;
    public static final String PLACES_API_KEY = "AIzaSyBg-iwzAjavEUVV9hOQUr0JljZHL7XFRkQ";

    List<SearchItemBean> results = new ArrayList<>();
    SearchResultAdapter resultAdapter;
    SearchItemBean search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.this.finish();
            }
        });

        mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.places_autocomplete);
        FloatingActionButton getDirection = (FloatingActionButton)findViewById(R.id.getDirection);
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search != null){
                    Intent detailActivity = new Intent(Search.this, PlaceDetail.class);
                    detailActivity.putExtra("placeId", search.getPlaceID());
                    startActivity(detailActivity);
                }
            }
        });
        new Locator(this, this).execute();
    }

    @Override
    public void onPreFetch() {

    }

    @Override
    public void onFetchCompletion(String string, int id) {
        if(string != null && !string.equals("")) {
            GooglePlacesParser parser = new GooglePlacesParser(string);
            ArrayList<GooglePlacesBean> placesList = parser.getPlaces();
            for (int i = 0; i < placesList.size(); i++) {
                SearchItemBean bean = new SearchItemBean();
                bean.setName(placesList.get(i).getDescription());
                bean.setPlaceID(placesList.get(i).getPlaceId());
                bean.setType("Google");
                results.add(bean);
            }
            resultAdapter.notifyDataSetChanged();
            ListView resultList = (ListView) findViewById(R.id.searchResult);
            resultList.setAdapter(resultAdapter);
            resultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    search = results.get(position);
                    mAutocompleteView.setText(search.getName());
                }
            });
        }
    }

    @Override
    public void onLocationComplete(final Location location) {
        resultAdapter = new SearchResultAdapter(this, results);
        mAutocompleteView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                results.clear();
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .encodedAuthority(GOOGLE_PLACES_URL)
                        .appendQueryParameter("input", s.toString())
                        .appendQueryParameter("location", location.getLatitude() + "," + location.getLongitude())
                        .appendQueryParameter("radius", String.valueOf(SEARCH_RADIUS))
                        .appendQueryParameter("key", PLACES_API_KEY);

                String url = builder.build().toString();
                Log.e("URL", url);
                new FetchFromServerTask(Search.this, 0).execute(url);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}