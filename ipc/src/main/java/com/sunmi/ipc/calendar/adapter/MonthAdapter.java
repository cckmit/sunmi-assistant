package com.sunmi.ipc.calendar.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.calendar.CalendarInfo;
import com.sunmi.ipc.calendar.Config;
import com.sunmi.ipc.calendar.VerticalCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by maning on 2017/5/9.
 */

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MyViewHolder> {

    private final VerticalCalendar calendarView;
    private List<ArrayList<CalendarInfo>> mDatas;

    private Config config;

    public MonthAdapter(List<ArrayList<CalendarInfo>> data, Config config, VerticalCalendar calendar) {
        this.mDatas = data;
        this.config = config;
        this.calendarView = calendar;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.calendar_item_month, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();

        //标题
        ArrayList<CalendarInfo> model = mDatas.get(position);
        CalendarInfo month = model.get(model.size() - 1);

        //设置标题的格式
        holder.tv_item_title.setText(DateUtils.formatDateTime(context, month.date,
                DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY));

        //日期数据
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 7);
        holder.recyclerViewItem.setLayoutManager(gridLayoutManager);

        //初始化Adapter
        DateAdapter dateAdapter = new DateAdapter(model, this);
        holder.recyclerViewItem.setAdapter(dateAdapter);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public Calendar getSelected() {
        return calendarView.getSelected();
    }

    public boolean setSelected(long date) {
        boolean result = calendarView.setSelected(date);
        if (result) {
            notifyDataSetChanged();
        }
        return result;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_title;
        private RecyclerView recyclerViewItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            recyclerViewItem = itemView.findViewById(R.id.recyclerViewItem);
        }
    }

    public void updateDatas(List<ArrayList<CalendarInfo>> data, Config config) {
        this.mDatas = data;
        this.config = config;
        notifyDataSetChanged();
    }

}
