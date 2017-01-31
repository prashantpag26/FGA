package com.allnew.facebookgraphapi;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class SimpleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        if (getIntent().getExtras() != null) {
            callNewWeb(getIntent().getStringExtra("text"));
        }

    }

    public void callNewWeb(final String url) {
        Uri uri = Uri.parse(url);

// create an intent builder
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

// Begin customizing
// set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

// set start and exit animations
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

// build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();

// launch the url
        customTabsIntent.launchUrl(this, uri);
    }

}
