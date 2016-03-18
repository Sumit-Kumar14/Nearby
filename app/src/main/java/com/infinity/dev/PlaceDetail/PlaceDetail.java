package com.infinity.dev.PlaceDetail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.infinity.dev.Receiver.PlaceInfoReceiver;
import com.infinity.dev.Services.FetchPlaceDetailInfo;
import com.infinity.dev.Services.FetchPlaceInfo;
import com.infinity.dev.Utility.PlaceAutoComplete;
import com.infinity.dev.Utility.PlaceDetailParser;
import com.infinity.dev.nearby.PagerAnimation;
import com.infinity.dev.nearby.PlacesMain;
import com.infinity.dev.nearby.R;
import com.infinity.dev.nearby.Search;
import com.infinity.dev.nearby.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetail extends FragmentActivity implements PlaceInfoReceiver.Receiver{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    SlidingTabLayout tabs;
    Fragment fragAbout, fragReview, fragGallery;
    ProgressDialog progressDialog;
    private PlaceInfoReceiver mReceiver;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_detail);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceDetail.this, PlacesMain.class);
                startActivity(intent);
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

        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeId+"&key=AIzaSyBg-iwzAjavEUVV9hOQUr0JljZHL7XFRkQ";

        mReceiver = new PlaceInfoReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent placeDetailIntent = new Intent(Intent.ACTION_SYNC, null, this, FetchPlaceDetailInfo.class);


        placeDetailIntent.putExtra("url", url);
        placeDetailIntent.putExtra("receiver", mReceiver);
        placeDetailIntent.putExtra("requestId", 101);

        startService(placeDetailIntent);
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
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case FetchPlaceInfo.STATUS_RUNNING:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                break;
            case FetchPlaceInfo.STATUS_FINISHED:
                progressDialog.dismiss();
                PlaceDetailBean detailBean = (PlaceDetailBean)resultData.get("PlacesDetail");

                fragAbout = new AboutFragment();
                fragReview = new ReviewFragment();
                fragGallery = new GalleryFragment();

                Bundle data = new Bundle();
                data.putDouble("Lat", detailBean.getLat());
                data.putDouble("Lng", detailBean.getLng());
                data.putString("Name", detailBean.getName());
                fragAbout.setArguments(data);

                initFragments();

                PlaceDetailBean.Review []reviewsArray = detailBean.getReviews();
                int r1 = 0,r2 = 0, r3 = 0, r4 = 0, r5 = 0;
                if(reviewsArray != null && reviewsArray.length > 0) {
                    for (PlaceDetailBean.Review r : reviewsArray) {
                        if (Math.floor(r.getRating()) == 1)
                            r1++;
                        else if (Math.floor(r.getRating()) == 2)
                            r2++;
                        else if (Math.floor(r.getRating()) == 3)
                            r3++;
                        else if (Math.floor(r.getRating()) == 4)
                            r4++;
                        else if (Math.floor(r.getRating()) == 5)
                            r5++;
                    }
                }

                //Initialize About Fragment
                TextView detailName = (TextView)findViewById(R.id.places_detail_name);
                TextView detailPhone = (TextView)findViewById(R.id.places_detail_phone_detail);
                TextView detailIntPhone = (TextView)findViewById(R.id.places_detail_int_phone_detail);
                TextView rating1 = (TextView)findViewById(R.id.rating_1);
                TextView rating2 = (TextView)findViewById(R.id.rating_2);
                TextView rating3 = (TextView)findViewById(R.id.rating_3);
                TextView rating4 = (TextView)findViewById(R.id.rating_4);
                TextView rating5 = (TextView)findViewById(R.id.rating_5);
                TextView rating1t = (TextView)findViewById(R.id.rating_1t);
                TextView rating2t = (TextView)findViewById(R.id.rating_2t);
                TextView rating3t = (TextView)findViewById(R.id.rating_3t);
                TextView rating4t = (TextView)findViewById(R.id.rating_4t);
                TextView rating5t = (TextView)findViewById(R.id.rating_5t);

                ViewGroup.LayoutParams params1 = rating1.getLayoutParams();
                ViewGroup.LayoutParams params2 = rating2.getLayoutParams();
                ViewGroup.LayoutParams params3 = rating3.getLayoutParams();
                ViewGroup.LayoutParams params4 = rating4.getLayoutParams();
                ViewGroup.LayoutParams params5 = rating5.getLayoutParams();

                final float scale = context.getResources().getDisplayMetrics().density;
                int pixels = (int) (320 * scale + 0.5f);
                if((r1+r2+r3+r4+r5) > 0) {
                    float res1 = (float)r1 / (r1 + r2 + r3 + r4 + r5);
                    float res2 = (float)r2 / (r2 + r2 + r3 + r4 + r5);
                    float res3 = (float)r3 / (r3 + r2 + r3 + r4 + r5);
                    float res4 = (float)r4 / (r4 + r2 + r3 + r4 + r5);
                    float res5 = (float)r5 / (r5 + r2 + r3 + r4 + r5);

                    params1.width = (int)(pixels * res1);
                    rating1.setLayoutParams(params1);
                    params2.width = (int)(pixels * res2);
                    rating2.setLayoutParams(params2);
                    params3.width = (int)(pixels * res3);
                    rating3.setLayoutParams(params3);
                    params4.width = (int)(pixels * res4);
                    rating4.setLayoutParams(params4);
                    params5.width = (int)(pixels * res5);
                    rating5.setLayoutParams(params5);

                }else {
                    params1.width = 0;
                    rating1.setLayoutParams(params1);

                    params2.width = 0;
                    rating2.setLayoutParams(params2);

                    params3.width = 0;
                    rating3.setLayoutParams(params3);

                    params4.width = 0;
                    rating4.setLayoutParams(params4);

                    params5.width = 0;
                    rating5.setLayoutParams(params5);
                }
                rating1t.setText(""+r1);
                rating2t.setText(""+r2);
                rating3t.setText(""+r3);
                rating4t.setText(""+r4);
                rating5t.setText(""+r5);

                TextView detailAddress = (TextView)findViewById(R.id.places_detail_address_detail);

                detailName.setText(detailBean.getName());
                detailPhone.setText(detailBean.getFormatted_phone_number());
                detailIntPhone.setText(detailBean.getInternational_phone_number());
                detailAddress.setText(detailBean.getFormatted_address());

                //Initialize Review Fragment
                ListView reviews = (ListView)findViewById(R.id.review_list);
                if(reviewsArray != null && reviewsArray.length > 0) {
                    ReviewAdapter reviewsAdapter = new ReviewAdapter(reviewsArray, this);
                    reviews.setAdapter(reviewsAdapter);
                }else{
                    TextView no_Review = (TextView)findViewById(R.id.no_reviews);
                    no_Review.setText("Sorry, no reviews available for this place");
                }

                //Initialize Gallery Fragment
                GridView gridView = (GridView)findViewById(R.id.placesImage);
                String []photosArray = detailBean.getPhotos();
                if(photosArray != null && photosArray.length > 0) {
                    gridView.setAdapter(new PlaceImages(this, photosArray));
                }else{
                    TextView no_Image = (TextView)findViewById(R.id.no_images);
                    no_Image.setText("Sorry, no images available for this place");
                }
                break;
            case FetchPlaceInfo.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.e("PlaceResult", error);
                Toast.makeText(this, "Unable to fetch data", Toast.LENGTH_LONG).show();
                break;
        }
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
