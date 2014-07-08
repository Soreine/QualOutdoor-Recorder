package com.qualoutdoor.recorder;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	// The activity title
	private String title;

	// The drawer title
	private String drawerTitle;

	// Hold the navigation titles displayed in the Navigation Drawer
	private String[] navigationTitles;
	// A reference to the Navigation Drawer layout
	private DrawerLayout drawerLayout;
	// The ListView associated to the Navigation Drawer
	private ListView drawerList;
	// A DrawerListener that integrate well with the ActionBar
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize the Navigation Drawer's content
		{
			// Retrieve the navigation titles
			navigationTitles = getResources().getStringArray(
					R.array.top_level_navigation_titles);
			// Get the DrawerLayout instance
			drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			// Get the ListView reference
			drawerList = (ListView) findViewById(R.id.left_drawer);

			// Set the adapter for the list view
			drawerList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.drawer_list_item, navigationTitles));
			// Set the list's click listener
			// drawerList.setOnItemClickListener(new DrawerItemClickListener());

			// Instantiate an ActionBarDrawerToggle which implements
			// DrawerListener
			mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
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
					getActionBar().setTitle(title);
				}

				/** Called when a drawer has settled in a completely open state. */
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					getActionBar().setTitle(drawerTitle);
				}
			};

			// Set the drawer toggle as the DrawerListener
			drawerLayout.setDrawerListener(mDrawerToggle);

			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);
		}

		// Initialize activity title
		title = getResources().getString(R.string.title_overview);

		// Initialize the drawer title
		drawerTitle = getResources().getString(R.string.title_activity_main);
		
		// Check that we are not being restored from a previous state
		if (savedInstanceState == null) {
			// Instantiate the Overview fragment and add it to the corresponding
			// container
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.container,
							new GenericFragment(getResources().getString(
									R.string.title_overview))).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

}
