package com.ua.cs495f2018.berthaIRT.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.FirebaseNet;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Report;
import com.ua.cs495f2018.berthaIRT.adapter.ReportCardAdapter;
import com.ua.cs495f2018.berthaIRT.dialog.FilterDialog;

import java.util.ArrayList;
import java.util.List;

public class AdminReportCardsFragment extends Fragment {
    RecyclerView rv;
    ReportCardAdapter adapter;
    TextView tvNoReports;
    ImageView ivSearch;
    EditText etSearch;
    FilterDialog filterDialog;
    List<Report> filterData;

    public AdminReportCardsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater flater, ViewGroup tainer, Bundle savedInstanceState) {
        View v = flater.inflate(R.layout.fragment_admin_reportcards, tainer, false);

        adapter = new ReportCardAdapter(getContext());

        rv = v.findViewById(R.id.admin_reports_rv);
        rv.setAdapter(adapter);

        tvNoReports = v.findViewById(R.id.admin_reports_alt_noreports);

        //Set the Image search button and edit logText for it.
        ivSearch = v.findViewById(R.id.admin_search_icon_iv);
        etSearch = v.findViewById(R.id.admin_reports_input_searchbox);

        filterData = adapter.getData();

        //todo: this will all have to be redone upon an update to the report
        //create a filter dialog for use later
        filterDialog = new FilterDialog(getContext(), filteredReports-> {
            adapter.updateReports(filteredReports, this::updateView);
            filterData = filteredReports;
        });

        //if you hit filter
        v.findViewById(R.id.admin_reports_button_filter).setOnClickListener(x->actionShowFilters());


        //if you search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etSearch.getText().toString().isEmpty())
                    adapter.updateReports(filterData, ()-> updateView());
                else
                    adapter.updateReports(Client.reportMap.values(), ()->{});
            }
        });

        //Begin Search when Icon is Clicked
        ivSearch.setOnClickListener(v1 -> {
            String searchText = etSearch.getText().toString();
            List<Report> reportList = adapter.getData();
            List<Report> searchedList = new ArrayList<>();
            boolean check;
            for(int i = 0; i < reportList.size(); i++){
                //reset check
                check = false;
                //Search ReportIds
                if(reportList.get(i).getReportID().toString().contains(searchText)) {
                    searchedList.add(reportList.get(i));
                    continue;
                }
                //Search Categories
                for(int j = 0; j < reportList.get(i).getCategories().size(); j++){
                    if((reportList.get(i).getCategories().get(j).toLowerCase()).contains(searchText.toLowerCase())) {
                        searchedList.add(reportList.get(i));
                        check = true;
                        break;
                    }
                }
                //Check if item was added to list from Categories
                if(check)
                    continue;
                //Search Tags
                for(int k = 0; k < reportList.get(i).getTags().size(); k++){
                    if((reportList.get(i).getTags().get(k).toLowerCase()).contains(searchText.toLowerCase())) {
                        searchedList.add(reportList.get(i));
                        break;
                    }
                }
            }
            //Update The Report Display with User Searched Reports.
            adapter.updateReports(searchedList, this::updateView);
        });

        return v;
    }

    //show the filter dialog
    private void actionShowFilters() {
        filterDialog.resetUnfilteredList(Client.reportMap.values());
        filterDialog.show();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(adapter == null)
            return;
        adapter.updateReports(Client.reportMap.values(), this::updateView);
        FirebaseNet.setOnRefreshHandler((r)->
                adapter.updateReports(Client.reportMap.values(), this::updateView));
    }

    private void updateView() {
        if(adapter.getItemCount() == 0)
            tvNoReports.setVisibility(View.VISIBLE);
        else
            tvNoReports.setVisibility(View.GONE);
    }
}