package unipd.dei.ESP1415.falldetector.preferences;


import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetAlarmPreference extends DialogPreference {
	// we create this preference to permit the user to set the time for the
	// notification
	private boolean isFirstTime; // I use this boolean to see if it's the first time that the user opens the TimePicker or if he opens it after unchecked and checked the checkbox
    private Context mContext;
	private int hour;
	private int minute;
	private TimePicker timePicker = null;
	public String alarmCustomTime = ""; // string for the toast
	private Calendar calendar; // calendar to show the right hour to the user
								// alarmTime he opens the TimePicker

	// constructor
	public SetAlarmPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		calendar = new GregorianCalendar();
		mContext=context;
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
		
		SharedPreferences mTimeSettings = mContext.getSharedPreferences("TimePickerSettings", Context.MODE_PRIVATE);
		isFirstTime = mTimeSettings.getBoolean("firstTime", true);
		// if it\'s the first time we show the right hour
		if (isFirstTime) {
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
		// then we show the time of the last alarm
		else {
			
			int oldHour = mTimeSettings.getInt("hour",calendar.get(Calendar.HOUR_OF_DAY) );
			int oldMinute = mTimeSettings.getInt("minute", calendar.get(Calendar.MINUTE));
			timePicker.setCurrentHour(oldHour);
			timePicker.setCurrentMinute(oldMinute);
		}

	}

	// what happens alarmTime the user closes the dialog
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			hour = timePicker.getCurrentHour();
			minute = timePicker.getCurrentMinute();
			alarmCustomTime = hour + ":" + minute;
			
			isFirstTime = false;
			
			//now we save the hour and minute in a SharedPreference file so 
			// I can show the time of the last alarm time
			SharedPreferences mTimeSettings = mContext.getSharedPreferences("TimePickerSettings", Context.MODE_PRIVATE);
            
			SharedPreferences.Editor mEditor = mTimeSettings.edit();
			mEditor.putInt("hour", hour);
			mEditor.putInt("minute", minute);
			mEditor.putBoolean("firstTime", isFirstTime);
			
			mEditor.commit();
			
			
			// set the alarm to start at the time of user decided
			Calendar calendar = Calendar.getInstance();
			
			//current time
			//long currentTime = calendar.getTimeInMillis();
			
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