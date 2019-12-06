package com.sunmi.ipc.cash.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.view.View;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.DropdownTime;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sunmi.common.view.DropdownMenuNew;
import sunmi.common.view.loopview.LoopView;

/**
 * @author yinhui
 * @date 2019-12-05
 */
public class CashDropdownTimeAdapter extends DropdownMenuNew.Adapter<DropdownTime> {

    public static final int STATE_SUCCESS = 0;
    public static final int STATE_START_EMPTY = 1;
    public static final int STATE_END_EMPTY = 2;
    public static final int STATE_BOTH_EMPTY = 3;
    public static final int STATE_RANGE_ERROR = 4;

    private static final int MINUTE_PER_HOUR = 60;
    private static final int HOUR_PER_DAY = 24;
    private static final int MINUTE_PER_DAY = HOUR_PER_DAY * MINUTE_PER_HOUR;

    private Group groupCustomTime;
    private TextView tvCustomTimeStart;
    private TextView tvCustomTimeEnd;
    private LoopView lvWheelHour;
    private LoopView lvWheelMinute;

    private boolean isTargetCustomStart = true;
    private boolean customStartReady = true;
    private boolean customEndReady = false;
    private int timeStartMinutes;
    private int timeEndMinutes;
    private Calendar temp = Calendar.getInstance();

    public CashDropdownTimeAdapter(Context context) {
        super(context, R.layout.dropdown_title_new, R.layout.cash_video_dropdown_time_item);
    }

    public int checkAndUpdateTime(DropdownTime model, long timestamp) {
        if (!customStartReady && !customEndReady) {
            return STATE_BOTH_EMPTY;
        } else if (!customStartReady) {
            return STATE_START_EMPTY;
        } else if (!customEndReady) {
            return STATE_END_EMPTY;
        } else if (timeStartMinutes > timeEndMinutes) {
            return STATE_RANGE_ERROR;
        }
        temp.setTimeInMillis(timestamp);
        int year = temp.get(Calendar.YEAR);
        int month = temp.get(Calendar.MONTH);
        int date = temp.get(Calendar.DATE);
        temp.clear();
        temp.set(year, month, date, timeStartMinutes / MINUTE_PER_HOUR,
                timeStartMinutes % MINUTE_PER_HOUR);
        long start = temp.getTimeInMillis();
        temp.set(year, month, date, timeEndMinutes / MINUTE_PER_HOUR,
                timeEndMinutes % MINUTE_PER_HOUR);
        long end = temp.getTimeInMillis();
        model.setTime(start, end);
        model.setTitle(timeMinuteToString(timeStartMinutes) + "~" + timeMinuteToString(timeEndMinutes));
        return STATE_SUCCESS;
    }

    @Override
    public void setSelected(int position) {
        DropdownTime model = getData().get(position);
        List<DropdownTime> selected = getSelected();
        for (DropdownTime item : selected) {
            item.setChecked(false);
        }
        model.setChecked(true);
        selected.clear();
        selected.add(model);
        if (!model.isCustom()) {
            updateTitle();
        }
        updateContent();
        notifyDataSetChanged();
    }

    @Override
    protected void setupTitle(@NonNull DropdownMenuNew.ViewHolder<DropdownTime> holder,
                              List<DropdownTime> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        DropdownTime model = models.get(0);
        TextView title = holder.getView(R.id.dropdown_title);
        title.setText(model.getTitle());
        title.setSelected(model.getId() != 0);
    }

    @Override
    protected void setupContent(@NonNull DropdownMenuNew.ViewHolder<DropdownTime> holder,
                                List<DropdownTime> models) {
        if (models == null || models.isEmpty()) {
            return;
        }
        DropdownTime model = models.get(0);
        holder.getView(R.id.group_custom).setVisibility(model.isCustom() ? View.VISIBLE : View.GONE);
        if (model.isCustom()) {
            initCustomTime(model);
        }
    }

    @Override
    protected void setupItem(@NonNull DropdownMenuNew.ViewHolder<DropdownTime> holder,
                             DropdownTime model, int position) {
        TextView item = holder.getView(R.id.tv_item);
        item.setText(model.getItemName());
        item.setSelected(model.isChecked());
    }

    private void initCustomTime(DropdownTime model) {
        DropdownMenuNew.ViewHolder<DropdownTime> holder = getContent();
        tvCustomTimeStart = holder.getView(R.id.tv_start);
        tvCustomTimeEnd = holder.getView(R.id.tv_end);
        lvWheelHour = holder.getView(R.id.lv_time_hour);
        lvWheelMinute = holder.getView(R.id.lv_time_minute);

        tvCustomTimeStart.setOnClickListener(v -> {
            isTargetCustomStart = true;
            customStartReady = true;
            updateTimeView();
        });
        tvCustomTimeEnd.setOnClickListener(v -> {
            isTargetCustomStart = false;
            customEndReady = true;
            updateTimeView();
        });
        holder.getView(R.id.iv_clear).setOnClickListener(v -> {
            // 清空状态，无滚轮，时间标签灰色
            customStartReady = false;
            customEndReady = false;
            lvWheelHour.setVisibility(View.INVISIBLE);
            lvWheelMinute.setVisibility(View.INVISIBLE);
            tvCustomTimeStart.setText("");
            tvCustomTimeEnd.setText("");
            tvCustomTimeStart.setSelected(false);
            tvCustomTimeEnd.setSelected(false);
            timeStartMinutes = 0;
            timeEndMinutes = MINUTE_PER_DAY - 1;
        });

        lvWheelHour.setListener(index -> {
            if (isTargetCustomStart) {
                int minute = timeStartMinutes % MINUTE_PER_HOUR;
                timeStartMinutes = index * MINUTE_PER_HOUR + minute;
            } else {
                int minute = timeEndMinutes % MINUTE_PER_HOUR;
                timeEndMinutes = index * MINUTE_PER_HOUR + minute;
            }
            updateTimeLabel();
        });
        lvWheelMinute.setListener(index -> {
            if (isTargetCustomStart) {
                int hour = timeStartMinutes / MINUTE_PER_HOUR;
                timeStartMinutes = hour * MINUTE_PER_HOUR + index;
            } else {
                int hour = timeEndMinutes / MINUTE_PER_HOUR;
                timeEndMinutes = hour * MINUTE_PER_HOUR + index;
            }
            updateTimeLabel();
        });
        updateTime(model);
    }

    private void updateTime(DropdownTime customTime) {
        long timeStart = customTime.getTimeStart();
        if (timeStart >= 0) {
            temp.setTimeInMillis(timeStart);
            timeStartMinutes = temp.get(Calendar.HOUR_OF_DAY) * MINUTE_PER_HOUR
                    + temp.get(Calendar.MINUTE);
        } else {
            timeStartMinutes = 0;
        }
        long timeEnd = customTime.getTimeEnd();
        if (timeEnd >= 0) {
            temp.setTimeInMillis(timeEnd);
            timeEndMinutes = temp.get(Calendar.HOUR_OF_DAY) * MINUTE_PER_HOUR
                    + temp.get(Calendar.MINUTE);
        } else {
            timeEndMinutes = MINUTE_PER_DAY - 1;
        }
        if (customTime.getTimeStart() < 0 && customTime.getTimeEnd() < 0) {
            isTargetCustomStart = true;
            customStartReady = true;
            customEndReady = false;
            tvCustomTimeStart.setText("");
            tvCustomTimeEnd.setText("");
        }
        updateTimeView();
    }

    private void updateTimeView() {
        lvWheelHour.setVisibility(View.VISIBLE);
        lvWheelMinute.setVisibility(View.VISIBLE);
        tvCustomTimeStart.setSelected(isTargetCustomStart);
        tvCustomTimeEnd.setSelected(!isTargetCustomStart);
        int time = isTargetCustomStart ? timeStartMinutes : timeEndMinutes;
        lvWheelHour.setCurrentPosition(time / MINUTE_PER_HOUR);
        lvWheelMinute.setCurrentPosition(time % MINUTE_PER_HOUR);
        updateTimeLabel();
    }

    private void updateTimeLabel() {
        TextView target = isTargetCustomStart ? tvCustomTimeStart : tvCustomTimeEnd;
        int targetValue = isTargetCustomStart ? timeStartMinutes : timeEndMinutes;
        target.setText(timeMinuteToString(targetValue));
    }

    private String timeMinuteToString(int time) {
        return String.format(Locale.getDefault(),
                "%02d:%02d", time / MINUTE_PER_HOUR, time % MINUTE_PER_HOUR);
    }

}
