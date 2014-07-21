package com.qualoutdoor.recorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * A button which starts or stops the background sampling (this only displays
 * notification)
 */
public class SamplingButton extends Button {

	public SamplingButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public SamplingButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SamplingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onAttachedToWindow() {
		// Set the listener for this button
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionStartStop(v);
			}
		});
		super.onAttachedToWindow();
	}

	private void actionStartStop(View view) {
		// Get the application context
		QualOutdoorApp app = (QualOutdoorApp) getContext()
				.getApplicationContext();
		// Start/Stop the recording
		app.switchRecording();
	}
}
