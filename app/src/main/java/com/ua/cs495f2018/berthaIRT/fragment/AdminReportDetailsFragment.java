package com.ua.cs495f2018.berthaIRT.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.LogActivity;
import com.ua.cs495f2018.berthaIRT.Message;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Report;
import com.ua.cs495f2018.berthaIRT.Util;
import com.ua.cs495f2018.berthaIRT.adapter.NotesAdapter;
import com.ua.cs495f2018.berthaIRT.dialog.AddRemoveDialog;
import com.ua.cs495f2018.berthaIRT.dialog.CheckboxDialog;
import com.ua.cs495f2018.berthaIRT.dialog.ImageDialog;
import com.ua.cs495f2018.berthaIRT.dialog.NotesDialog;
import com.ua.cs495f2018.berthaIRT.dialog.WaitDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ua.cs495f2018.berthaIRT.Client.net;

public class AdminReportDetailsFragment extends Fragment {
    LinearLayout catTainer, tagTainer;

    TextView tvReportId, tvStatus, tvCreateTimestamp, tvLastActionTimestamp, tvIncidentTimestamp, tvThreat, tvDescription, tvLocation, tvAdmins, tvNoNotes, tvOpen, tvClosed, tvResolved;

    CardView cvOpen, cvClosed, cvResolved;

    private NotesAdapter adapter;
    List<Message> notesList = new ArrayList<>();

    final int[] pushedState = new int[]{android.R.attr.state_enabled,android.R.attr.state_pressed};
    final int[] unpushedState = new int[]{android.R.attr.state_enabled,-android.R.attr.state_pressed};

    public AdminReportDetailsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater flater, ViewGroup tainer, Bundle savedInstanceState){
        View v = flater.inflate(R.layout.fragment_admin_reportdetails, tainer, false);

        //Find all the views
        tvReportId = v.findViewById(R.id.admin_reportdetails_alt_id);
        tvStatus = v.findViewById(R.id.admin_reportdetails_alt_status);
        tvCreateTimestamp = v.findViewById(R.id.admin_reportdetails_alt_creationdate);
        tvLastActionTimestamp = v.findViewById(R.id.admin_reportdetails_alt_lastaction);
        tvIncidentTimestamp = v.findViewById(R.id.admin_reportdetails_alt_incidentdate);
        tvThreat = v.findViewById(R.id.admin_reportdetails_alt_threat);
        tvDescription = v.findViewById(R.id.admin_reportdetails_alt_description);
        tvLocation = v.findViewById(R.id.admin_reportdetails_alt_location);
        cvOpen = v.findViewById(R.id.cardviewOpen);
        cvClosed = v.findViewById(R.id.cardviewClosed);
        cvResolved = v.findViewById(R.id.cardviewResolved);
        tvOpen = v.findViewById(R.id.textViewOpen);
        tvClosed = v.findViewById(R.id.textViewClosed);
        tvResolved = v.findViewById(R.id.textViewResolved);

        //Set the Category and Tag Recycler Views
        catTainer = v.findViewById(R.id.admin_reportdetails_container_categories);
        tagTainer = v.findViewById(R.id.admin_reportdetails_container_tags);
        tvAdmins = v.findViewById(R.id.admin_reportdetails_alt_assignedadmins);
        tvNoNotes = v.findViewById(R.id.admin_reportdetails_notes_no);

        RecyclerView rv = v.findViewById(R.id.admin_reportdetails_notes_rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));

        adapter = new NotesAdapter(notesList);
        rv.setAdapter(adapter);

        //Set the required on click listeners
        v.findViewById(R.id.admin_reportdetails_button_viewlog).setOnClickListener(v1 ->
                startActivity(new Intent(getActivity(), LogActivity.class)) );

        //if attachments is hit
        //TODO get attachments from server
        if(Client.activeReport.getMediaCount() > 0)
            v.findViewById(R.id.admin_reportdetails_button_attachments).setOnClickListener(v1 ->
                    new ImageDialog(getContext()).show());
        else {
            v.findViewById(R.id.student_reportdetails_button_attachments).setVisibility(View.GONE);
            v.findViewById(R.id.admin_reportdetails_no_attachments).setVisibility(View.GONE);
        }


        //Listener for editing categories. It gets the selected ones first
        v.findViewById(R.id.admin_reportdetails_button_editcategory).setOnClickListener(v1 ->
                new CheckboxDialog(getActivity(), Util.getPreChecked(Arrays.asList(getResources().getStringArray(R.array.category_item)), Client.activeReport.getCategories()),
                        Arrays.asList(getResources().getStringArray(R.array.category_item)), this::finishEditCategories).show());

        //lets you edit tags
        v.findViewById(R.id.admin_reportdetails_button_edittags).setOnClickListener(v1 ->
                new AddRemoveDialog(getActivity(), Client.activeReport.getTags(), null, null, this::finishEditTags).show());

        //lets you edit admins
        v.findViewById(R.id.admin_reportdetails_button_editassignees).setOnClickListener(v1 ->
                net.lookupGroup(getContext(), Client.userAttributes.get("custom:groupID"), () ->
                        new CheckboxDialog(getActivity(), Util.getPreChecked(Client.userGroupAdmins, Client.activeReport.getAssignedTo()),
                            Client.userGroupAdmins, this::finishEditAdmins).show()));

        //lets you add notes
        v.findViewById(R.id.admin_reportdetails_button_addnotes).setOnClickListener(v1 ->
                new NotesDialog(getActivity(), "Notes", this::actionUpdateNotes).show());

        //lets you change the status
        View.OnClickListener statusOnClick = v1 -> {
            cvOpen.getBackground().setState(unpushedState);
            cvClosed.getBackground().setState(unpushedState);
            cvResolved.getBackground().setState(unpushedState);
            tvOpen.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            tvClosed.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            tvResolved.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            tvClosed.setTypeface(null, Typeface.NORMAL);
            tvOpen.setTypeface(null, Typeface.NORMAL);
            tvResolved.setTypeface(null, Typeface.NORMAL);

            TextView clickedButtonText;
            String x;
            if(v1 == cvOpen) {
                clickedButtonText = tvOpen;
                if(Client.activeReport.getAssignedTo().size() == 0)
                    x = "Open";
                else
                    x = "Assigned";
            }
            else if (v1 == cvClosed) {
                clickedButtonText = tvClosed;
                x = "Closed";
            }
            else {
                clickedButtonText = tvResolved;
                x = "Resolved";
            }

            v1.getBackground().setState(pushedState);
            clickedButtonText.setTypeface(null, Typeface.BOLD);
            clickedButtonText.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            tvStatus.setText(x);
            Client.activeReport.setStatus(x);
            actionUpdateReport();
        };

        //Listeners for Report Status Change
        v.findViewById(R.id.cardviewOpen).setOnClickListener(statusOnClick);
        v.findViewById(R.id.cardviewClosed).setOnClickListener(statusOnClick);
        v.findViewById(R.id.cardviewResolved).setOnClickListener(statusOnClick);

        populateReportDetails(Client.activeReport);
        actionUpdateInitialButton();
        return v;
    }

    private void populateReportDetails(Report r) {
        System.out.println("Log size" + r.getLogs().size());
        tvReportId.setText(String.format("%s",r.getReportID()));
        tvCreateTimestamp.setText(Util.formatTimestamp(r.getCreationDate()));
        tvLastActionTimestamp.setText(Util.formatTimestamp(r.getLogs().get(r.getLogs().size() - 1).getMessageTimestamp()));
        tvStatus.setText(r.getStatus());
        tvIncidentTimestamp.setText(Util.formatTimestamp(r.getIncidentDate()));

        String threatString = r.getThreat() + "/5";
        tvThreat.setText(threatString);

        tvDescription.setText(r.getDescription());
        tvLocation.setText(r.getLocation());

        catTainer.removeAllViews();
        for(String cat : r.getCategories()){
            @SuppressLint("InflateParams")
            View v = getLayoutInflater().inflate(R.layout.adapter_category, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(cat);
            catTainer.addView(v);
        }
        tagTainer.removeAllViews();
        for(String tag : r.getTags()){
            @SuppressLint("InflateParams")
            View v = getLayoutInflater().inflate(R.layout.adapter_tag, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_tag)).setText(tag);
            tagTainer.addView(v);
        }

        System.out.println("Post Update" + r.getAssignedTo());
        if(r.getAssignedTo().size() > 0)
            tvAdmins.setText(Util.formatStringFromList(r.getAssignedTo()));
        else
            tvAdmins.setText(R.string.none);

        notesList.clear();
        notesList.addAll(Client.activeReport.getNotes());
        adapter.notifyDataSetChanged();

        //if there is no log then show message
        if (notesList.size() == 0)
            tvNoNotes.setVisibility(View.VISIBLE);
        else
            tvNoNotes.setVisibility(View.GONE);
    }

    private void finishEditCategories(List<String> newList) {
        Client.activeReport.setCategories(newList);
        actionUpdateReport();
    }

    private void finishEditTags(List<String> newList) {
        Client.activeReport.setTags(newList);
        actionUpdateReport();
    }

    private void finishEditAdmins(List<String> newList) {
        Client.activeReport.setAssignedTo(newList);
        System.out.println("Active: " + Client.activeReport.getAssignedTo());
        actionUpdateReport();
    }

    private void actionUpdateNotes(String note) {
        Message newNote = new Message();
        newNote.setMessageBody(note);
        Client.activeReport.getNotes().add(newNote);
        actionUpdateReport();
    }

    private void actionUpdateReport() {
        WaitDialog d = new WaitDialog(getContext());
        d.show();
        net.syncActiveReport(getContext(), ()->{
            d.dismiss();
            populateReportDetails(Client.activeReport);
        });
    }

    private void actionUpdateInitialButton(){
        if(tvStatus.getText().toString().equals("Open")){
            cvOpen.getBackground().setState(pushedState);
            tvOpen.setTypeface(null, Typeface.BOLD);
            tvOpen.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        if(tvStatus.getText().toString().equals("Closed")){
            cvClosed.getBackground().setState(pushedState);
            tvClosed.setTypeface(null, Typeface.BOLD);
            tvClosed.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
        if(tvStatus.getText().toString().equals("Resolved")){
            cvResolved.getBackground().setState(pushedState);
            tvResolved.setTypeface(null, Typeface.BOLD);
            tvResolved.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        }
    }
}