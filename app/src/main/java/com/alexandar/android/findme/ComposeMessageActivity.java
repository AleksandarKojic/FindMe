package com.alexandar.android.findme;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import android.os.Bundle;

public class ComposeMessageActivity extends SingleFragmentActivity {

    private static final String EXTRA_LATITUDE_LONGITUDE = "latitude_longitude";

    private String [] latitudeLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null){
            latitudeLongitude = getIntent().getStringArrayExtra(EXTRA_LATITUDE_LONGITUDE);
        }
    }



    public static Intent newIntent(Context packageContext, String latitude, String longitude){
        String [] latitudeLongitude = {latitude, longitude};
        Intent intent = new Intent(packageContext, ComposeMessageActivity.class);
        intent.putExtra(EXTRA_LATITUDE_LONGITUDE, latitudeLongitude );
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new ComposeMessageFragment().newInstance(latitudeLongitude);
    }


}
