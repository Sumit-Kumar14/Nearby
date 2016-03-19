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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

                initFragments();

                PlaceDetailBean.Review[] reviewsArray = detailBean.getReviews();

                //Initialize About Fragment
                TextView detailName = (TextView) findViewById(R.id.places_detail_name);
                TextView detailIntPhone = (TextView) findViewById(R.id.places_detail_int_phone_detail);
                TextView detailAddress = (TextView) findViewById(R.id.places_detail_address_detail);
                RatingBar rating = (RatingBar) findViewById(R.id.rating);

                detailName.setText(detailBean.getName());
                detailIntPhone.setText(detailBean.getInternational_phone_number());
                detailAddress.setText(detailBean.getFormatted_address());
                rating.setRating(detailBean.getRating());

                //Initialize Review Fragment
                ListView reviews = (ListView) findViewById(R.id.review_list);
                if (reviewsArray != null && reviewsArray.length > 0) {
                    ReviewAdapter reviewsAdapter = new ReviewAdapter(reviewsArray, this);
                    reviews.setAdapter(reviewsAdapter);
                } else {
                    TextView no_Review = (TextView) findViewById(R.id.no_reviews);
                    no_Review.setText("Sorry, no reviews available for this place");
                }

                //Initialize Gallery Fragment
                GridView gridView = (GridView) findViewById(R.id.placesImage);
                String[] photosArray = detailBean.getPhotos();
                if (photosArray != null && photosArray.length > 0) {
                    gridView.setAdapter(new PlaceImages(this, photosArray));
                } else {
                    TextView no_Image = (TextView) findViewById(R.id.no_images);
                    no_Image.setText("Sorry, no images available for this place");
                }
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