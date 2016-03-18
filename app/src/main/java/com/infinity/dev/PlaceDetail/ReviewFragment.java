package com.infinity.dev.PlaceDetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.infinity.dev.nearby.R;

/**
 * Created by suny on 16/8/15.
 */
public class ReviewFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment, container, false);
        return view;
    }
}
