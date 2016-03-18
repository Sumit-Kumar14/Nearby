package com.infinity.dev.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.infinity.dev.nearby.JSONParser;
import com.infinity.dev.nearby.PlaceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suny on 19/8/15.
 */
public class FetchPlaceInfo extends IntentService {

    static public final int STATUS_RUNNING = 0;
    static public final int STATUS_FINISHED = 1;
    static public final int STATUS_ERROR = 2;

    static final String TAG = "FetchPlaceInfo";

    List<PlaceBean> list = null;

    public FetchPlaceInfo(){
        super("FetchPlaceInfo");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Starting Service");

        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");
        String kind = intent.getStringExtra("kind");

        Bundle bundle = new Bundle();
        receiver.send(STATUS_RUNNING, bundle.EMPTY);
        try{
            JSONParser jsonParser = new JSONParser(url, kind);
            list = jsonParser.getPlaceBeanList();

            if(list != null) {
                ArrayList<PlaceBean> parcel= new ArrayList<>(list.size());
                parcel.addAll(list);
                bundle.putSerializable("PlacesList", parcel);
                receiver.send(STATUS_FINISHED, bundle);
            }else {
                bundle.putSerializable("PlacesList", null);
                receiver.send(STATUS_FINISHED, bundle);
            }
        }catch (Exception ex){
            bundle.putString(Intent.EXTRA_TEXT, ex.toString());
            receiver.send(STATUS_ERROR, bundle);
        }
    }
}
