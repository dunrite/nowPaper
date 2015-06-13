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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.google.android.apps.muzei.api.internal.ProtocolConstants;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mColorIcons;
    private ArrayAdapter<String> adapter;
    private static Context context;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();

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
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ablogo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        switch (item.getItemId()) {
            case R.id.action_about:
                getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ablogo));
                supportInvalidateOptionsMenu();
                selectItem(-1);
                return true;
            case R.id.muzei_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
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

    public void setInMuzei() {

        Utils.setLocation(this, capitalizeString(((String) getSupportActionBar().getTitle()).toLowerCase()));
        Intent updateIntent = new Intent(this, ArtSource.class);
        updateIntent.setAction(ProtocolConstants.ACTION_HANDLE_COMMAND);
        updateIntent.putExtra(ProtocolConstants.EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK);
        startService(updateIntent);
        Snackbar
                .make(findViewById(R.id.main_content), R.string.applied, Snackbar.LENGTH_LONG)
                .show();
    }
    /**
     * Static way for the java classes to receive the application context
     */
    public static Context getAppContext() {
        return MainActivity.context;
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
            if (i == -1) { //special case for about screen
                rootView = inflater.inflate(R.layout.fragment_about, container, false);
                return rootView;
            }
            String color = getResources().getStringArray(R.array.colors_array)[i];
            String colorNS = color.replaceAll("\\s", "");
            if (!color.equals("Home")) {
                rootView = inflater.inflate(R.layout.fragment_color, container, false);
                ((MainActivity) getActivity()).getSupportActionBar().setIcon(getResources().getColor(android.R.color.transparent));
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
                mRecyclerView.setHasFixedSize(true);

                // use a linear layout manager
                mLayoutManager = new LinearLayoutManager(this.getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

                String[] myDataset = {(colorNS + "/JPEG/dawn.jpg"),
                        (colorNS + "/JPEG/day.jpg"),
                        (colorNS + "/JPEG/dusk.jpg"),
                        (colorNS + "/JPEG/night.jpg")};

                // specify an adapter (see also next example)
                mAdapter = new RecAdapter(getActivity(), myDataset);
                mRecyclerView.setAdapter(mAdapter);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(color);

                View.OnClickListener fabListen = new View.OnClickListener() {
                    Activity c = getActivity();
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) c).setInMuzei();
                    }
                };
                fab.setOnClickListener(fabListen);
                if (!((MainActivity) getActivity()).isAppInstalled("net.nurik.roman.muzei"))
                    fab.setVisibility(View.INVISIBLE);
                else
                    fab.setVisibility(View.VISIBLE);
            } else {
                ((MainActivity) getActivity()).getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.ablogo));
                rootView = inflater.inflate(R.layout.fragment_home, container, false);
                CardView muzeiCard = (CardView) rootView.findViewById(R.id.card_4);
                TextView muzeiDesc = (TextView) rootView.findViewById(R.id.muzei);
                final boolean isInstalled;
                if (((MainActivity) getActivity()).isAppInstalled("net.nurik.roman.muzei")) {
                    muzeiDesc.setText(R.string.muzei_enable);
                    isInstalled=true;
                }else{
                    muzeiDesc.setText(R.string.muzei_install);
                    isInstalled=false;
                }
                View.OnClickListener m = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isInstalled){
                            Intent i;
                            PackageManager manager = getAppContext().getPackageManager();
                            try {
                                i = manager.getLaunchIntentForPackage("net.nurik.roman.muzei");
                                if (i == null)
                                    throw new PackageManager.NameNotFoundException();
                                i.setClassName("net.nurik.roman.muzei", "com.google.android.apps.muzei.settings.SettingsActivity");
                                getAppContext().startActivity(i);
                            } catch (PackageManager.NameNotFoundException e) {

                            }
                        }else{
                            rootView.getContext().startActivity(
                                    new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=net.nurik.roman.muzei")));
                        }
                    }
                };
                muzeiCard.setOnClickListener(m);
            }
            return rootView;
        }
    }
}