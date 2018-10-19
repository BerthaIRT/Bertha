package com.ua.cs495_f18.berthaIRT;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserDisplayReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_displayreport);

        getIncomingIntent();

        FloatingActionButton fab = findViewById(R.id.button_user_goto_report_messages);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionGotoMessages();
            }
        });
    }

    private void actionGotoMessages() {
        startActivity(new Intent(UserDisplayReportActivity.this,ChatActivity.class));
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("report_id")){
            String reportId = getIntent().getStringExtra("report_id");
            String date = "05/04/22";
            String time = "5:22PM";
            String status = "Unopened";
            String description = "Lorum ipsum blah blah";
            ((TextView) findViewById(R.id.label_user_viewreport_id_value)).setText(reportId);
            ((TextView) findViewById(R.id.label_user_viewreport_date_value)).setText(date);
            ((TextView) findViewById(R.id.label_user_viewreport_time_value)).setText(time);
            ((TextView) findViewById(R.id.label_user_viewreport_status_value)).setText(status);
            ((TextView) findViewById(R.id.label_user_viewreport_description_value)).setText(description);
        }
        else if(getIntent().hasExtra("need_update")){
            updateDisplay();
        }
    }

    //TODO finish this function to update the display with new SQL information after an edit is made.
    private void updateDisplay(){
        //get info from SQL
        //CAN REMOVE THIS ONLY FOR TEST
    }
    //TODO add an export report function.

    // Override onResume to update when resumed.
    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
    }
}