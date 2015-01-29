package com.dunrite.now;

import com.dunrite.now.SystemBarTintManager.SystemBarConfig;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.internal.ProtocolConstants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;


public class SettingsActivity extends PreferenceActivity
	implements SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String LOCATION_PREFERENCE = "location_preference";
	private static final String FOURK_PREFERENCE = "fourk_preference";
	private static SystemBarTintManager tintManager;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
			
        super.onCreate(savedInstanceState);       
        this.addPreferencesFromResource(R.xml.settings);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //Color the action bar white 
        getActionBar().setTitle("MUZEI SETTINGS");
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {  //set transparency if has kitkat
        	//----KITKAT TRANSPARENCY STUFF----------------------------------
            Window w = getWindow(); // in Activity's onCreate() for instance;
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, //Set the Nav Bar to translucent
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,      //Set the Status Bar to translucent
                     WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //----COLOR TINTING STUFF----------------------------------------
               // create our manager instance after the content view is set
               tintManager = new SystemBarTintManager(this);
               // enable status bar tint
               tintManager.setStatusBarTintEnabled(true);
               // enable navigation bar tint
               tintManager.setNavigationBarTintEnabled(false);
               tintManager.setTintColor(Color.parseColor("#FFFFFF"));
               ListView listView = getListView();
               SystemBarConfig config = tintManager.getConfig();
               listView.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom());
        }
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (LOCATION_PREFERENCE.equals(key) || FOURK_PREFERENCE.equals(key)){
			Intent updateIntent = new Intent(SettingsActivity.this, ArtSource.class);
	        updateIntent.setAction(ProtocolConstants.ACTION_HANDLE_COMMAND);
	        updateIntent.putExtra(ProtocolConstants.EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
	        startService(updateIntent);
		}
	}

}