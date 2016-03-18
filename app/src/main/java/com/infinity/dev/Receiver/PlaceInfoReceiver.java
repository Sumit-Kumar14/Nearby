package com.infinity.dev.Receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by suny on 19/8/15.
 */
public class PlaceInfoReceiver extends ResultReceiver {

    Receiver mReceiver;
    public PlaceInfoReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
