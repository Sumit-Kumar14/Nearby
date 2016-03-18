package com.infinity.dev.nearby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.infinity.dev.PlaceDetail.PlaceDetailBean;
import com.infinity.dev.Utility.PlaceDetailParser;

import java.util.List;

public class Splash extends Activity {

    public static final int SPLASH_DISPLAY_LENGTH = 8000;
    Typeface robotobold;
    Animation anim;
    ImageView img;
    ImageView img1;
    ImageView img2;
    ImageView img3;
    ImageView img4;
    ImageView img5;
    ImageView img6;
    ImageView img7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        robotobold = Typeface.createFromAsset(this.getAssets(), "Roboto-Bold.ttf");

        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        img = (ImageView)findViewById(R.id.imageView);
        img1 = (ImageView)findViewById(R.id.imageView2);
        img2 = (ImageView)findViewById(R.id.imageView3);
        img3 = (ImageView)findViewById(R.id.imageView4);
        img4 = (ImageView)findViewById(R.id.imageView5);
        img5 = (ImageView)findViewById(R.id.imageView6);
        img6 = (ImageView)findViewById(R.id.imageView7);
        img7 = (ImageView)findViewById(R.id.imageView8);

        TextView nearbyTxt = (TextView)findViewById(R.id.nearbyTxt);
        nearbyTxt.setTypeface(robotobold);
        anim = AnimationUtils.loadAnimation(this, R.anim.anim);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, PlacesMain.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            img.startAnimation(anim);
            img2.startAnimation(anim);
            img3.startAnimation(anim);
            img4.startAnimation(anim);
            img5.startAnimation(anim);
            img6.startAnimation(anim);
            img7.startAnimation(anim);
            img1.startAnimation(anim);
        }
    }
}
