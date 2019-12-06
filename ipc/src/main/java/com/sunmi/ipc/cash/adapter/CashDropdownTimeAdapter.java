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

    private Group groupCustomTime;
    private TextView tvCustomTimeStart;
    private TextView tvCustomTimeEnd;
    private LoopView lvWheelHour;
    private LoopView lvWheelMinute;

    private boolean isTargetCustomStart = true;
    private int customTimeStartMinutes;
    private int customTimeEndMinutes;
    private Calendar temp = Calendar.getInstance();

    public CashDropdownTimeAdapter(Context context) {
        super(context, R.layout.dropdown_title_new, R.layout.cash_video_dropdown_time_item);
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
            updateTimeView();
        });
        tvCustomTimeEnd.setOnClickListener(v -> {
            isTargetCustomStart = false;
            updateTimeView();
        });
        holder.getView(R.id.iv_clear).setOnClickListener(v -> {
            // 清空状态，无滚轮，时间标签灰色
            lvWheelHour.setVisibility(View.INVISIBLE);
            lvWheelMinute.setVisibility(View.INVISIBLE);
            tvCustomTimeStart.setText("");
            tvCustomTimeEnd.setText("");
            tvCustomTimeStart.setSelected(false);
            tvCustomTimeEnd.setSelected(false);
            customTimeStartMinutes = 0;
            customTimeEndMinutes = 23 * 60 + 59;
        });

        lvWheelHour.setListener(index -> {
            if (isTargetCustomStart) {
                int minute = customTimeStartMinutes % 60;
                customTimeStartMinutes = index * 60 + minute;
            } else {
                int minute = customTimeEndMinutes % 60;
                customTimeEndMinutes = index * 60 + minute;
            }
            updateTimeLabel();
        });
        lvWheelMinute.setListener(index -> {
            if (isTargetCustomStart) {
                int hour = customTimeStartMinutes / 60;
                customTimeStartMinutes = hour * 60 + index;
            } else {
                int hour = customTimeEndMinutes / 60;
                customTimeEndMinutes = hour * 60 + index;
            }
            updateTimeLabel();
        });
        updateTime(model);
    }

    private void updateTime(DropdownTime customTime) {
        long timeStart = customTime.getTimeStart() * 1000;
        if (timeStart >= 0) {
            temp.setTimeInMillis(timeStart);
            customTimeStartMinutes = temp.get(Calendar.HOUR_OF_DAY) * 60 + temp.get(Calendar.MINUTE);
        } else {
            customTimeStartMinutes = 0;
        }
        long timeEnd = customTime.getTimeEnd() * 1000;
        if (timeEnd >= 0) {
            temp.setTimeInMillis(timeEnd);
            customTimeEndMinutes = temp.get(Calendar.HOUR_OF_DAY) * 60 + temp.get(Calendar.MINUTE);
        } else {
            customTimeEndMinutes = 23 * 60 + 59;
        }
        updateTimeView();
    }

    private void updateTimeView() {
        lvWheelHour.setVisibility(View.VISIBLE);
        lvWheelMinute.setVisibility(View.VISIBLE);
        tvCustomTimeStart.setSelected(isTargetCustomStart);
        tvCustomTimeEnd.setSelected(!isTargetCustomStart);
        int time = isTargetCustomStart ? customTimeStartMinutes : customTimeEndMinutes;
        lvWheelHour.setCurrentPosition(time / 60);
        lvWheelMinute.setCurrentPosition(time % 60);
    }

    private void updateTimeLabel() {
        TextView target = isTargetCustomStart ? tvCustomTimeStart : tvCustomTimeEnd;
        int targetValue = isTargetCustomStart ? customTimeStartMinutes : customTimeEndMinutes;
        String timeText = String.format(Locale.getDefault(),
                "%02d:%02d", targetValue / 60, targetValue % 60);
        target.setText(timeText);
    }

}
