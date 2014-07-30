package com.qualoutdoor.recorder;

/***********************************************************************/
/* Imported packages */
/***********************************************************************/
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

import com.qualoutdoor.recorder.recording.RecordingContext;
import com.qualoutdoor.recorder.recording.RecordingService;
import com.qualoutdoor.recorder.settings.SettingsActivity;
import com.qualoutdoor.recorder.telephony.ITelephony;
import com.qualoutdoor.recorder.telephony.TelephonyContext;
import com.qualoutdoor.recorder.telephony.TelephonyListener;
import com.qualoutdoor.recorder.telephony.TelephonyService;

public class MainActivity extends ActionBarActivity implements RecordingContext, TelephonyContext{

    /**************** State variable *******************/
    /** The current network type code */
    private int mNetworkType = ITelephony.NETWORK_TYPE_UNKNOWN;
    /** The data connection state */
    private int mDataState = ITelephony.DATA_DISCONNECTED;
    /** If the app is recording */
    private boolean mIsRecording = false;

    /** The telephony listener for the UI component in MainActivity */
    private TelephonyListener telephonyListener = new TelephonyListener() {
        @Override
        public void onDataStateChanged(int state, int networkType) {
            // Update the network var
            mNetworkType = networkType;
            // Update the UI
            updateNetworkView();
            // Update the data state var
            mDataState = state;
            // Update the UI
            updateDataStateView();
        };
    };
    
    /** The events the telephony listener will monitor */
    private int telephonyEvents = TelephonyListener.LISTEN_DATA_STATE;
    /** The TelephonyServiceConnection used to access the TelephonyService */
    private LocalServiceConnection<TelephonyService> telServiceConnection = new LocalServiceConnection<TelephonyService>(
            TelephonyService.class);
    /**
     * This component define the behavior when the TelephonyService becomes
     * available
     */
    private ServiceListener<TelephonyService> telephonyServiceListener = new ServiceListener<TelephonyService>() {
        @Override
        public void onServiceAvailable(TelephonyService service) {
            // Register the telephony listener
            service.listen(telephonyListener, telephonyEvents);
        }
    };

    /** The TelephonyServiceConnection used to access the TelephonyService */
    private LocalServiceConnection<RecordingService> recServiceConnection = new LocalServiceConnection<RecordingService>(
            RecordingService.class);
    /**
     * This component define the behavior when the RecordingService becomes
     * available
     */
    private ServiceListener<RecordingService> recordingServiceListener = new ServiceListener<RecordingService>() {
        @Override
        public void onServiceAvailable(RecordingService service) {
            // TODO update the recording button
        }
    };

    /*********** Navigation Drawer ****************/
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
     * Navigation Drawer physical behaviors
     */
    private ActionBarDrawerToggle drawerToggle;

    /*********** Action Bar Menu ****************/

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

        // Register the service listeners
        telServiceConnection.register(telephonyServiceListener);
        recServiceConnection.register(recordingServiceListener);
        
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
        // Bind to the TelephonyService
        telServiceConnection.bindToService(this);
        // Bind to the RecordingService
        recServiceConnection.bindToService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
        // Unbind from the TelephonyService if needed
        unbindService(telServiceConnection);
        // Unbind from the RecordingService if needed
        unbindService(recServiceConnection);
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
        { // TODO export in a updateRecordingButton
          // Check if we are recording
            if (mIsRecording) {
                // The icon should represent the stop record action
                recordMenuItem.setIcon(R.drawable.recording);
            } else {
                // The icon should represent the start record action
                recordMenuItem.setIcon(R.drawable.not_recording);
            }
        }
        // Update the network view
        updateNetworkView();
        // Update the data state view
        updateDataStateView();

        return super.onPrepareOptionsMenu(menu);
    }

    /** Update the data state indicator in the UI */
    private void updateDataStateView() {
        // Access the UI element from the UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Set the network view style according to the data state
                if (mDataState == TelephonyManager.DATA_CONNECTED) {
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
                networkView.invalidate();
            }
        });
    }

    /** Update the current network type indicator in the UI */
    private void updateNetworkView() {
        // Access the UI element from the UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Find the network names array
                String[] networkNames = getResources().getStringArray(
                        R.array.network_type_name);
                // Initialize the text view with the current network type string
                networkView.setText(networkNames[mNetworkType]);
                networkView.invalidate();
            }
        });
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
            if (recServiceConnection.isAvailable()) {
                if (recServiceConnection.getService().isRecording()) {
                    // The service is recording, so stop the recording
                    recServiceConnection.getService().stopRecording();
                } else {
                    // The service is not recording, so start the service
                    Intent recordingServiceIntent = new Intent(this,
                            RecordingService.class);
                    startService(recordingServiceIntent);
                }

                // TODO callback from the RecordingService
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

    @Override
    public ServiceProvider<TelephonyService> getTelephonyServiceProvider() {
        return telServiceConnection;
    }

    @Override
    public ServiceProvider<RecordingService> getRecordingServiceProvider() {
        return recServiceConnection;
    }

}
