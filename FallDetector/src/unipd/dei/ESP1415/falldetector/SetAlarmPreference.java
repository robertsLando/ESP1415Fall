package unipd.dei.ESP1415.falldetector;


import java.sql.Timestamp;
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

public class SetAlarmPreference extends DialogPreference {
	// we create this preference to permit the user to set the time for the
	// notification

	private int hour = -1;
	private int minute = -1;
	private TimePicker timePicker = null;
	public String alarmCustomTime = ""; // string for the toast
	private Calendar calendar; // calendar to show the right hour to the user
								// alarmTime he opens the TimePicker

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
		if ((hour == -1) && (minute == -1)) {
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
		// then we show the time of the last alarm
		else {
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
		}

	}

	// what happens alarmTime the user closes the dialog
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			hour = timePicker.getCurrentHour();
			minute = timePicker.getCurrentMinute();
			alarmCustomTime = hour + ":" + minute;

			// set the alarm to start at the time of user decided
			Calendar calendar = Calendar.getInstance();
			
			//current time
			long currentTime = calendar.getTimeInMillis();
			
			//alarm time
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			long alarmTime = calendar.getTimeInMillis();
			Intent notificationIntent = new Intent(getContext(),
					MyNotificationBroadcastReceiver.class);
			PendingIntent notificationPendingIntent = PendingIntent
					.getBroadcast(getContext(), 0, notificationIntent,0);
			AlarmManager mAlarmManager = (AlarmManager) getContext()
					.getSystemService(Context.ALARM_SERVICE);
			
			//to compare two time we use the class Timestamp
			//Timestamp currentTimestamp = new Timestamp(currentTime);
			//Timestamp alarmTimestamp = new Timestamp(alarmTime);
			
			
			//FROM ANDROID DOCUMENTATION
            //repeating alarms are a good choice for scheduling regular events or data lookups.
			//A repeating alarm has the following characteristics:
			//A trigger time. If the trigger time you specify is in the past, the alarm triggers immediately.
			//if(alarmTimestamp.after(currentTimestamp)){
			mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, AlarmManager.INTERVAL_DAY,
					notificationPendingIntent);

			// create a toast
			Toast.makeText(super.getContext(),
					"You set the notification at " + alarmCustomTime + " !",
					Toast.LENGTH_SHORT).show();
			//}
		}
	}

}