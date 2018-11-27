package com.universityofalabama.cs495f2018.berthaIRT.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.universityofalabama.cs495f2018.berthaIRT.AdminReportDetailsActivity;
import com.universityofalabama.cs495f2018.berthaIRT.Client;
import com.universityofalabama.cs495f2018.berthaIRT.Message;
import com.universityofalabama.cs495f2018.berthaIRT.R;
import com.universityofalabama.cs495f2018.berthaIRT.Report;
import com.universityofalabama.cs495f2018.berthaIRT.StudentReportDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class AlertCardAdapter extends RecyclerView.Adapter<AlertCardAdapter.AlertViewHolder>{
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private Context ctx;
    private List<Message> data;

    public AlertCardAdapter(Context c){
        ctx = c;
        data = new ArrayList<>();
    }

    public void updateAlerts(List<Message> c){
        data = new ArrayList<>(c);
        notifyDataSetChanged();
    }


    public void removeAlert(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlertViewHolder(LayoutInflater.from(ctx).inflate(R.layout.adapter_alertcard, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        Message a = data.get(position);
        viewBinderHelper.bind(holder.srl, a.getMessageTimestamp().toString());

        holder.srl.setSwipeListener(new SwipeRevealLayout.SimpleSwipeListener() {
            @Override
            public void onOpened(SwipeRevealLayout view){
                removeAlert(holder.getAdapterPosition());
            }
        });

        holder.catTainer.removeAllViews();

        Report r = Client.reportMap.get(a.getReportID());

        for(String cat : r.getCategories()) {
            @SuppressLint("InflateParams") View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_category, null, false);
            ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(cat);
            holder.catTainer.addView(v);
        }
        holder.tvAction.setText(a.getMessageBody());
        holder.tvReportID.setText(a.getReportID().toString());
        holder.tvTimeSince.setText(calculateTimeSince(a.getMessageTimestamp()));

        holder.cardContainer.setOnClickListener(v -> {
            //get the report clicked on
            Client.activeReport = Client.reportMap.get(a.getReportID());
            //if the parent activity is AdminMain
            if(ctx.getClass().getSimpleName().equals("AdminMainActivity"))
                ctx.startActivity(new Intent(ctx, AdminReportDetailsActivity.class));
            else
                ctx.startActivity(new Intent(ctx, StudentReportDetailsActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AlertViewHolder extends RecyclerView.ViewHolder {
        LinearLayout catTainer;
        CardView cardContainer;
        TextView tvTimeSince, tvReportID, tvAction, tvStatus;
        SwipeRevealLayout srl;

        AlertViewHolder(View itemView) {
            super(itemView);
            srl = itemView.findViewById(R.id.alertcard_srl);
            catTainer = itemView.findViewById(R.id.alertcard_container_categories);
            cardContainer = itemView.findViewById(R.id.alertcard_cv);
            tvTimeSince = itemView.findViewById(R.id.alertcard_alt_timesince);
            tvReportID = itemView.findViewById(R.id.alertcard_alt_id);
            tvStatus = itemView.findViewById(R.id.alertcard_alt_status);
            tvAction = itemView.findViewById(R.id.alertcard_alt_action);
        }
    }

    public String calculateTimeSince(long last) {
        long diff = System.currentTimeMillis() - last;
        String since;
        //print in seconds
        if(diff < 60000)
            since = (diff/1000) + " SECONDS AGO";
        else if (diff < 3600000)
            since = ((diff/1000) / 60) + " MINUTES AGO";
        else if(diff < 216000000)
            since = (((diff/1000) / 60) / 60) + " HOURS AGO";
        else
            since = ((((diff/1000) / 60) / 60) / 24) + " DAYS AGO";
        return since;
    }
}