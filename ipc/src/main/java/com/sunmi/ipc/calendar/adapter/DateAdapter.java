package com.sunmi.ipc.calendar.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.calendar.CalendarInfo;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by maning on 2017/5/9.
 */

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {


    private ArrayList<CalendarInfo> data;

    private MonthAdapter adapter;

    private Calendar temp = Calendar.getInstance();

    public DateAdapter(ArrayList<CalendarInfo> data, MonthAdapter adapter) {
        this.data = data;
        this.adapter = adapter;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.calendar_item_date, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CalendarInfo model = data.get(position);
        if (model.date == 0) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }

        temp.setTimeInMillis(model.date);

        holder.itemView.setVisibility(View.VISIBLE);
        holder.tvDay.setText(String.valueOf(temp.get(Calendar.DATE)));
        holder.ivPoint.setVisibility(model.point ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setEnabled(model.enable);
        holder.tvDay.setEnabled(model.enable);

        holder.tvDay.setSelected(adapter.getSelected() != null
                && model.date == adapter.getSelected().getTimeInMillis());

        holder.itemView.setOnClickListener(view -> {
            boolean result = adapter.setSelected(model.date);
            if (result) {
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDay;
        private ImageView ivPoint;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            ivPoint = itemView.findViewById(R.id.ivPoint);
        }

    }

}
