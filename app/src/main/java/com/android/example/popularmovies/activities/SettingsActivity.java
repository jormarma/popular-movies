package com.android.example.popularmovies.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.android.example.popularmovies.R;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(preferences != null) {
            String order = preferences.getString(getString(R.string.pref_order_key), getString(R.string.pref_order_popular));


            // Do things with the
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String orderValue = sharedPreferences.getString(key, getString(R.string.pref_order_popular));

    }
}
