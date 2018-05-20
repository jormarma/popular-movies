package com.android.example.popularmovies.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.android.example.popularmovies.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        setSummary(rootKey);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void setSummary(String rootKey) {
        int count = getPreferenceScreen().getPreferenceCount();

        for(int i = 0; i < count; i++) {
            Preference preference = getPreferenceScreen().getPreference(i);
            String key = rootKey != null ? rootKey: getString(R.string.pref_order_key);

            if(preference != null && preference.getKey().equals(key)) {
                ListPreference listPreference = (ListPreference) preference;

                SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
                String defaultValue = getString(R.string.pref_order_popular);
                String value = sharedPreferences.getString(key, defaultValue);
                int indexOfValue = listPreference.findIndexOfValue(value);

                listPreference.setSummary(listPreference.getEntries()[indexOfValue]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_order_key))) {
            setSummary(key);
        }
    }

    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
