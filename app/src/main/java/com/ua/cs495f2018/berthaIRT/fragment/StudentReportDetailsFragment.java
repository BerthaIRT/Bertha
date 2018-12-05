package com.ua.cs495f2018.berthaIRT.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Report;
import com.ua.cs495f2018.berthaIRT.Util;
import com.ua.cs495f2018.berthaIRT.dialog.ImageDialog;


public class StudentReportDetailsFragment extends Fragment {
    View v;

    TextView tvReportId, tvStatus, tvCreateTimestamp, tvLastActionTimestamp, tvIncidentTimestamp, tvThreat, tvDescription, tvLocation;

    public StudentReportDetailsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater flater, ViewGroup tainer, Bundle savedInstanceState){
        v = flater.inflate(R.layout.fragment_student_reportdetails, tainer, false);

        //Get the required views
        tvReportId = v.findViewById(R.id.student_reportdetails_alt_id);
        tvStatus = v.findViewById(R.id.student_reportdetails_alt_status);
        tvCreateTimestamp = v.findViewById(R.id.student_reportdetails_alt_creationdate);
        tvLastActionTimestamp = v.findViewById(R.id.student_reportdetails_alt_lastaction);
        tvIncidentTimestamp = v.findViewById(R.id.student_reportdetails_alt_incidentdate);
        tvThreat = v.findViewById(R.id.student_reportdetails_alt_threat);
        tvDescription = v.findViewById(R.id.student_reportdetails_alt_description);
        tvLocation = v.findViewById(R.id.student_reportdetails_alt_location);

        //set the media listener
        if(Client.activeReport.getMediaCount() > 0)
            v.findViewById(R.id.student_reportdetails_button_attachments).setOnClickListener(v1 ->
                new ImageDialog(getContext()).show());
        else {
            v.findViewById(R.id.student_reportdetails_button_attachments).setVisibility(View.GONE);
            v.findViewById(R.id.student_reportdetails_no_attachments).setVisibility(View.VISIBLE);
        }

        updateReportDisplay(Client.activeReport);
        return v;
    }

    private void updateReportDisplay(Report r) {
        tvReportId.setText(String.format("%s",r.getReportID()));
        tvCreateTimestamp.setText(Util.formatTimestamp(r.getCreationDate()));
        tvLastActionTimestamp.setText(Util.formatTimestamp(r.getLogs().get(r.getLogs().size() - 1).getMessageTimestamp()));
        tvStatus.setText(r.getStatus());
        tvIncidentTimestamp.setText(Util.formatTimestamp(r.getIncidentDate()));

        String threatString = r.getThreat() + "/5";
        tvThreat.setText(threatString);

        tvDescription.setText(r.getDescription());
        tvLocation.setText(r.getLocation());

        LinearLayout catTainer = v.findViewById(R.id.student_reportdetails_container_categories);
        catTainer.removeAllViews();
        for(String cat : r.getCategories()) {
            @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.adapter_category, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(cat);
            catTainer.addView(v);
        }
    }
}
