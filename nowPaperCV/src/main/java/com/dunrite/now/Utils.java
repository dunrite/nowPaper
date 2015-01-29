package com.dunrite.now;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

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
		String location = preferences.getString("location_preference", "Canyon");
		return location;
	}
	
	protected static void setRandomLocation(Context context){
		Resources res = context.getResources();
		SharedPreferences preferences = getPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		String curr = preferences.getString("location_preference", null);
		int idx = new Random().nextInt(res.getStringArray(R.array.locationValues).length);
		String random = (String) (res.getStringArray(R.array.locationValues)[idx]);
		
		if(random.equals(curr))
			setRandomLocation(context);
		else{
			editor.putString("location_preference", random);
			editor.commit();
		}		
	}
	
	protected static void setLocation(Context context, String loc){
		SharedPreferences preferences = getPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();	
        editor.putString("location_preference", loc);
		editor.commit();
				
	}
	private static SharedPreferences getPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

}
