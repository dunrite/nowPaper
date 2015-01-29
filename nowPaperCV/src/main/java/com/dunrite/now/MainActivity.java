/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dunrite.now;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.dunrite.now.R;
import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.internal.ProtocolConstants;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mColorIcons;
    private ArrayAdapter<String> adapter;
    private static SystemBarTintManager tintManager;
    private static boolean inLoc = false;
    
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Forces portrait orientation
        setContentView(R.layout.activity_main);
        
    
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.KITKAT) {  //set transparency if has kitkat
        	//----KITKAT TRANSPARENCY STUFF----------------------------------
            Window w = getWindow(); // in Activity's onCreate() for instance
           
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
               SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
               findViewById(android.R.id.content).setPadding(0, config.getPixelInsetTop(true),0,0);
        }
        if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.LOLLIPOP){
	        Window w = getWindow(); // in Activity's onCreate() for instance
	        
	        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, //Set the Nav Bar to translucent
	        		WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
	        tintManager = new SystemBarTintManager(this);
	        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            findViewById(android.R.id.content).setPadding(0, config.getPixelInsetTop(true),0,0);
        }
        mColorIcons = getResources().getStringArray(R.array.color_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        adapter = new MyPerformanceArrayAdapter(this, mColorIcons);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setScrollingCacheEnabled(false);
        mDrawerList.setAnimationCacheEnabled(false);
        // enable ActionBar app icon to behave as action to toggle nav drawer
       getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ablogo)); 
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setHomeButtonEnabled(true);
       getSupportActionBar().setDisplayShowHomeEnabled(true);
       getSupportActionBar().setDisplayUseLogoEnabled(true);
        //set color of action bar
       getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            @Override
			public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                

            }

            @Override
			public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }   
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);
        menu.findItem(R.id.muzei_settings).setVisible(!drawerOpen);
        if(!inLoc || !((MainActivity) this).isAppInstalled("net.nurik.roman.muzei"))
        	menu.findItem(R.id.muzei_apply).setVisible(false);
        else
        	menu.findItem(R.id.muzei_apply).setVisible(!drawerOpen);
        
        return super.onPrepareOptionsMenu(menu);
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_about:
        	inLoc=false;
        	supportInvalidateOptionsMenu();
        	selectItem(-1);
        	return true;
        case R.id.muzei_settings:
        	Intent intent = new Intent(this, SettingsActivity.class);
        	startActivity(intent);
        	return true;
        case R.id.muzei_apply:
        
        	if(((String)getSupportActionBar().getTitle()).toLowerCase().equals("rockys")){
        		Utils.setLocation(this, "Rocky Mountains");
        	}
        	else{
        		Utils.setLocation(this, capitalizeString(((String)getSupportActionBar().getTitle()).toLowerCase()));
        	}
        	Intent updateIntent = new Intent(this, ArtSource.class);
	        updateIntent.setAction(ProtocolConstants.ACTION_HANDLE_COMMAND);
	        updateIntent.putExtra(ProtocolConstants.EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
	        startService(updateIntent);
	        Toast.makeText(getApplicationContext(), "Location Applied in Muzei", Toast.LENGTH_LONG).show();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
   	
   public static String capitalizeString(String string) {
	   char[] chars = string.toLowerCase().toCharArray();
	   boolean found = false;
	   for (int i = 0; i < chars.length; i++) {
	     if (!found && Character.isLetter(chars[i])) {
	       chars[i] = Character.toUpperCase(chars[i]);
	       found = true;
	     } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
	       found = false;
	     }
	   }
	   return String.valueOf(chars);
	 }
    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new ColorFragment();
        Bundle args = new Bundle();
        args.putInt(ColorFragment.ARG_COLOR_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
           pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
           installed = true;
        } catch (PackageManager.NameNotFoundException e) {
           installed = false;
        }
        return installed;
    }
     
    /**
     * Fragment that appears in the "content_frame", shows wallpapers
     */
    public static class ColorFragment extends Fragment {
        public static final String ARG_COLOR_NUMBER = "color_number";
        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        

        public ColorFragment() {
            // Empty constructor required for fragment subclasses
        }
        
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	final View rootView;
        	int i = getArguments().getInt(ARG_COLOR_NUMBER);
        	if(i == -1){ //special case for about screen
        		rootView = inflater.inflate(R.layout.fragment_about, container, false);
        		return rootView;
        	}
            String color = getResources().getStringArray(R.array.colors_array)[i];
            String colorNS = color.replaceAll("\\s","");
        	if(!color.equals("Home") ){
        		inLoc=true;
	            rootView = inflater.inflate(R.layout.fragment_color, container, false);
	            
	            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
	
	            mRecyclerView.setHasFixedSize(true);
	            
	            // use a linear layout manager
	            mLayoutManager = new LinearLayoutManager(this.getActivity());
	            mRecyclerView.setLayoutManager(mLayoutManager);
	           
	            String[] myDataset = {(colorNS + "/JPEG/dawn.jpg"), 
	            		(colorNS + "/JPEG/day.jpg"), 
	            		(colorNS + "/JPEG/dusk.jpg"), 
	            		(colorNS + "/JPEG/night.jpg")};
	            
				// specify an adapter (see also next example)
	            mAdapter = new RecAdapter(getActivity(), myDataset);
	            mRecyclerView.setAdapter(mAdapter);
	            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(color.toUpperCase());
            }else{
            	inLoc=false;
            	rootView = inflater.inflate(R.layout.fragment_home, container, false);
            	if(((MainActivity)getActivity()).isAppInstalled("net.nurik.roman.muzei")){
            		
            	}
            }

            return rootView;  
            
        }
    }
}