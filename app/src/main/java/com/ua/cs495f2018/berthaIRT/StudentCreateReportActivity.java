package com.ua.cs495f2018.berthaIRT;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.dialog.CheckboxDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class StudentCreateReportActivity extends AppCompatActivity {

    private TextView tvDate;
    private TextView tvTime;
    private EditText etLocation;
    private EditText etDescription;
    private SeekBar sbThreat;

    private long incidentDateStamp = GregorianCalendar.getInstance().getTimeInMillis();
    private long incidentTimeStamp = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_createreport);

        sbThreat = findViewById(R.id.createreport_input_threat);

        tvDate = findViewById(R.id.createreport_input_date);
        tvDate.setOnClickListener(v -> actionSelectDate());

        tvTime = findViewById(R.id.createreport_input_time);
        tvTime.setOnClickListener(v -> actionSelectTime());

        etLocation = findViewById(R.id.createreport_input_location);
        etDescription = findViewById(R.id.createreport_input_description);

        //if you hit submit report
        findViewById(R.id.createreport_button_submit).setOnClickListener(v -> actionSubmitReport());
    }


    private void actionSelectDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog d = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            tvDate.setText(date);
            incidentDateStamp = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
        }, mYear, mMonth, mDay);

        //don't let the future time be picked
        d.getDatePicker().setMaxDate(System.currentTimeMillis());
        d.show();
    }

    private void actionSelectTime() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.ENGLISH,"%02d:%02d", hourOfDay, minute);
            tvTime.setText(time);
            incidentTimeStamp = (60*minute) + (3600*hourOfDay);
        }, mHour, mMinute, false).show();
    }

    private void actionSubmitReport() {
        if(etDescription.getText().toString().equals("")){
            etDescription.setError("You must provide a description.");
            return;
        }

        List<String> cats = Arrays.asList(getResources().getStringArray(R.array.category_item));
        List<Boolean> checked = new ArrayList<>();
        for(String ignored : cats)
            checked.add(false);

        Integer threat = sbThreat.getProgress() + 1;
        String description = etDescription.getText().toString();
        String location = etLocation.getText().toString();
        if(incidentDateStamp == 0)
            incidentDateStamp = new GregorianCalendar(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH).getTimeInMillis();

        //show the categories checkbox
        new CheckboxDialog(this, checked, cats, r->{
            Report newReport = new Report();
            newReport.setThreat(threat);
            newReport.setDescription(description);
            newReport.setLocation(location);
            newReport.setIncidentDate((incidentDateStamp + incidentTimeStamp));
            newReport.setCategories(r);
            Client.activeReport = newReport;
            Client.net.syncActiveReport(StudentCreateReportActivity.this, ()->{
                startActivity(new Intent(this, StudentReportDetailsActivity.class));
                finish();
            });
        }).show();
    }
}