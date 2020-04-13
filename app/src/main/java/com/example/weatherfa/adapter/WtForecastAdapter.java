package com.example.weatherfa.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherfa.R;
import com.example.weatherfa.wtclass.WtForecast;

import java.util.List;

public class WtForecastAdapter extends RecyclerView.Adapter<WtForecastAdapter.ViewHolder>{
    private List<WtForecast> mWtForestList;
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fWeek;
        TextView fDate;
        ImageView fIcon;
        TextView fType;
        TextView fMaxTemp;
        TextView fMinTemp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fWeek=(TextView)itemView.findViewById(R.id.fore_item_week_tv);
            fDate=(TextView)itemView.findViewById(R.id.fore_item_date_tv);
            fIcon=(ImageView)itemView.findViewById(R.id.fore_item_icon_iv);
            fType=(TextView)itemView.findViewById(R.id.fore_item_type_tv);
            fMaxTemp=(TextView)itemView.findViewById(R.id.fore_item_maxtemp_tv);
            fMinTemp=(TextView)itemView.findViewById(R.id.fore_item_mintemp_tv);
        }
    }
    public WtForecastAdapter(List<WtForecast> forecastList){
        mWtForestList=forecastList;
    }
    @NonNull
    @Override
    public WtForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wt_forecast_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WtForecastAdapter.ViewHolder holder, int position) {
        WtForecast wtForecast=mWtForestList.get(position);
        holder.fWeek.setText(wtForecast.getWeek());
        holder.fDate.setText(wtForecast.getDate());
        holder.fIcon.setImageResource(wtForecast.getIcon());
    //    holder.fIcon.setColorFilter(Integer.parseInt("#000000"));
        holder.fType.setText(wtForecast.getType());
        holder.fMaxTemp.setText(wtForecast.getMaxTemp());
        holder.fMinTemp.setText(wtForecast.getMinTemp());
    }

    @Override
    public int getItemCount() {
        return mWtForestList.size();
    }
}
