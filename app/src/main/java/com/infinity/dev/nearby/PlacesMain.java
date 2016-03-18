package com.infinity.dev.nearby;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

public class PlacesMain extends FragmentActivity{

    Fragment fragAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_main);

        ImageView search = (ImageView)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlacesMain.this, Search.class);
                startActivity(intent);
            }
        });

        fragAll = new PlacesGrid();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.places_grid, fragAll);
        ft.commit();
    }
}
