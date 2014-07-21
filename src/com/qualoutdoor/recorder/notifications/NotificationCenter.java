package com.qualoutdoor.recorder.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.qualoutdoor.recorder.MainActivity;
import com.qualoutdoor.recorder.R;

/**
 * This class is a utility class allowing the app to manage user notifications.
 * An exemple of notification is the "ongoing recording" notification which
 * tells the user that the app is currently recording
 */
public class NotificationCenter {

	/** The id of the background recording notification */
	private final static int BACKGROUND_RECORDING = 0;

	/** This class is not meant to be instantiated */
	private NotificationCenter() {
	}

	/** Switch on/off the ongoing recording notification */
	public static void notifyBackgroundRecording(Context context) {
		// Create a little Toast :)
		Toast.makeText(context, R.string.notification_recording_title,
				Toast.LENGTH_SHORT).show();

		// Get a notification builder
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context);
		// Set the notification small icon
		notificationBuilder.setSmallIcon(R.drawable.notification_icon);
		// Set the notification title
		notificationBuilder.setContentTitle(context.getResources().getText(
				R.string.notification_recording_title));
		// Set the notification text
		notificationBuilder.setContentText(context.getResources().getText(
				R.string.notification_recording_text));
		// Indicate that the notification represent an ongoing process
		notificationBuilder.setOngoing(true);
		// Set the priority of this notification to the minimum
		notificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);

		// Creates an explicit intent for the settings activity
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// the application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		/**
		 * // Adds the parents back stack according to the parents relationships
		 * // defined in the app manifest
		 * stackBuilder.addParentStack(MainActivity.class);
		 */
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);

		// Obtain the pending intent associated to the task constructed so far
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		// Attach it to the notification builder
		notificationBuilder.setContentIntent(resultPendingIntent);

		// Get the notification manager from the host activity
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Build the notification and notify it with the appropriate tag
		mNotificationManager.notify(BACKGROUND_RECORDING,
				notificationBuilder.build());
	}

	public static void dismissBackgroundRecording(Context context) {
		// Get the notification manager from the host activity
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Cancel the notification
		mNotificationManager.cancel(BACKGROUND_RECORDING);
	}

}
