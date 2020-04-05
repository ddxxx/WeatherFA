package com.example.weatherfa.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherfa.R;
import com.example.weatherfa.wtclass.WtLifeIndex;

import java.util.List;

public class WtLifeIndexAdapter extends RecyclerView.Adapter<WtLifeIndexAdapter.ViewHolder> {
    private List<WtLifeIndex> mWtLifeList;
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView lIcon;
        TextView lValue;
        TextView lType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lIcon=(ImageView)itemView.findViewById(R.id.life_item_icon_iv);
            lValue=(TextView)itemView.findViewById(R.id.life_item_value_tv);
            lType=(TextView)itemView.findViewById(R.id.life_item_type_tv);
        }
    }
    public WtLifeIndexAdapter(List<WtLifeIndex> wtLifeIndexList){
        mWtLifeList=wtLifeIndexList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wt_lifeindex_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WtLifeIndexAdapter.ViewHolder holder, int position) {
        WtLifeIndex wtLifeIndex=mWtLifeList.get(position);
        holder.lIcon.setImageResource(wtLifeIndex.getIcon());
        holder.lValue.setText(wtLifeIndex.getValue());
        holder.lType.setText(wtLifeIndex.getType());
    }

    @Override
    public int getItemCount() {
        return mWtLifeList.size();
    }

}
