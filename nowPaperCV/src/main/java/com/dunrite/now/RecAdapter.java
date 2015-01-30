package com.dunrite.now;

import java.io.File;
import java.io.InputStream;

import com.bumptech.glide.Glide;

import java.net.URL;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {
	private static String[] mDataset;
	private String URL = "http://dunrite.dinoscript.com/nowPaper/thumbs/";
	private static String URL2 = "http://dunrite.dinoscript.com/nowPaper/muzei/";
	private final Activity context;
    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener, OnLongClickListener {
		// each data item is just a string in this case
		public String currentItem;
		public ImageView mImageView;
        public CardView container;

		public ViewHolder(View v) {
			super(v);
			mImageView = (ImageView) v.findViewById(R.id.bg_image);
			mImageView.setOnClickListener(this);
			mImageView.setOnLongClickListener(this);
            container = (CardView) v.findViewById(R.id.card_view);
		}

		@Override
		public void onClick(View v) {
			new SetWallpaperAsyncTask(v.getContext()).execute("");
			Toast.makeText(v.getContext(), "Setting Wallpaper", Toast.LENGTH_LONG).show();
		}
		
		@Override
		public boolean onLongClick(View v) {
			downloadFile(URL2 + currentItem, v.getContext());
			return true;
		}
		public void downloadFile(String uRl,Context c) {
			Context con = c;
		    File direct = new File(Environment.getExternalStorageDirectory()
		            + "/nowPaper");

		    if (!direct.exists()) {
		        direct.mkdirs();
		    }

		    DownloadManager mgr = (DownloadManager) con.getSystemService(Context.DOWNLOAD_SERVICE);

		    Uri downloadUri = Uri.parse(uRl);
		    DownloadManager.Request request = new DownloadManager.Request(
		            downloadUri);

		    request.setAllowedNetworkTypes(
		            DownloadManager.Request.NETWORK_WIFI
		                    | DownloadManager.Request.NETWORK_MOBILE)
		            .setAllowedOverRoaming(false).setTitle(currentItem.replaceAll("/",""))
		            .setDescription("nowPaper")
		            .setDestinationInExternalPublicDir("/nowPaper/", currentItem.replaceAll("/",""));
		    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		    mgr.enqueue(request);
		    Toast.makeText(c, "Downloading Image", Toast.LENGTH_LONG).show();
		}
		private class SetWallpaperAsyncTask extends
				AsyncTask<String, Void, String> {
			Context context;

			public SetWallpaperAsyncTask(Context context) {
				this.context = context;
			}

			@Override
			protected String doInBackground(String... params) {
				String URL = URL2 + currentItem;
				setWallpaper(URL);
				return "Executed";
			}

			@Override
			protected void onPostExecute(String result) {
			}

			@Override
			protected void onPreExecute() {
			}

			@Override
			protected void onProgressUpdate(Void... values) {
			}

			private void setWallpaper(String url) {
				try {
					WallpaperManager wpm = WallpaperManager
							.getInstance(context);
					InputStream ins = new URL(url).openStream();
					wpm.setStream(ins);				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// Provide a suitable constructor (depends on the kind of dataset)
	public RecAdapter(Activity context, String[] myDataset) {
		mDataset = myDataset;
		this.context = context;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public RecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.card, parent, false);
		// set the view's size, margins, paddings and layout parameters
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element

		Glide.with(context).load(URL + mDataset[position]).centerCrop()
				.into(holder.mImageView);
		String r = mDataset[position].replaceAll("/JPEG", "");
		String rest = r.replaceAll("jpg", "png");
		holder.currentItem = rest;
        setAnimation(holder.container, position);
	}
    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position){
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mDataset.length;
	}
}