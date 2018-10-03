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
                startActivity(new Intent(UserDisplayReportActivity.this,MessageActivity.class));
            }
        });
    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("report_id")){
            String reportId = getIntent().getStringExtra("report_id");
            //TODO Look up ReportID in SQL and set the rest of the values accordingly.
            setReportId(reportId);
            setDate("05/04/22");
            setTime("05:22 PM/AM");
            setStatus("Unopened");
            setDescription("I once knew a fish named Larry.");
        }
        else if(getIntent().hasExtra("need_update")){
            updateDisplay();
        }

    }

    private void setReportId(String s){
        TextView tv = findViewById(R.id.label_user_viewreport_id_value);
        tv.setText(s);
    }

    private void setDate(String s){
        TextView tv = findViewById(R.id.label_user_viewreport_date_value);
        tv.setText(s);
    }

    private void setTime(String s){
        TextView tv = findViewById(R.id.label_user_viewreport_time_value);
        tv.setText(s);
    }

    private void setStatus(String s){
        TextView tv = findViewById(R.id.label_user_viewreport_status_value);
        tv.setText(s);
    }

    private void setDescription(String s){
        TextView tv = findViewById(R.id.label_user_viewreport_description_value);
        tv.setText(s);
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