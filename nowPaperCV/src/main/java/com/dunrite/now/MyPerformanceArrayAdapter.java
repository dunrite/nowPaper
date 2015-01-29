package com.dunrite.now;

import com.bumptech.glide.Glide;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MyPerformanceArrayAdapter extends ArrayAdapter<String>{

	 private final Activity context;
	 private final String[] names;

	  static class ViewHolder {
	    public ImageView image;
	  }

	  public MyPerformanceArrayAdapter(Activity context, String[] names) {
	    super(context, R.layout.drawer_list_item, names);
	    this.context = context;
	    this.names = names;
	  }
	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		    View rowView = convertView;
		    ViewHolder viewHolder = null;
		    // reuse views
		    if (rowView == null) {
		      LayoutInflater inflater = context.getLayoutInflater();
		      rowView = inflater.inflate(R.layout.drawer_list_item, null);
		      // configure view holder
		      viewHolder = new ViewHolder();
		      viewHolder.image = (ImageView) rowView
		          .findViewById(R.id.image);
		      rowView.setTag(viewHolder);
		    }
		    else{
		      viewHolder = (ViewHolder) rowView.getTag();
		    } 
		    String s = names[position];
		    Glide.with(context)
		    	.load(context.getResources().getIdentifier(s, "drawable", context.getPackageName()))
		    	.centerCrop()
		    	.crossFade()
		    	.into(viewHolder.image);
		    
		    return rowView;
	  }    
}

