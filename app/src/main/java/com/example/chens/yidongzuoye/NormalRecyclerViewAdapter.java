package com.example.chens.yidongzuoye;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chens.yidongzuoye.data.PracticeBean;

import java.util.ArrayList;

/**
 * Created by chens on 2017/2/23.
 */
public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> implements View.OnClickListener {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<PracticeBean> practiceBeanList;

    public NormalRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, String.valueOf(v.getTag()));
        }
    }

    /**
     * 定义点击的接口
     * */
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setPracticeBeanList(ArrayList<PracticeBean> practiceBeanList){
        this.practiceBeanList = practiceBeanList;
    }
    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_text, parent, false);
        NormalTextViewHolder viewHolder = new NormalTextViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.tv_name.setText(practiceBeanList.get(position).getPractice_name());
        holder.tv_releaseTime.append(practiceBeanList.get(position).getRelease_time());
        holder.tv_deadline.append(practiceBeanList.get(position).getDeadline());

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return practiceBeanList == null ? 0 : practiceBeanList.size();
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        TextView tv_releaseTime;
        TextView tv_deadline;

        NormalTextViewHolder(View view) {
            super(view);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_releaseTime = (TextView) view.findViewById(R.id.tv_release_time);
            tv_deadline = (TextView) view.findViewById(R.id.tv_deadline);
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    /**
//                     * 这里写点击事件
//                     * */
//                    Intent intent = new Intent();
//                    intent.putExtra("",practiceBeanList.get(getLayoutPosition()).getPractice_id());
//                    Log.d("info", "onClick--> position = " + getPosition());
//                }
//            });
        }
    }
}
