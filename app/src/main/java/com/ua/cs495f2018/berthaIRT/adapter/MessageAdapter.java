package com.ua.cs495f2018.berthaIRT.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.Message;
import com.ua.cs495f2018.berthaIRT.R;
import com.ua.cs495f2018.berthaIRT.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context ctx;
    private List<Message> data;


    public MessageAdapter(Context c) {
        ctx = c;
        data = new ArrayList<>();
    }

    public void updateMessages(Collection<Message> c){
        data.clear();
        data.addAll(c);
        notifyDataSetChanged();
    }

    //function to know if the days are different
    private boolean isNewDay(Long a, Long b){
        Calendar ac = Calendar.getInstance();
        Calendar bc = Calendar.getInstance();
        ac.setTimeInMillis(a);
        bc.setTimeInMillis(b);
        return (ac.get(Calendar.DAY_OF_YEAR) != bc.get(Calendar.DAY_OF_YEAR));
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(ctx).inflate(R.layout.adapter_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = data.get(position);

        if(message.getMessageSubject().equals(Client.userAttributes.get("username"))){
            holder.outContainer.setVisibility(View.VISIBLE);
            holder.tvOutTime.setText(Util.formatJustTime(message.getMessageTimestamp()));
            holder.tvOutSub.setText(message.getMessageSubject());
            holder.tvOutBody.setText(message.getMessageBody());
            holder.inContainer.setVisibility(View.GONE);
            if(message.getMessageSubject().startsWith("student"))
                holder.tvOutSub.setText(R.string.you);
        }

        else {
            holder.inContainer.setVisibility(View.VISIBLE);
            holder.tvInTime.setText(Util.formatJustTime(message.getMessageTimestamp()));
            holder.tvInSub.setText(message.getMessageSubject());
            holder.tvInBody.setText(message.getMessageBody());
            holder.outContainer.setVisibility(View.GONE);
            if(message.getMessageSubject().startsWith("student"))
                holder.tvInSub.setText(R.string.hidden);
        }

        Message lastMessage = null;
        try{
            lastMessage = data.get(position-1);
        } catch(IndexOutOfBoundsException ignored){}

        if(lastMessage == null || isNewDay(message.getMessageTimestamp(), lastMessage.getMessageTimestamp())){
            ((TextView) holder.dateDiv.findViewById(R.id.message_alt_datediv)).setText(Util.formatDatestamp(message.getMessageTimestamp()));
            holder.dateDiv.setVisibility(View.VISIBLE);
        }
        else
            holder.dateDiv.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout dateDiv;
        ConstraintLayout inContainer, outContainer;
        TextView tvInTime, tvInSub, tvInBody, tvOutTime, tvOutSub, tvOutBody;

        MessageViewHolder(View itemView) {
            super(itemView);
            inContainer = itemView.findViewById(R.id.message_container_incomming);
            outContainer = itemView.findViewById(R.id.message_container_outgoing);
            dateDiv = itemView.findViewById(R.id.message_container_datediv);
            tvInTime = itemView.findViewById(R.id.message_alt_incoming_time);
            tvInSub = itemView.findViewById(R.id.message_alt_incoming_subject);
            tvInBody = itemView.findViewById(R.id.message_alt_incoming_body);
            tvOutTime = itemView.findViewById(R.id.message_alt_outgoing_time);
            tvOutSub = itemView.findViewById(R.id.message_alt_outgoing_subject);
            tvOutBody = itemView.findViewById(R.id.message_alt_outgoing_body);
        }
    }
}
