package com.dunrite.now;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.internal.ProtocolConstants;

public class SettingsActivity extends AppCompatActivity{
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsActivity.context = getApplicationContext();
        setContentView(R.layout.settings_activity);
        getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ablogo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Muzei Settings");
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Context getAppContext() {
        return SettingsActivity.context;
    }

    public static class MyPreferenceFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final String LOCATION_PREFERENCE = "location_preference";
        private static final String FOURK_PREFERENCE = "fourk_preference";

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            this.addPreferencesFromResource(R.xml.settings);
            PreferenceManager.getDefaultSharedPreferences(SettingsActivity.getAppContext()).registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {

            if (LOCATION_PREFERENCE.equals(key) || FOURK_PREFERENCE.equals(key)) {
                Intent updateIntent = new Intent(SettingsActivity.getAppContext(), ArtSource.class);
                updateIntent.setAction(ProtocolConstants.ACTION_HANDLE_COMMAND);
                updateIntent.putExtra(ProtocolConstants.EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
                SettingsActivity.getAppContext().startService(updateIntent);
            }
        }
    }
}