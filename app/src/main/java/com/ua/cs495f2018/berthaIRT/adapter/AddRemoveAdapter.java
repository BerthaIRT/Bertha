package com.ua.cs495f2018.berthaIRT.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ua.cs495f2018.berthaIRT.Client;
import com.ua.cs495f2018.berthaIRT.Interface;
import com.ua.cs495f2018.berthaIRT.R;

import java.util.ArrayList;
import java.util.List;

public class AddRemoveAdapter extends RecyclerView.Adapter<AddRemoveAdapter.AddRemoveViewHolder>{
    private List<String> dataList;
    private Interface.WithStringListener removeListener;
    private Context ctx;

    public AddRemoveAdapter(Context c, List<String> l, Interface.WithStringListener listener){
        ctx = c;
        dataList = l;
        removeListener = listener;
    }

    public List<String> getList(){
        return new ArrayList<>(dataList);
    }

    public void addToList(String s){
        dataList.add(s);
        notifyItemInserted(dataList.size()-1);
    }

    @NonNull
    @Override
    public AddRemoveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.adapter_addremove, parent, false);
        return new AddRemoveViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AddRemoveViewHolder holder, int position) {
        holder.tv.setText(dataList.get(position));

        //don't let you delete yourself
        if(holder.tv.getText().toString().equals(Client.userAttributes.get("username")))
            holder.bRemove.setVisibility(View.INVISIBLE);
        else {
            holder.bRemove.setVisibility(View.VISIBLE);
            //clicker for removing things
            holder.bRemove.setOnClickListener(l -> {
                //if there is a listener for remove then return the string of what's removed
                if (removeListener != null)
                    removeListener.onEvent(dataList.get(position));
                dataList.remove(position);
                notifyItemRemoved(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class AddRemoveViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView bRemove;

        AddRemoveViewHolder(View itemView){
            super(itemView);
            tv = itemView.findViewById(R.id.addremove_alt_text);
            bRemove = itemView.findViewById(R.id.addremove_button_delete);
        }
    }
}