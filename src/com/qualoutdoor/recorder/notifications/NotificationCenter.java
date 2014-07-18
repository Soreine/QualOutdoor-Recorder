package com.qualoutdoor.recorder.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.qualoutdoor.recorder.R;
import com.qualoutdoor.recorder.settings.SettingsActivity;

/**
 * This class is a utility class allowing the app to manage user notifications.
 * An exemple of notification is the "ongoing sampling" notification which
 * tells the user that the app is currently sampling
 */
public class NotificationCenter {

	/** The id of the background sampling notification */
	private final static int BACKGROUND_SAMPLING = 0;

	/** This class is not meant to be instantiated */
	private NotificationCenter() {
	}

	/** Switch on/off the ongoing sampling notification */
	public static void notifyBackgroundSampling(Context context) {
		// Get a notification builder
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				context);
		// Set the notification small icon
		notificationBuilder.setSmallIcon(R.drawable.notification_icon);
		// Set the notification title
		notificationBuilder.setContentTitle(context.getResources().getText(
				R.string.notification_sampling_title));
		// Set the notification text
		notificationBuilder.setContentText(context.getResources().getText(
				R.string.notification_sampling_text)); 
		// Indicate that the notification represent an ongoing process
		notificationBuilder.setOngoing(true); 
		// Set the priority of this notification to the minimum
		notificationBuilder.setPriority(NotificationCompat.PRIORITY_MIN);

		// Creates an explicit intent for the settings activity
		Intent resultIntent = new Intent(context, SettingsActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// the application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SettingsActivity.class);
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
		mNotificationManager.notify(BACKGROUND_SAMPLING,
				notificationBuilder.build());
	}

	public static void dismissBackgroundSampling(Context context) {
		// Get the notification manager from the host activity
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Cancel the notification
		mNotificationManager.cancel(BACKGROUND_SAMPLING);
	}

}
