package com.ua.cs495f2018.berthaIRT;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StudentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        ((TextView) findViewById(R.id.student_main_name)).setText(Client.userGroupName);

        //if you hit Create New Report
        findViewById(R.id.student_main_button_createreport).setOnClickListener(v ->
                startActivity(new Intent(StudentMainActivity.this, StudentCreateReportActivity.class)));

        //if you hit the option to view past submitted report
        findViewById(R.id.student_main_viewhistory).setOnClickListener(v ->
                startActivity(new Intent(StudentMainActivity.this, StudentReportCardsActivity.class)));

        Client.net.getEmblem(findViewById(R.id.student_main_img_emblem));
    }
}