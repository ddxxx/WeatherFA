package com.example.weatherfa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherfa.R;
import com.example.weatherfa.wtclass.WtHistory;

import java.util.List;

public class WtHistoryAdapter extends RecyclerView.Adapter<WtHistoryAdapter.ViewHolder> {
    private List<WtHistory> mWtHistoryList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        Button hFulls;
        ImageView hIcon;
        TextView hType;
        ViewHolder(View itemView){
            super(itemView);
            hFulls=(Button) itemView.findViewById(R.id.h_item_fulls_bt);
            hIcon=(ImageView)itemView.findViewById(R.id.h_item_icon_iv);
            hType=(TextView) itemView.findViewById(R.id.h_item_type_tv);
        }
    }
    public WtHistoryAdapter(List<WtHistory> wtHistoryList){
        mWtHistoryList=wtHistoryList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wt_history_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WtHistoryAdapter.ViewHolder holder, int position) {
        WtHistory wtHistory=mWtHistoryList.get(position);
        holder.hIcon.setImageResource(wtHistory.getIcon());
        holder.hType.setText(wtHistory.getType());
    }

    @Override
    public int getItemCount() {
        return mWtHistoryList.size();
    }

}
