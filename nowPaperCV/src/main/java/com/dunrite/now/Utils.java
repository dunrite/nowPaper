package com.dunrite.now;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.internal.ProtocolConstants;

import java.util.Random;

public class Utils {

	public static final boolean CONNECTION_WIFI = false;
	public static final boolean RANDOM_LOCATION = false;
	public static final boolean FOURK_WALLPAPERS = false;

	protected static boolean isDownloadOnlyOnWifi(Context context) {
		SharedPreferences preferences = getPreferences(context);
		return preferences.getBoolean("network_preference", CONNECTION_WIFI);
	}
	
	protected static boolean isRandom(Context context) {
		SharedPreferences preferences = getPreferences(context);
		return preferences.getBoolean("random_preference", RANDOM_LOCATION);
	}
	
	protected static boolean isFourK(Context context) {
		SharedPreferences preferences = getPreferences(context);
		return preferences.getBoolean("fourk_preference", FOURK_WALLPAPERS);
	}
	
	protected static boolean isWifiConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifi.isConnected();
	}

	protected static String getLocation(Context context) {
		SharedPreferences preferences = getPreferences(context);
		return preferences.getString("location_preference", "Canyon");
	}
	
	protected static void setRandomLocation(Context context){
		Resources res = context.getResources();
		SharedPreferences preferences = getPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		String curr = preferences.getString("location_preference", null);
		int idx = new Random().nextInt(res.getStringArray(R.array.locationValues).length);
		String random = res.getStringArray(R.array.locationValues)[idx];
		
		if(random.equals(curr))
			setRandomLocation(context);
		else{
			editor.putString("location_preference", random);
			editor.apply();
		}		
	}
	
	protected static void setLocation(Context context, String loc){
		SharedPreferences preferences = getPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();	
        editor.putString("location_preference", loc);
		editor.apply();
				
	}
	private static SharedPreferences getPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static void resetRemoved(Context context) {
		if (getLocation(context).equals("Austin") ||
                getLocation(context).equals("Chicago") ||
                getLocation(context).equals("Great Plains") ||
                getLocation(context).equals("London") ||
                getLocation(context).equals("New York") ||
                getLocation(context).equals("Rocky Mountains")) {
			setLocation(context, "Canyon");
            Intent updateIntent = new Intent(context, ArtSource.class);
            updateIntent.setAction(ProtocolConstants.ACTION_HANDLE_COMMAND);
            updateIntent.putExtra(ProtocolConstants.EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
            context.startService(updateIntent);
		}
	}
}
