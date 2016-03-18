package com.infinity.dev.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.infinity.dev.PlaceDetail.PlaceDetailBean;
import com.infinity.dev.Utility.PlaceDetailParser;

/**
 * Created by suny on 20/8/15.
 */
public class FetchPlaceDetailInfo extends IntentService {

    static public final int STATUS_RUNNING = 0;
    static public final int STATUS_FINISHED = 1;
    static public final int STATUS_ERROR = 2;

    public FetchPlaceDetailInfo(){
        super("FetchPlaceDetailInfo");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("FetchPlaceDetailInfo", "Starting Service");

        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, bundle.EMPTY);
        try{
            PlaceDetailParser jsonParser = new PlaceDetailParser(url);
            PlaceDetailBean placeDetail = jsonParser.getPlaceDetail();

            if(placeDetail != null) {

                bundle.putSerializable("PlacesDetail", placeDetail);
                receiver.send(STATUS_FINISHED, bundle);
            }else {
                bundle.putSerializable("PlacesDetail", null);
                receiver.send(STATUS_FINISHED, bundle);
            }
        }catch (Exception ex){
            bundle.putString(Intent.EXTRA_TEXT, ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }
}
