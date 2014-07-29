package com.qualoutdoor.recorder;

/***********************************************************************/
/* Imported packages */
/***********************************************************************/
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.qualoutdoor.recorder.recording.RecordingService;
import com.qualoutdoor.recorder.recording.RecordingServiceConnection;
import com.qualoutdoor.recorder.settings.SettingsActivity;
import com.qualoutdoor.recorder.telephony.ITelephony;
import com.qualoutdoor.recorder.telephony.TelephonyListener;
import com.qualoutdoor.recorder.telephony.TelephonyService;
import com.qualoutdoor.recorder.telephony.TelephonyServiceConnection;
import com.qualoutdoor.recorder.telephony.TelephonyServiceConnectionProvider;

public class MainActivity extends ActionBarActivity implements
        TelephonyServiceConnectionProvider {

    /***********************************************************************/
    /* Public attributes */
    /***********************************************************************/

    /***********************************************************************/
    /* Private attributes */
    /***********************************************************************/

    /** A reference to the TelephonyService */
    private TelephonyService telephonyService;
    /** The telephony listener for the UI component in MainActivity */
    private TelephonyListener telephonyListener = new TelephonyListener() {
        @Override
        public void onDataStateChanged(int state, int networkType) {
            updateNetwork(networkType);
            updateDataState(state);
        };
    };
    /** The events the telephony listener will monitor */
    private int telephonyEvents = TelephonyListener.LISTEN_DATA_STATE;
    /** The TelephonyServiceConnection used to connect to the TelephonyService */
    private TelephonyServiceConnection telServiceConnection = new TelephonyServiceConnection() {
        @Override
        public void onServiceObtained() {
            // Give the service to the activity
            MainActivity.this.telephonyService = this.getService();
            // Register listener
            MainActivity.this.telephonyService.listen(telephonyListener,
                    telephonyEvents);
        }
    };

    @Override
    public TelephonyServiceConnection getTelephonyServiceConnection() {
        return telServiceConnection;
    }

    /** A reference to the RecordingService */
    private RecordingService recordingService;
    /** The TelephonyServiceConnection used to connect to the TelephonyService */
    private RecordingServiceConnection recServiceConnection = new RecordingServiceConnection() {
        @Override
        public void onServiceObtained() {
            // Give the service to the activity
            MainActivity.this.recordingService = this.getService();
        }
    };

    /** The current fragment title */
    private CharSequence fragmentTitle;
    /** The drawer title */
    private CharSequence drawerTitle;
    /** The active section in the navigation drawer */
    private int activeSection = -1;

    /** The previous actionbar title key in the savedInstanceState */
    private static final String PREVIOUS_TITLE = "previous_title";
    /** The active section key in the savedInstanceState */
    private static final String ACTIVE_SECTION = "active_section";

    /** Hold the navigation titles displayed in the Navigation Drawer */
    private String[] navigationTitles;
    /** A reference to the Navigation Drawer layout */
    private DrawerLayout drawerLayout;
    /** The ListView associated to the Navigation Drawer */
    private ListView drawerList;
    /**
     * A DrawerListener that integrate well with the ActionBar and handle the
     * Navigation Drawer behaviors
     */
    private ActionBarDrawerToggle drawerToggle;

    /**
     * The view located in the action bar which displays the current network
     * type
     */
    private TextView networkView;
    /** The record action from the options menu */
    private MenuItem recordMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the drawer title
        drawerTitle = getResources().getString(R.string.title_activity_main);
        // Retrieve the navigation titles
        navigationTitles = getResources().getStringArray(
                R.array.top_level_navigation_titles);

        // Initialize the Navigation Drawer's content
        {
            // Get the DrawerLayout instance
            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            // Set a custom shadow that overlays the main content when the
            // drawer opens
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                    GravityCompat.START);
            // Get the ListView reference
            drawerList = (ListView) findViewById(R.id.left_drawer);
            // Items cannot be focused (as would an EditText for example)
            drawerList.setItemsCanFocus(false);

            // Set the adapter for the list view
            drawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.view_drawer_list_item, navigationTitles));
            // Set the list's click listener
            drawerList.setOnItemClickListener(new DrawerItemClickListener());

            // Instantiate an ActionBarDrawerToggle which implements
            // DrawerListener
            drawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
            drawerLayout, /* DrawerLayout object */
            R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
            R.string.drawer_open, /* "open drawer" description */
            R.string.drawer_close /* "close drawer" description */
            ) {

                /**
                 * Called when a drawer has settled in a completely closed
                 * state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    // Set the ActionBar title to the fragment title
                    getSupportActionBar().setTitle(fragmentTitle);
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // Set the ActionBar title to the drawer title
                    getSupportActionBar().setTitle(drawerTitle);
                }
            };

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(drawerToggle);

            // Enable the home button in the ActionBar
            getSupportActionBar().setHomeButtonEnabled(true);
            // Set the Home button to display an drawer indicator
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        // Check we are not being restored
        if (savedInstanceState == null) {
            // Set the first fragment as the selected view
            selectItem(0);
        } else {
            // Retrieve the active section
            activeSection = savedInstanceState.getInt(ACTIVE_SECTION);
            // Get the corresponding fragment title
            fragmentTitle = navigationTitles[activeSection];
            // Restore the previous ActionBar title (this depends on the state
            // of the drawer)
            getSupportActionBar().setTitle(
                    savedInstanceState.getCharSequence(PREVIOUS_TITLE));
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the onConfigurationChanged call to the drawerToggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the active section
        outState.putInt(ACTIVE_SECTION, activeSection);
        // Save the current action bar title
        outState.putCharSequence(PREVIOUS_TITLE, getSupportActionBar()
                .getTitle());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart");

        // We do not check if the ServiceConnections are bound already,
        // because it seems they never are when starting the activity...

        // Bind to the TelephonyService
        {
            // Create an intent toward TelephonyService
            Intent intent = new Intent(this, TelephonyService.class);
            // Bind to TelephonyService through TelephonyServiceConnection
            bindService(intent, telServiceConnection, Context.BIND_AUTO_CREATE);
        }

        // Bind to the RecordingService
        {
            // Create an intent toward TelephonyService
            Intent intent = new Intent(this, RecordingService.class);
            // bind to RecordingService through our RecordingServiceConnection
            bindService(intent, recServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");

        // Unbind from the TelephonyService if needed
        if (telServiceConnection.isBound()) {
            unbindService(telServiceConnection);
        }
        // Unbind from the RecordingService if needed
        if (recServiceConnection.isBound()) {
            unbindService(recServiceConnection);
        }
    }

    /** The navigation drawer items click listener */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            // The behavior has been exported to the selectItem method.
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view on item selection */
    private void selectItem(int position) {
        // Check that we selected an different item than the active one
        if (position != activeSection) {
            // Create the corresponding fragment
            Fragment fragment = NavigationDrawerBehavior.getFragment(position);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

            // Highlight the selected item
            drawerList.setItemChecked(position, true);

            // Update the fragment title
            fragmentTitle = navigationTitles[position];

            // Set the ActionBar title to the fragment title
            getSupportActionBar().setTitle(fragmentTitle);

            // Set the active section
            activeSection = position;
        }
        // Close the drawer
        drawerLayout.closeDrawer(drawerList);
    }

    /***********************************************************************/
    /* ActionBar Options Menu */
    /***********************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Initialize the record action reference
        recordMenuItem = menu.findItem(R.id.action_record);

        // Get the network item reference
        MenuItem networkMenuItem = menu.findItem(R.id.network_info);
        // Initialize the network info view reference
        networkView = (TextView) MenuItemCompat.getActionView(networkMenuItem);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Check that we are bound to the recording service and if it is
        // recording
        if (recServiceConnection.isBound() && recordingService.isRecording()) {
            // The icon should represent the stop record action
            recordMenuItem.setIcon(R.drawable.recording);
        } else {
            // The icon should represent the start record action
            recordMenuItem.setIcon(R.drawable.not_recording);
        }

        // Initialize the network info view
        {// The current network is unknown
            int currentNetwork = ITelephony.NETWORK_TYPE_UNKNOWN;
            // We first assume that data is off
            int dataState = ITelephony.DATA_DISCONNECTED;
            // If we can access to the TelephonyService
            if (telServiceConnection.isBound()) {
                // Get the current network type
                currentNetwork = telephonyService.getNetworkType();
                // Get the phonte state
                dataState = telephonyService.getDataState();
            }
            // Update the network view
            updateNetwork(currentNetwork);
            updateDataState(dataState);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /** Update the data state indicator in the UI */
    private void updateDataState(int currentDataState) {
        // Set the network view style according to the data state
        if (currentDataState == TelephonyManager.DATA_CONNECTED) {
            // Data is on
            // Change color to highlight the state
            networkView.setTextColor(getResources().getColor(
                    R.color.network_info_highlight));
            networkView.setTypeface(null, Typeface.BOLD_ITALIC);
        } else {
            // Data is off
            networkView.setTextColor(getResources().getColor(
                    R.color.normal_text));
            networkView.setTypeface(null, Typeface.NORMAL);
        }
    }

    public void updateNetwork(int currentNetwork) {
        // Find the network names array
        String[] networkNames = getResources().getStringArray(
                R.array.network_type_name);
        // Initialize the text view with the current network type string
        networkView.setText(networkNames[currentNetwork]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle the other action bar items...
        switch (item.getItemId()) {
        case R.id.action_record:
            // Check that we are bound to the recording service
            if (recServiceConnection.isBound()) {
                if (recordingService.isRecording()) {
                    // The service is recording, so stop the recording
                    recordingService.stopRecording();
                } else {
                    // The service is not recording, so start the service
                    Intent recordingServiceIntent = new Intent(this,
                            RecordingService.class);
                    startService(recordingServiceIntent);
                }

                // Update the Options Menu view (for the record icon to change)
                supportInvalidateOptionsMenu();
            }
            return true;
        case R.id.action_settings:
            openSettings();
            return true;
        case R.id.action_help:
            openHelp();
            return true;
        default:
            // We don't handle the event, pass it to the super class
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // We desire to go back to the main section when back button is pressed,
        // and
        // to leave the app if we were in the main section already
        if (activeSection == 0) {
            // Defer to the system default behavior
            super.onBackPressed();
        } else {
            selectItem(0);
        }

    }

    /** Action associated to the settings option menu item */
    private void openSettings() {
        // Create an intent toward the DisplaySettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        // Start the activity
        startActivity(intent);
    }

    /** Action associated to the help option menu item */
    private void openHelp() {
        // Create an intent toward the DisplayHelpActivity
        Intent intent = new Intent(this, DisplayHelpActivity.class);
        // Start the activity
        startActivity(intent);
    }

}
