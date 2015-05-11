package unipd.dei.ESP1415.falldetector;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetAlarmPreference extends DialogPreference {
	// per riuscire nelle Settings a far immettere all'utente l'ora
	// per la notifica

	private int hour = 0; // salviamo quante ore vuole che la sessione duri
	private int minute = 0; // salviamo quanti minuti deve durare la sessione
	private TimePicker timePicker = null;
	public String alarmTime = ""; // stringa da andare poi ad
													// aggiornare il database
	private Calendar calendar; // calendario per usare i metodi e fa vedere
								// all\'apertura l\'ora esatta

	// costruttore della nostra classe
	public SetAlarmPreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);
		calendar = new GregorianCalendar();
	}

	// creiamo cosa si deve mostrare quando si apre la view
	@Override
	protected View onCreateDialogView() {
		timePicker = new TimePicker(getContext());
		return timePicker;
	}

	// passiamo i valori iniziali al Dialog
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		// se è la prima volta che \l'utente inserisce l\'ora mostriamo l\'ora
		// esatta
		if ((hour == 0) && (minute == 0)) {
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
		// altrimenti passiamo l\'ultimo orario che aveva inserito per l\'ultima
		// sveglia
		else {
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
		}

	}

	// ora dobbiamo dire cosa fare quando l\'utente chiude il dialog, questo
	// comportamento dipende
	// se preme il tasto ok (positiveResult = true) oppure se preme il tasto
	// cancel (positiveResult = false)
	protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult){
        	hour = timePicker.getCurrentHour();
        	minute = timePicker.getCurrentMinute();
        	alarmTime = hour+":"+minute;
        	
        	//dobbiamo andare a modificare l'orario della sveglia nel database
        	//TODO
        	
        	//creiamo un toast che avvisa l'utente cha ha impostato l'ora per la sveglia
        	Toast.makeText(super.getContext(), "You set your alarm!", Toast.LENGTH_SHORT).show();
        }
	}

}