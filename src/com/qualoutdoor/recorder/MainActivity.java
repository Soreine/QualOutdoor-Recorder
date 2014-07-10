package com.qualoutdoor.recorder;

/***********************************************************************/
/* Imported packages */
/***********************************************************************/
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

	/***********************************************************************/
	/* Public attributes */
	/***********************************************************************/

	/***********************************************************************/
	/* Private attributes */
	/***********************************************************************/
	/// The activity title
	private CharSequence title;
	/// The drawer title
	private CharSequence drawerTitle;

	/// Hold the navigation titles displayed in the Navigation Drawer
	private String[] navigationTitles;
	/// A reference to the Navigation Drawer layout
	private DrawerLayout drawerLayout;
	/// The ListView associated to the Navigation Drawer
	private ListView drawerList;
	/**
	 * A DrawerListener that integrate well with the ActionBar and handle the
	 * Navigation Drawer behaviors
	 */
	private ActionBarDrawerToggle drawerToggle;

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
			// Get the ListView reference
			drawerList = (ListView) findViewById(R.id.left_drawer);

			// Set the adapter for the list view
			drawerList.setAdapter(new ArrayAdapter<String>(this,
					R.layout.drawer_list_item, navigationTitles));
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
					getSupportActionBar().setTitle(title);
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

		// Initialize activity title
		title = getResources().getString(R.string.title_overview);

		// Check that we are not being restored from a previous state
		if (savedInstanceState == null) {
			// Set the selected view as the first (Overview)
			selectItem(0);
		}
	}

	/** The navigation drawer items click listener */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		/** Called when a item click occured */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// The behavior has been exported to the selectItem method.
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view on item selection */
	private void selectItem(int position) {

		// Create the corresponding fragment
		Fragment fragment = NavigationDrawer.getFragment(position);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragment)
				.commit();

		// Highlight the selected item
		drawerList.setItemChecked(position, true);
		// Set the title
		setTitle(navigationTitles[position]);
		// Close the drawer
		drawerLayout.closeDrawer(drawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title = title;
		getSupportActionBar().setTitle(title);
	}

	/***********************************************************************/
	/* ActionBar Option Menu */
	/***********************************************************************/

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
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

}
