package com.letsandy.vavis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.os.Bundle;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.annotation.SuppressLint;

public class ScheduleActivity_date extends BaseActivity {

	@SuppressLint("SimpleDateFormat")
	protected void onCreate(Bundle savedInstanceState) {

		// setTheme(R.style.CustomTheme);
		setTheme(android.R.style.Theme_Holo);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addscheduleview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		super.onCreate(savedInstanceState);

		DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
		TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

		@SuppressWarnings("unused")
		Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		@SuppressWarnings("unused")
		SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
		//
	}
}
