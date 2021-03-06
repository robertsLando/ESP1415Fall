package unipd.dei.ESP1415.falldetector.preferences;

import unipd.dei.ESP1415.falldetector.R;
import unipd.dei.ESP1415.falldetector.R.drawable;
import unipd.dei.ESP1415.falldetector.R.string;
import unipd.dei.ESP1415.falldetector.mainactivity.MainActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("NewApi")
public class MyNotificationBroadcastReceiver extends BroadcastReceiver {
	// this is a simple broadcast in which we override the method onReceive to
	// create the notification
	int notifyID = 1;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {

		// prepare the Notification
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(context.getResources().getString(R.string.notification_title))
				.setContentText(context.getResources().getString(R.string.notification_text))
				.setAutoCancel(true);

		// set what happens when the user click on the notification
		Intent notificationIntent = new Intent(context, MainActivity.class);

		// FROM ANDROID DOCUMENTATION:
		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(notificationIntent);

		// Inside a Notification, the action itself is defined by a
		// PendingIntent containing an Intent that starts an Activity in your
		// application.
		PendingIntent notificationPendingIntent = stackBuilder
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		// set the intent to the notificationCompat
		mBuilder.setContentIntent(notificationPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// notify the notification only if the checkBox is activate
		//we have to check in the SharedPreference the state of the checkbox
		//if false---->we haven't to notify the notification in the status bar
		SharedPreferences settings = 
		        PreferenceManager.getDefaultSharedPreferences(context);
		
		boolean isChecked = settings.getBoolean("pref_notification_activate", true);
		
		if(isChecked){ //we show the notification only if the checkbox is checked
		//Post a notification to be shown in the status bar.
		mNotificationManager.notify(notifyID, mBuilder.build());
		}
		
	}

}