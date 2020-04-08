package com.example.weatherfa.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherfa.MainActivity;
import com.example.weatherfa.R;
import com.example.weatherfa.wtclass.WtCity;

import java.util.List;

public class WtCityAdapter extends RecyclerView.Adapter<WtCityAdapter.ViewHolder> {
    private List<WtCity> mWtCityList;
    private Context mContext;//---------跳转
    static class ViewHolder extends RecyclerView.ViewHolder {
        View cView;
        TextView cName;//---响应
        Button cDel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cView=itemView;//---响应
            cDel=(Button)itemView.findViewById(R.id.city_item_del_bt);
            cName=(TextView)itemView.findViewById(R.id.city_item_name_tv);
        }
    }
    public WtCityAdapter(List<WtCity> wtCityList, Context context){//---------跳转
        mWtCityList = wtCityList;
        this.mContext=context;//---------跳转
    }
    @NonNull
    @Override
    public WtCityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_manager_item,parent,false);
        final ViewHolder holder= new ViewHolder(view);
        //----响应
        holder.cView.setOnClickListener(new View.OnClickListener() {//跳转到天气
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                WtCity wtCity=mWtCityList.get(position);
                //跳转
                Intent intent=new Intent(mContext, MainActivity.class);
                intent.putExtra("fragment_id",0);
                mContext.startActivity(intent);

                Toast.makeText(v.getContext(),"you clicked 一行"+wtCity.getName(),
                        Toast.LENGTH_SHORT).show();

            }
        });
        holder.cDel.setOnClickListener(new View.OnClickListener() {//删除城市
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                //del
                mWtCityList.remove(position);
                notifyDataSetChanged();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WtCityAdapter.ViewHolder holder, int position) {
        WtCity wtCity = mWtCityList.get(position);
        holder.cName.setText(wtCity.getName());
    }

    @Override
    public int getItemCount() {
        return mWtCityList.size();
    }



}
