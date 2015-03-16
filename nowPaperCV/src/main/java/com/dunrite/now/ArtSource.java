package com.dunrite.now;

import java.util.Calendar;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import android.content.Intent;
import android.net.Uri;

public class ArtSource extends RemoteMuzeiArtSource {
	private static final String SOURCE_NAME = "ArtSource";
	private static final String URL = "http://dunrite.dinoscript.com/nowPaper/muzei/";
	private static final int MILLIS_AN_HOUR = 3600000;
	private static final int MILLIS_A_MIN = 60000;
	Boolean randomized = false;

	public ArtSource() {
		super(SOURCE_NAME);
	}

	@Override
	protected void onTryUpdate(int reason) throws RetryException {

		String setting = new String(); // dawn,day,dusk,night
		String imageURL = new String();
		
		
		Calendar today = Calendar.getInstance();

		int hour = today.get(Calendar.HOUR_OF_DAY);
		int minute = today.get(Calendar.MINUTE);
		long updateTime = 0;

		unscheduleUpdate();
		
		if (!(Utils.isDownloadOnlyOnWifi(this) && !Utils.isWifiConnected(this))) { //does this as long as wifi settings are correct

			if (hour >= 6 && hour < 9) {
				if(Utils.isRandom(this) && randomized != true){//Set a Random location
					Utils.setRandomLocation(this);
					randomized = true; //so it doesn't get caught in a loop
				}			
				setting = "dawn";
				updateTime = System.currentTimeMillis() + ((9 - hour - 1) * MILLIS_AN_HOUR) + ((60 - minute) * MILLIS_A_MIN);
			} else if (hour >= 9 && hour < 18) {
				randomized = false;
				setting = "day";
				updateTime = System.currentTimeMillis() + ((18 - hour - 1) * MILLIS_AN_HOUR) + ((60 - minute) * MILLIS_A_MIN);
			} else if (hour >= 18 && hour < 21) {
				randomized = false;
				setting = "dusk";
				updateTime = System.currentTimeMillis() + ((21 - hour - 1) * MILLIS_AN_HOUR) + ((60 - minute) * MILLIS_A_MIN);
			} else if (hour >= 21) {
				randomized = false;
				setting = "night";
				updateTime = System.currentTimeMillis() + ((30 - hour - 1) * MILLIS_AN_HOUR) + ((60 - minute) * MILLIS_A_MIN); // 30=24+6
			} else if (hour < 6) {
				randomized = false;
				setting = "night";
				updateTime = System.currentTimeMillis() + ((6 - hour - 1) * MILLIS_AN_HOUR) + ((60 - minute) * MILLIS_A_MIN);
			}	
			
			String location = Utils.getLocation(this);
			String locationS = Utils.getLocation(this); // to preserve space for
														// title
		
			// get rid of space in name
			if (location.contains(" ")) {
				String[] parts = location.split(" ");
				String part1 = parts[0];
				String part2 = parts[1];
				location = part1 + part2;
			}
			if(Utils.isFourK(this))
				imageURL = URL + location + "/4k/" + setting + ".png";
			else
				imageURL = URL + location + "/" + setting + ".png";
			
			publishArtwork(new Artwork.Builder()
					.imageUri(
							Uri.parse(imageURL))
					.title(locationS + " (" + setting + ")")
					.byline("Alex Pasquarella, c. 2015")
					.viewIntent(
							new Intent(Intent.ACTION_VIEW, Uri
									.parse("http://alexpasquarella.com")))
					.build());

			scheduleUpdate(updateTime);
		}else{
			scheduleUpdate(System.currentTimeMillis() + (2*MILLIS_AN_HOUR));
		}
	}
}
