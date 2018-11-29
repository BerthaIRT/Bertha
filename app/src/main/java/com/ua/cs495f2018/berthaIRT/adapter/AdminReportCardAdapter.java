package com.ua.cs495f2018.berthaIRT.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.AdminReportDetailsActivity;
import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Report;
import com.ua.cs495f2018.berthaIRT.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdminReportCardAdapter extends RecyclerView.Adapter<AdminReportCardAdapter.ReportViewHolder>{
    private Context ctx;
    private List<Report> data;

    public AdminReportCardAdapter(Context c){
        ctx = c;
        data = new ArrayList<>();
    }

    public void updateReports(Collection<Report> c){
        if(c == null)
            c = new ArrayList<>();

        data = new ArrayList<>(c);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_reportcard, parent, false);
        return new ReportViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report r = data.get(position);


        holder.tvReportID.setText(r.getReportID().toString());
        holder.tvStatus.setText(r.getStatus());
        holder.tvSubmitted.setText(Util.formatTimestamp(r.getCreationDate()));
        holder.catTainer.removeAllViews();

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
            holder.tvExtraCats.setText(String.format("+%d", hidden));
            holder.tvExtraCats.setVisibility(View.VISIBLE);
        }

        holder.cardContainer.setOnClickListener(v -> {
            //get the report clicked on
            Client.activeReport = data.get(holder.getAdapterPosition());
            ctx.startActivity(new Intent(ctx, AdminReportDetailsActivity.class));
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    class ReportViewHolder extends RecyclerView.ViewHolder {
        LinearLayout catTainer;
        CardView cardContainer;
        TextView tvReportID, tvSubmitted, tvStatus, tvExtraCats;

        ReportViewHolder(View v) {
            super(v);
            catTainer = v.findViewById(R.id.reportcard_container_categories);
            cardContainer = itemView.findViewById(R.id.reportcard_cv);
            tvReportID = itemView.findViewById(R.id.reportcard_alt_id);
            tvStatus = itemView.findViewById(R.id.reportcard_alt_status);
            tvSubmitted = itemView.findViewById(R.id.reportcard_alt_action);
            tvExtraCats = itemView.findViewById(R.id.reportcard_alt_extracats);
        }
    }

    public List<Report> getData(){
        return this.data;
    }
}