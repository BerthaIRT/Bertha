package com.ua.cs495f2018.berthaIRT.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.FirebaseNet;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.adapter.ReportCardAdapter;

public class StudentReportCardsFragment extends Fragment {
    RecyclerView rv;
    ReportCardAdapter adapter;
    TextView tvNoReports;

    public StudentReportCardsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater flater, ViewGroup tainer, Bundle savedInstanceState) {
        View v = flater.inflate(R.layout.fragment_student_reportcards, tainer, false);

        adapter = new ReportCardAdapter(getContext());

        rv = v.findViewById(R.id.admin_reports_rv);
        rv.setAdapter(adapter);

        tvNoReports = v.findViewById(R.id.student_reports_alt_noreports);
        return v;
    }


    @Override
    public void onResume(){
        super.onResume();
        if(adapter == null) return;
        adapter.updateReports(Client.reportMap.values());
        FirebaseNet.setOnRefreshHandler((r)-> adapter.updateReports(Client.reportMap.values()));
    }
}