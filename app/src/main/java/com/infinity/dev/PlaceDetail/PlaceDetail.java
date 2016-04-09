package com.infinity.dev.PlaceDetail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.infinity.dev.Utility.FetchFromServerTask;
import com.infinity.dev.Utility.FetchFromServerUser;
import com.infinity.dev.Utility.PlaceDetailParser;
import com.infinity.dev.nearby.ErrorFragment;
import com.infinity.dev.nearby.PagerAnimation;
import com.infinity.dev.nearby.R;
import com.infinity.dev.nearby.Search;
import com.infinity.dev.nearby.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetail extends FragmentActivity implements FetchFromServerUser{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout tabs;
    Fragment fragAbout, fragReview, fragGallery, errorFragment;
    ProgressDialog progressDialog;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_detail);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceDetail.this.finish();
            }
        });

        ImageView search = (ImageView)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceDetail.this, Search.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        String placeId = intent.getStringExtra("placeId");

        url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeId+"&key=AIzaSyBg-iwzAjavEUVV9hOQUr0JljZHL7XFRkQ";
        Log.e("PlaceDetail", url);
        new FetchFromServerTask(this, 0).execute(url);
    }

    private void initFragments(){

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSectionsPagerAdapter.addFragment(fragAbout);
        mSectionsPagerAdapter.addFragment(fragReview);
        mSectionsPagerAdapter.addFragment(fragGallery);

        mViewPager = (ViewPager) findViewById(R.id.places_detail);
        mViewPager.setPageTransformer(true, new PagerAnimation());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.yellow);
            }
        });

        tabs.setViewPager(mViewPager);
    }

    @Override
    public void onPreFetch() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onFetchCompletion(String string, int id) {
        if(progressDialog != null)
            progressDialog.dismiss();
        if(errorFragment != null)
            getSupportFragmentManager().beginTransaction().remove(errorFragment).commit();
        if(string == null || string.equals("")){
            errorFragment = new ErrorFragment();
            Bundle msg = new Bundle();
            msg.putString("msg", "No or poor internet connection.");
            errorFragment.setArguments(msg);
            getSupportFragmentManager().beginTransaction().replace(R.id.message, errorFragment).commit();
        }else {
            try {
                PlaceDetailParser jsonParser = new PlaceDetailParser(string);
                PlaceDetailBean detailBean = jsonParser.getPlaceDetail();
                fragAbout = new AboutFragment();
                fragReview = new ReviewFragment();
                fragGallery = new GalleryFragment();

                Bundle data = new Bundle();
                data.putDouble("Lat", detailBean.getLat());
                data.putDouble("Lng", detailBean.getLng());
                data.putString("Name", detailBean.getName());
                fragAbout.setArguments(data);

                Bundle photos = new Bundle();
                photos.putStringArray("photos", detailBean.getPhotos());
                fragGallery.setArguments(photos);

                Bundle reviews = new Bundle();
                reviews.putSerializable("reviews", detailBean.getReviews());
                fragReview.setArguments(reviews);

                initFragments();

                //Initialize About Fragment
                TextView detailName = (TextView) findViewById(R.id.places_detail_name);
                TextView detailIntPhone = (TextView) findViewById(R.id.places_detail_int_phone_detail);
                TextView detailAddress = (TextView) findViewById(R.id.places_detail_address_detail);
                RatingBar rating = (RatingBar) findViewById(R.id.rating);

                detailName.setText(detailBean.getName());
                detailIntPhone.setText(detailBean.getInternational_phone_number());
                detailAddress.setText(detailBean.getFormatted_address());
                rating.setRating(detailBean.getRating());

            }catch (Exception ex){
                errorFragment = new ErrorFragment();
                Bundle msg = new Bundle();
                msg.putString("msg", ex.getMessage());
                errorFragment.setArguments(msg);
                getSupportFragmentManager().beginTransaction().replace(R.id.message, errorFragment).commit();
            }
        }
    }

    public void retry(View view){
        new FetchFromServerTask(this, 0).execute(url);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;

        private String []title = {"ABOUT", "REVIEWS", "GALLERY"};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }
}