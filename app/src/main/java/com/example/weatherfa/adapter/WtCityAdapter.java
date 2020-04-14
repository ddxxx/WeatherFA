package com.example.weatherfa.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherfa.R;
import com.example.weatherfa.wtclass.WtCity;
import java.util.List;

public class WtCityAdapter extends RecyclerView.Adapter<WtCityAdapter.ViewHolder>
implements View.OnClickListener{
    private List<WtCity> mWtCityList;//数据源
    private Context mContext;//上下文

    public WtCityAdapter(List<WtCity> wtCityList, Context context){
        this.mWtCityList = wtCityList;
        this.mContext=context;
    }

    @NonNull
    @Override
    public WtCityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_manager_item,parent,false);
        final ViewHolder holder= new ViewHolder(view);

        return holder;
    }

    //绑定
    @Override
    public void onBindViewHolder(@NonNull WtCityAdapter.ViewHolder holder, int position) {
        WtCity wtCity = mWtCityList.get(position);
        holder.cName.setText(wtCity.getName());

        holder.cView.setTag(position);
        holder.cDel.setTag(position);
    }
    //有多少个item
    @Override
    public int getItemCount() {
        return mWtCityList.size();
    }

    //创建viewhodler继承
    class ViewHolder extends RecyclerView.ViewHolder {
        View cView;
        TextView cName;//---响应
        Button cDel;
      //  private OnRecyclerItemClickListener mOnRecyclerItemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cView=itemView;//---响应
            cDel= itemView.findViewById(R.id.city_item_del_bt);
            cName= itemView.findViewById(R.id.city_item_name_tv);

            //为ItemView添加点击事件
            itemView.setOnClickListener(WtCityAdapter.this);
            cDel.setOnClickListener(WtCityAdapter.this);
        }
    }
//==================item中的button的点击事件处理
    public enum ViewName {
        ITEM,
        PRACTISE
    }
    //自定义回调接口
    public interface OnItemClickListener{
        void onItemClick(View v,ViewName viewName,int position);
        void onItemLongClick(View v);
    }
    private OnItemClickListener mOnItemClickListener;//声明自定义的接口
    //定义方法并传给外部使用者
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }

    @Override
    public void onClick(View v) {
        int position=(int)v.getTag();
        if(mOnItemClickListener!=null){
            switch (v.getId()){
                case R.id.city_item_del_bt:
                    mOnItemClickListener.onItemClick(v,ViewName.PRACTISE,position);
                    break;
                default:
                    mOnItemClickListener.onItemClick(v,ViewName.ITEM,position);
                    break;
            }
        }
    }

    //添加item
    public void addData(WtCity city){
        //在list中添加
        if(mWtCityList.contains(city)){
            delDate(city);
        }
        mWtCityList.add(mWtCityList.size(),city);
        notifyItemChanged(mWtCityList.size());//添加动画
        notifyDataSetChanged();
    }

    //删除item
    public void delDate(WtCity city){
        if(mWtCityList.contains(city)) {
            mWtCityList.remove(city);
            notifyDataSetChanged();
        }
    }
    public void delDate(int position){
        if(mWtCityList.size()>position) {
            mWtCityList.remove(position);
            notifyDataSetChanged();
        }
    }
}
