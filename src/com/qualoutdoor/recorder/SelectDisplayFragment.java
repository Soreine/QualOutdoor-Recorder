package com.qualoutdoor.recorder;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectDisplayFragment extends ListFragment {

	// The titles of the different views
	static String[] viewTitles = { "Time Graph", "Map View" };
	// The short description of each view
	static String[] viewDescription = { "Display signal strength against time.",
			"Display signal strength on a map." };

	OnViewSelectedListener mCallback;

	// The container Activity must implement this interface so the frag can
	// deliver messages
	public interface OnViewSelectedListener {
		/** Called by SelectDisplayFragment when a list item is selected */
		public void onViewSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// We need to use a different list item layout for devices older than
		// Honeycomb
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;

		// Create an array adapter for the list view, using the view titles
		// array
		setListAdapter(new ArrayAdapter<String>(getActivity(), layout,
				viewTitles));
	}

	@Override
	public void onStart() {
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
//		if (getFragmentManager().findFragmentById(R.id.select_display_fragment) != null) {
//			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnViewSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnViewSelectedListener");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notify the parent activity of selected item
		mCallback.onViewSelected(position);

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);
	}
}
