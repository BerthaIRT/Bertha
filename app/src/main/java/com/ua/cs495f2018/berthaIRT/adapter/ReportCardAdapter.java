package com.ua.cs495f2018.berthaIRT.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.AdminReportDetailsActivity;
import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.Interface;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Report;
import com.ua.cs495f2018.berthaIRT.StudentReportDetailsActivity;
import com.ua.cs495f2018.berthaIRT.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class ReportCardAdapter extends RecyclerView.Adapter<ReportCardAdapter.ReportViewHolder>{
    private Context ctx;
    private List<Report> data;

    public ReportCardAdapter(Context c){
        ctx = c;
        data = new ArrayList<>();
    }

    public void updateReports(Collection<Report> c, Interface.WithVoidListener listener){
        if(c == null)
            c = new ArrayList<>();

        data.clear();
        data.addAll(c);
        notifyDataSetChanged();
        if(listener != null)
            listener.onEvent();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportViewHolder(LayoutInflater.from(ctx).inflate(R.layout.adapter_reportcard, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        holder.tvMetrics.setText(String.format("%s Reports matching criteria", String.valueOf(data.size())));
        if(position == 0)
            holder.tvMetrics.setVisibility(View.VISIBLE);
        else
            holder.tvMetrics.setVisibility(View.GONE);

        Report r = data.get(position);

        holder.tvReportID.setText(String.format("%s",r.getReportID()));
        holder.tvStatus.setText(r.getStatus());
        //set the text color of status appropriately
        switch (holder.tvStatus.getText().toString()) {
            case "New":
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.NewStatus));
                break;
            case "Open":
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.OpenStatus));
                break;
            case "Assigned":
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.AssignedStatus));
                break;
            case "Closed":
                holder.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.ClosedStatus));
                break;
        }
        holder.tvSubmitted.setText(Util.formatTimestamp(r.getCreationDate()));
        holder.catTainer.removeAllViews();

        //to fix the display of number of categories
        Integer spaceLeft = Client.displayWidthDPI - Util.measureViewWidth(holder.tvStatus);
        spaceLeft -= (8 + 8 + 8 + 8 + 8); // margins
        int hidden = 0;
        for(String cat : r.getCategories()) {
            View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_category, holder.catTainer, false);
            ((TextView) v.findViewById(R.id.adapter_alt_category)).setText(cat);
            int spaceTaken = Util.measureViewWidth(v);
            if(spaceLeft < spaceTaken)
                hidden++;
            else {
                holder.catTainer.addView(v);
                spaceLeft -= spaceTaken;
            }
        }
        if(hidden > 0){
            holder.tvExtraCats.setText(String.format(Locale.US,"+%d", hidden));
            holder.tvExtraCats.setVisibility(View.VISIBLE);
        }

        //if you click on the the card it launches the report details
        holder.cardContainer.setOnClickListener(v -> {
            //get the report clicked on
            Client.activeReport = Client.reportMap.get(r.getReportID());
            //if the parent activity is AdminMain vs StudentMain
            if(ctx.getClass().getSimpleName().equals("AdminMainActivity"))
                ctx.startActivity(new Intent(ctx, AdminReportDetailsActivity.class));
            else
                ctx.startActivity(new Intent(ctx, StudentReportDetailsActivity.class));
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        LinearLayout catTainer;
        CardView cardContainer;
        TextView tvReportID, tvSubmitted, tvStatus, tvExtraCats;
        TextView tvMetrics;

        ReportViewHolder(View v) {
            super(v);
            catTainer = v.findViewById(R.id.reportcard_container_categories);
            cardContainer = itemView.findViewById(R.id.reportcard_cv);
            tvReportID = itemView.findViewById(R.id.reportcard_alt_id);
            tvStatus = itemView.findViewById(R.id.reportcard_alt_status);
            tvSubmitted = itemView.findViewById(R.id.reportcard_alt_action);
            tvExtraCats = itemView.findViewById(R.id.reportcard_alt_extracats);

            tvMetrics = itemView.findViewById(R.id.reportcard_alt_metrics);
        }
    }

    public List<Report> getData(){
        return this.data;
    }
}