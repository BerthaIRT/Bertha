package com.ua.cs495f2018.berthaIRT.dialog;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.Interface;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Report;
import com.ua.cs495f2018.berthaIRT.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

@SuppressLint("InflateParams")
public class FilterDialog extends AlertDialog{
    private long filterStartTime;
    private long filterEndTime;
    private List<String> filterStatus;
    private List<String> filterCategories;
    private List<String> filterTags;
    private int originalTagNo;
    private List<String> filterAssignedTo;

    private List<Report> unfilteredList;
    private Interface.WithReportListListener callback;

    private TextView tvAfter, tvBefore;
    private CheckBox cbNew, cbOpen, cbClosed, cbResolved;
    private LinearLayout llCategories, llTags;
    private TextView tvAssignedTo;

    public FilterDialog(Context ctx, Interface.WithReportListListener callback) {
        super(ctx);
        filterStartTime = 0L;
        filterEndTime = Long.MAX_VALUE;
        filterStatus = new ArrayList<>();
        filterCategories = new ArrayList<>();

        filterTags = new ArrayList<>();
        for(Report r : Client.reportMap.values()){
            for(String s : r.getTags())
                if(!filterTags.contains(s))
                    filterTags.add(s);
        }
        originalTagNo = filterTags.size();
        filterAssignedTo = new ArrayList<>();
        filterStatus.add("New");
        filterStatus.add("Open");
        filterStatus.add("Assigned");
        filterStatus.add("Closed");
        filterStatus.add("Resolved");
        this.callback = callback;
    }

    public void resetUnfilteredList(Collection<Report> l){
        this.unfilteredList = new ArrayList<>(l);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);
        Objects.requireNonNull(getWindow()).clearFlags( WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        tvAfter = findViewById(R.id.filteroptions_input_afterdate);
        tvBefore = findViewById(R.id.filteroptions_input_beforedate);
        cbNew = findViewById(R.id.filteroptions_input_new);
        cbOpen = findViewById(R.id.filteroptions_input_openassigned);
        cbClosed = findViewById(R.id.filteroptions_input_closed);
        cbResolved = findViewById(R.id.filteroptions_input_resolved);
        llCategories = findViewById(R.id.filteroptions_container_categories);
        llTags = findViewById(R.id.filteroptions_container_tags);
        tvAssignedTo = findViewById(R.id.filteroptions_alt_assignedto);
        ImageView ivEditCategories = findViewById(R.id.admin_reportdetails_button_editcategory);
        ImageView ivEditTags = findViewById(R.id.filteroptions_button_edittags);
        ImageView ivEditAssignedTo = findViewById(R.id.filteroptions_button_editassignees);
        Button applyFilterBtn = findViewById(R.id.apply_filter_button);
        Button defaultFilterBtn = findViewById(R.id.default_filter_button);

        if(filterStartTime != 0)
            tvAfter.setText(Util.formatDatestamp(filterStartTime));
        if(filterEndTime != Long.MAX_VALUE)
            tvBefore.setText(Util.formatDatestamp(filterEndTime));
        if(filterStatus.size() > 0){
            if(!filterStatus.contains("New")) cbNew.setChecked(false);
            if(!filterStatus.contains("Open")) cbOpen.setChecked(false);
            if(!filterStatus.contains("Closed")) cbClosed.setChecked(false);
            if(!filterStatus.contains("Resolved")) cbResolved.setChecked(false);
        }

        if(filterCategories.size() > 0){
            for(String cat : filterCategories){
                View v = getLayoutInflater().inflate(R.layout.adapter_category, null, false);
                ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(cat);
                llCategories.addView(v);
            }
        }
        else{
            View v = getLayoutInflater().inflate(R.layout.adapter_category, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(R.string.all_categories);
            llCategories.addView(v);
        }
        if(filterTags.size() != originalTagNo){
            for(String cat : filterTags){
                View v = getLayoutInflater().inflate(R.layout.adapter_tag, null, false);
                ((TextView) v.findViewById(R.id.adapter_alt_tag)).setText(cat);
                llTags.addView(v);
            }
        }
        else{
            View v = getLayoutInflater().inflate(R.layout.adapter_tag, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_tag)).setText(R.string.all_tags);
            llTags.addView(v);
        }


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        tvAfter.setOnClickListener(x->{
            DatePickerDialog d = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                tvAfter.setText(date);
                filterStartTime = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
            }, mYear, mMonth, mDay);
            d.getDatePicker().setMaxDate(System.currentTimeMillis());
            d.show();
        });

        tvBefore.setOnClickListener(x->{
            DatePickerDialog d = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                tvBefore.setText(date);
                filterEndTime = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
            }, mYear, mMonth, mDay);
            d.getDatePicker().setMaxDate(System.currentTimeMillis());
            d.show();
        });

        cbNew.setOnClickListener(x->{
            if(cbNew.isChecked()) filterStatus.add("New");
            else filterStatus.remove("New");
        });

        cbOpen.setOnClickListener(x->{
            if(cbOpen.isChecked()){
                filterStatus.add("Open");
                filterStatus.add("Assigned");
            }
            else{
                filterStatus.remove("Open");
                filterStatus.remove("Assigned");
            }
        });

        cbClosed.setOnClickListener(x->{
            if(cbClosed.isChecked()) filterStatus.add("Closed");
            else filterStatus.remove("Closed");
        });

        cbResolved.setOnClickListener(x->{
            if(cbResolved.isChecked()) filterStatus.add("Resolved");
            else filterStatus.remove("Resolved");
        });

        Objects.requireNonNull(ivEditCategories).setOnClickListener(v -> {
            //open categories dialog box to get filterCategories List to how user wants it.
            new CheckboxDialog(v.getContext(), Util.getPreChecked(Arrays.asList(v.getResources().getStringArray(R.array.category_item)),filterCategories),
                    Arrays.asList(v.getResources().getStringArray(R.array.category_item)), this::updateCategories).show();
        });

        Objects.requireNonNull(ivEditTags).setOnClickListener(v ->{
            filterTags = new ArrayList<>();
            for(Report r : Client.reportMap.values()){
                for(String s : r.getTags())
                    if(!filterTags.contains(s))
                        filterTags.add(s);
            }
            new CheckboxDialog(v.getContext(), null, filterTags, (s)->{
                filterTags = new ArrayList<>(s);
            }).show();
        });

        Objects.requireNonNull(ivEditAssignedTo).setOnClickListener(v ->
                    new CheckboxDialog(v.getContext(), Util.getPreChecked(Client.userGroupAdmins, filterAssignedTo),
                            Client.userGroupAdmins, this::updateAssignedTo).show());

        Objects.requireNonNull(defaultFilterBtn).setOnClickListener(v -> {
            List<String> nullStringList = new ArrayList<>();
            updateCategories(nullStringList);
            updateTags(nullStringList);
            updateAssignedTo(nullStringList);
            filterStartTime = 0L;
            tvBefore.setText("");
            tvAfter.setText("");
            filterEndTime = Long.MAX_VALUE;
            cbNew.setChecked(true);
            cbClosed.setChecked(true);
            cbOpen.setChecked(true);
            cbResolved.setChecked(true);

            filterStatus.clear();
            filterStatus.addAll(Arrays.asList("New", "Open", "Assigned", "Closed", "Resolved"));

        });

        Objects.requireNonNull(applyFilterBtn).setOnClickListener(v -> dismiss());
    }

    private void updateCategories(List<String> r){
        filterCategories = r;
        //Update the Linear Layout of Category Icons
        if(filterCategories.size() != 0) {
            //Remove All Views From Linear Layout Categories
            int j = llCategories.getChildCount();
            for (int i = 0; i < j; i++)
                llCategories.removeViewAt(0);
            //Add All Other Category Selections to View.
            for (String cat : filterCategories) {
                View v = getLayoutInflater().inflate(R.layout.adapter_category, null, false);
                ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(cat);
                llCategories.addView(v);
            }
        }
        else{
            //Remove All Views From Linear Layout Categories
            int j = llCategories.getChildCount();
            for (int i = 0; i < j; i++)
                llCategories.removeViewAt(0);
            //Add "All Categories" to View.
            View v = getLayoutInflater().inflate(R.layout.adapter_category, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(R.string.all_categories);
            llCategories.addView(v);
        }
    }

    private void updateTags(List<String> r){
        filterTags = r;
        //Update the Linear Layout of Category Icons
        if(filterTags.size() != 0) {
            //Remove All Views From Linear Layout Categories
            int j = llTags.getChildCount();
            for (int i = 0; i < j; i++)
                llTags.removeViewAt(0);
            //Add All Other Category Selections to View.
            for (String cat : filterTags) {
                View v = getLayoutInflater().inflate(R.layout.adapter_tag, null, false);
                ((TextView) v.findViewById(R.id.adapter_alt_tag)).setText(cat);
                llTags.addView(v);
            }
        }
        else{
            //Remove All Views From Linear Layout Categories
            int j = llTags.getChildCount();
            for (int i = 0; i < j; i++)
                llTags.removeViewAt(0);
            //Add "All Categories" to View.
            View v = getLayoutInflater().inflate(R.layout.adapter_tag, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_tag)).setText(R.string.all_tags);
            llTags.addView(v);
        }
    }

    private void updateAssignedTo(List<String> r){
        filterAssignedTo = r;
        //Update the text of assigned to
        if(filterAssignedTo.size() != 0)
            tvAssignedTo.setText(Util.formatStringFromList(r));
        else
            tvAssignedTo.setText(R.string.any);
    }

    @Override
    public void dismiss(){
        List<Report> filteredList = new ArrayList<>(unfilteredList);
        for(Report r : unfilteredList){
            if(filterStartTime != 0)
                if(r.getCreationDate() < filterStartTime){
                    filteredList.remove(r);
                    continue;
                }
            if(filterEndTime != Long.MAX_VALUE)
                if(r.getCreationDate() > filterEndTime){
                    filteredList.remove(r);
                    continue;
                }
            if(filterStatus.size() < 5)
                if(!filterStatus.contains(r.getStatus())){
                    filteredList.remove(r);
                    continue;
                }
            if(filterCategories.size() > 0) {
                int needContinue = 0;
                for (int i = 0; i < r.getCategories().size(); i++) {
                    if (filterCategories.contains(r.getCategories().get(i)))
                        needContinue = 1;
                }
                if(needContinue == 0){
                    filteredList.remove(r);
                }
            }
            if(filterTags.size() > 0) {
                int needContinue = 0;
                for (int i = 0; i < r.getTags().size(); i++) {
                    if (filterTags.contains(r.getTags().get(i)))
                        needContinue = 1;
                }
                if(needContinue == 0)
                    filteredList.remove(r);
            }
            if(filterAssignedTo.size() > 0) {
                int needContinue = 0;
                for (int i = 0; i < r.getAssignedTo().size(); i++) {
                    if (filterAssignedTo.contains(r.getAssignedTo().get(i)))
                        needContinue = 1;
                }
                if(needContinue == 0)
                    filteredList.remove(r);
            }
        }
        callback.onEvent(filteredList);
        super.dismiss();
    }
}