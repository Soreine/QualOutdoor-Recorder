package com.qualoutdoor.recorder;

import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

public class ChangeNetworkButton extends Button {

	public ChangeNetworkButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ChangeNetworkButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ChangeNetworkButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onAttachedToWindow() {
		// Set the listener for this button
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionChangeNetwork(v);
			}
		});
		super.onAttachedToWindow();
	}

	/** Change the current network artificially */
	private void actionChangeNetwork(View view) {
		// Create a random generator
		Random rnd = new Random();
		// Get the network names array from the ressources
		String[] networkNames = getResources().getStringArray(
				R.array.network_type_name);
		// Chose a random network
		int network = rnd.nextInt(networkNames.length);
		// Update the application network variable value
		((QualOutdoorApp) getContext().getApplicationContext())
				.setCurrentNetwork(network);
	}
}
