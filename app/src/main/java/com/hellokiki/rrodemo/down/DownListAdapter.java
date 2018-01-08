package com.hellokiki.rrodemo.down;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hellokiki.rrodemo.R;
import com.hellokiki.rrorequest.down.DownInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 黄麒羽 on 2017/12/25.
 */

public class DownListAdapter extends RecyclerView.Adapter<DownListAdapter.MyViewHolder>{

    private LayoutInflater mInflater;
    private List<DownInfo> mDownInfo;

    public DownListAdapter(Context context, List<DownInfo> infos) {
        mInflater=LayoutInflater.from(context);
        if(infos==null){
            mDownInfo=new ArrayList<>();
        }else{
            mDownInfo=infos;
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder=new MyViewHolder(mInflater.inflate(R.layout.layout_down_item,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.downItemView.setDownInfo(mDownInfo.get(position));
    }

    @Override
    public int getItemCount() {
        return mDownInfo.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        DownItemView downItemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            downItemView= (DownItemView) itemView.findViewById(R.id.down_item_view);
        }
    }

}
