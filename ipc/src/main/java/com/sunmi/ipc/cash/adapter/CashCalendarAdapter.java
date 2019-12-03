package com.sunmi.ipc.cash.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunmi.ipc.R;

import java.util.Calendar;
import java.util.List;

import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-03.
 */
public class CashCalendarAdapter extends RecyclerView.Adapter<CashCalendarAdapter.ViewHolder> {

    private Context context;
    private List<Calendar> calendars;
    private int selectPosition = 14;
    private OnItemClickListener listener;
    private String[] sWeekName;

    public CashCalendarAdapter(Context context, List<Calendar> calendars) {
        this.context = context;
        this.calendars = calendars;
    }

    public interface OnItemClickListener {
        void onClick(Calendar calendar, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cash_calendar,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.itemView.setSelected(i == selectPosition);
        Calendar c = calendars.get(i);
        String date;
        if (i == selectPosition) {
            date = DateTimeUtils.calendarToDate(c, "MM.dd");
        } else {
            date = DateTimeUtils.calendarToDate(c, "dd");
        }
        viewHolder.tvDate.setText(date);
        int timeIndex = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (timeIndex <= 0) {
            timeIndex += 7;
        }
        viewHolder.tvWeek.setText(getWeekName(timeIndex));
    }

    @Override
    public int getItemCount() {
        return calendars.size();
    }

    private String getWeekName(int timeIndex) {
        if (sWeekName == null) {
            sWeekName = context.getResources().getStringArray(R.array.week_name);
        }
        return sWeekName[timeIndex - 1];
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDate;
        TextView tvWeek;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvWeek = itemView.findViewById(R.id.tv_week);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                selectPosition = getAdapterPosition();
                listener.onClick(calendars.get(selectPosition), selectPosition);
                notifyDataSetChanged();
            }
        }
    }
}
