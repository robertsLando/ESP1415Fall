package unipd.dei.ESP1415.falldetector;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import unipd.dei.ESP1415.falldetector.MyNotificationBroadcastReceiver;

public class SetAlarmPreference extends DialogPreference {
	// we create this preference to permit the user to set the time for the
	// notification

	private int hour = 0;
	private int minute = 0;
	private TimePicker timePicker = null;
	public String alarmTime = ""; // string for the toast
	private Calendar calendar; // calendar to show the right hour to the user
								// when he opens the TimePicker

	// constructor
	public SetAlarmPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		calendar = new GregorianCalendar();
	}

	// we crate the TimePicker we have to show to the user
	@Override
	protected View onCreateDialogView() {
		timePicker = new TimePicker(getContext());
		return timePicker;
	}

	// initial values
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		// if it\'s the first time we show the right hour
		if ((hour == 0) && (minute == 0)) {
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
		// then we show the time of the last alarm
		else {
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
		}

	}

	// what happens when the user closes the dialog
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			hour = timePicker.getCurrentHour();
			minute = timePicker.getCurrentMinute();
			alarmTime = hour + ":" + minute;

			// set the alarm to start at the time of user decided
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			long when = calendar.getTimeInMillis();
			Intent notificationIntent = new Intent(getContext(),
					MyNotificationBroadcastReceiver.class);
			PendingIntent notificationPendingIntent = PendingIntent
					.getBroadcast(getContext(), 0, notificationIntent,0);
			AlarmManager mAlarmManager = (AlarmManager) getContext()
					.getSystemService(Context.ALARM_SERVICE);
			mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, when, AlarmManager.INTERVAL_DAY,
					notificationPendingIntent);

			// create a toast
			Toast.makeText(super.getContext(),
					"You set the notification at " + alarmTime + " !",
					Toast.LENGTH_SHORT).show();
		}
	}

}