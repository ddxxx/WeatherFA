package com.example.weatherfa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherfa.R;
import com.example.weatherfa.wtclass.WtDetail;

import java.util.List;

public class WtDetailAdapter extends RecyclerView.Adapter<WtDetailAdapter.ViewHolder> {
    private List<WtDetail> mWtDetailList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView dIcon;
        TextView dType;
        TextView dValue;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dIcon=(ImageView)itemView.findViewById(R.id.detail_item_icon_iv);
            dType=(TextView)itemView.findViewById(R.id.detail_item_type_tv);
            dValue=(TextView)itemView.findViewById(R.id.detail_item_value_tv);
        }
    }
    public WtDetailAdapter(List<WtDetail> wtDetailList){
        mWtDetailList=wtDetailList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wt_detail_item,parent,false);
        ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WtDetail wtDetail=mWtDetailList.get(position);
        holder.dIcon.setImageResource(wtDetail.getIcon());
        holder.dType.setText(wtDetail.getKey());
        holder.dValue.setText(wtDetail.getValue());
    }

    @Override
    public int getItemCount() {
        return mWtDetailList.size();
    }

}
