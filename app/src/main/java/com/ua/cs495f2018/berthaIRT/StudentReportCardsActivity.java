package com.ua.cs495f2018.berthaIRT;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ua.cs495f2018.berthaIRT.fragment.AdminReportCardsFragment;
import com.ua.cs495f2018.berthaIRT.fragment.StudentReportCardsFragment;

public class StudentReportCardsActivity extends AppCompatActivity {

    final Fragment fragReports = new StudentReportCardsFragment();
    final FragmentManager fragDaddy = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_reportcards);

        fragDaddy.beginTransaction().add(R.id.student_reportcards_fragframe, fragReports, "Reports").show(fragReports).commit();
    }
}
