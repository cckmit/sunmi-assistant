package com.sunmi.ipc.calendar;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.calendar.adapter.MonthAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by maning on 2017/5/10.
 * 垂直方向的日历
 */

public class VerticalCalendar extends LinearLayout {

    private Context context;

    private RecyclerView recyclerViewCalendar;

    private MonthAdapter mAdapter;
    private List<ArrayList<CalendarInfo>> data;
    private HashSet<Long> points = new HashSet<>();

    private OnCalendarSelectListener mListener;

    private Config config;
    private Calendar selected;

    public VerticalCalendar(Context context) {
        this(context, null);
    }

    public VerticalCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalCalendar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        config = new Config();
        initViews();
        initCalendarDatas();
    }

    private void initViews() {
        //绑定View
        View.inflate(context, R.layout.calendar_view, this);
        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar);

        String[] weekName = getResources().getStringArray(R.array.week_name_abbr);
        int week = Calendar.getInstance().getFirstDayOfWeek() - 1;

        TextView week01 = findViewById(R.id.tv_week_01);
        TextView week02 = findViewById(R.id.tv_week_02);
        TextView week03 = findViewById(R.id.tv_week_03);
        TextView week04 = findViewById(R.id.tv_week_04);
        TextView week05 = findViewById(R.id.tv_week_05);
        TextView week06 = findViewById(R.id.tv_week_06);
        TextView week07 = findViewById(R.id.tv_week_07);

        week01.setText(weekName[week++ % 7]);
        week02.setText(weekName[week++ % 7]);
        week03.setText(weekName[week++ % 7]);
        week04.setText(weekName[week++ % 7]);
        week05.setText(weekName[week++ % 7]);
        week06.setText(weekName[week++ % 7]);
        week07.setText(weekName[week % 7]);

        //初始化RecyclerView
        recyclerViewCalendar.setLayoutManager(new LinearLayoutManager(context));
    }

    private void initCalendarDatas() {
        //日期集合
        data = new ArrayList<>();
        //计算日期
        Calendar min = config.getMinDate();
        Calendar max = config.getMaxDate();
        Calendar month = (Calendar) min.clone();
        month.set(Calendar.DATE, 1);
        while (month.before(max)) {
            int monthIndex = month.get(Calendar.MONTH);
            Calendar date = (Calendar) month.clone();
            ArrayList<CalendarInfo> monthData = new ArrayList<>();
            //获取当月第一天是星期几
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK) - 1;

            for (int i = 0; i < dayOfWeek; i++) {
                monthData.add(new CalendarInfo());
            }
            while (date.get(Calendar.MONTH) == monthIndex) {
                long timestamp = date.getTimeInMillis();
                boolean enable = timestamp >= min.getTimeInMillis()
                        && timestamp <= max.getTimeInMillis();
                monthData.add(new CalendarInfo(timestamp, enable, points.contains(timestamp)));
                date.add(Calendar.DATE, 1);
            }

            data.add(monthData);
            month.add(Calendar.MONTH, 1);
        }

        //设置Adapter
        initAdapter();

    }

    private long calendarToTimestamp(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        c.clear();
        c.set(year, month, date);
        return c.getTimeInMillis();
    }

    private void initAdapter() {
        if (mAdapter == null) {
            mAdapter = new MonthAdapter(data, config, this);
            recyclerViewCalendar.setAdapter(mAdapter);
        } else {
            mAdapter.updateDatas(data, config);
        }
    }

    public void setConfig(Config config) {
        this.config = config;
        points.clear();
        for (Calendar point : config.getPoints()) {
            points.add(calendarToTimestamp(point));
        }
        initCalendarDatas();
    }

    public Calendar getSelected() {
        return selected;
    }

    public boolean setSelected(long selected) {
        if (this.selected != null && this.selected.getTimeInMillis() == selected) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(selected);
        this.selected = c;
        if (this.mListener != null) {
            mListener.onCalendarSelect(c);
        }
        return true;
    }

    public void setOnCalendarSelectListener(OnCalendarSelectListener l) {
        mListener = l;
    }

}
