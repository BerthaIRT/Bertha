package com.ua.cs495f2018.berthaIRT;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    private LogAdapter adapter;
    List<Message> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        RecyclerView rv = findViewById(R.id.log_recycler_view);

        adapter = new LogAdapter(logList);
        rv.setAdapter(adapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        populateReportLog(Client.activeReport.getLogs());
    }

    private void populateReportLog(List<Message> l) {
        logList.clear();

/*        //adds all the reports logs in reverse order
        Collections.reverse(l);*/
        logList.addAll(l);

/*        //reverse it again
        Collections.reverse(l);*/
        adapter.notifyDataSetChanged();

        //if there is no log then show message
        if (logList.size() == 0)
            findViewById(R.id.log_no_log).setVisibility(View.VISIBLE);
    }

    class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

        private List<Message> reportLogs;

        LogAdapter(List<Message> reportLogs) {
            this.reportLogs = reportLogs;
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LogViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_log, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            Message reportLog = reportLogs.get(position);
            holder.logTimestamp.setText(Util.formatTimestamp(reportLog.getMessageTimestamp()));
            holder.logText.setText(reportLog.getMessageBody());
            holder.logBy.setText(reportLog.getMessageSubject());

            //if you click on the log
            holder.cardView.setOnClickListener(v -> {
                //handle displaying the time and user
                if (holder.layoutTop.getVisibility() == View.GONE)
                    holder.layoutTop.setVisibility(View.VISIBLE);
                else
                    holder.layoutTop.setVisibility(View.GONE);

                //If the sender is null make it invisible
                if(holder.logBy.getText().equals(""))
                    holder.logBy.setVisibility(View.GONE);
                else
                    holder.logBy.setVisibility(View.VISIBLE);
            });
        }

        @Override
        public int getItemCount() {
            return reportLogs.size();
        }

        class LogViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            ConstraintLayout layoutTop;

            TextView logText, logTimestamp, logBy;

            LogViewHolder(View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.log_cardview);

                layoutTop = itemView.findViewById(R.id.log_layout_top);
                logTimestamp = itemView.findViewById(R.id.log_alt_timestamp);
                logBy = itemView.findViewById(R.id.log_alt_by);

                logText =  itemView.findViewById(R.id.log_alt_text);
            }
        }
    }
}