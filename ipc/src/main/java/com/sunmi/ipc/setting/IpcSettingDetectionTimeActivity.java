package com.sunmi.ipc.setting;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.datelibrary.DatePicker;
import com.datelibrary.bean.DateType;
import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;

import sunmi.common.base.BaseActivity;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.dialog.BottomDialog;

/**
 * @author yinhui
 * @date 2019-07-16
 */
@EActivity(resName = "ipc_setting_activity_detection_time")
public class IpcSettingDetectionTimeActivity extends BaseActivity {

    private static final int WEEK_MONDAY = 0x0001;
    private static final int WEEK_TUESDAY = 0x0002;
    private static final int WEEK_WEDNESDAY = 0x0004;
    private static final int WEEK_THURSDAY = 0x0008;
    private static final int WEEK_FRIDAY = 0x0010;
    private static final int WEEK_SATURDAY = 0x0020;
    private static final int WEEK_SUNDAY = 0x0040;

    @ViewById(resName = "sil_ipc_setting_days_count")
    SettingItemLayout mSilDaysCount;
    @ViewById(resName = "sil_ipc_setting_time_start")
    SettingItemLayout mSilTimeStart;
    @ViewById(resName = "sil_ipc_setting_time_end")
    SettingItemLayout mSilTimeEnd;
    @ViewById(resName = "switch_ipc_setting_detection_all_time")
    Switch mSwitchDetectionAllTime;

    @Extra
    boolean mIsAllTime;
    @Extra
    int mDaySelect;
    @Extra
    int mTimeStart;
    @Extra
    int mTimeEnd;

    Calendar mCalendar = Calendar.getInstance();

    private View.OnClickListener mOnWeekSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.detection_day_monday) {
                mDaySelect ^= WEEK_MONDAY;
                v.setSelected(isWeekChecked(WEEK_MONDAY));
            } else if (id == R.id.detection_day_tuesday) {
                mDaySelect ^= WEEK_TUESDAY;
                v.setSelected(isWeekChecked(WEEK_TUESDAY));
            } else if (id == R.id.detection_day_wednesday) {
                mDaySelect ^= WEEK_WEDNESDAY;
                v.setSelected(isWeekChecked(WEEK_WEDNESDAY));
            } else if (id == R.id.detection_day_thursday) {
                mDaySelect ^= WEEK_THURSDAY;
                v.setSelected(isWeekChecked(WEEK_THURSDAY));
            } else if (id == R.id.detection_day_friday) {
                mDaySelect ^= WEEK_FRIDAY;
                v.setSelected(isWeekChecked(WEEK_FRIDAY));
            } else if (id == R.id.detection_day_saturday) {
                mDaySelect ^= WEEK_SATURDAY;
                v.setSelected(isWeekChecked(WEEK_SATURDAY));
            } else if (id == R.id.detection_day_sunday) {
                mDaySelect ^= WEEK_SUNDAY;
                v.setSelected(isWeekChecked(WEEK_SUNDAY));
            }
        }
    };

    @AfterViews
    void init() {
        setViewChecked(mIsAllTime);
        mSwitchDetectionAllTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setViewChecked(isChecked);
                // TODO: API
            }
        });
    }

    private void setViewChecked(boolean isAllTime) {
        mSwitchDetectionAllTime.setChecked(isAllTime);
        mSilDaysCount.setEnabled(!isAllTime);
        mSilTimeStart.setEnabled(!isAllTime);
        mSilTimeEnd.setEnabled(!isAllTime);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_right_arrow_24dp, getTheme());
        drawable = DrawableCompat.wrap(drawable);
        if (isAllTime) {
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.color_BBBBC7));
            mSilDaysCount.setRightImage(drawable);
            mSilTimeStart.setRightImage(drawable);
            mSilTimeEnd.setRightImage(drawable);
            mSilDaysCount.setLeftTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSilDaysCount.setRightTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSilTimeStart.setLeftTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSilTimeStart.setRightTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSilTimeEnd.setLeftTextColor(getResources().getColor(R.color.color_BBBBC7));
            mSilTimeEnd.setRightTextColor(getResources().getColor(R.color.color_BBBBC7));
        } else {
            DrawableCompat.setTint(drawable, getResources().getColor(R.color.colorText_60));
            mSilDaysCount.setRightImage(drawable);
            mSilTimeStart.setRightImage(drawable);
            mSilTimeEnd.setRightImage(drawable);
            mSilDaysCount.setLeftTextColor(getResources().getColor(R.color.colorText));
            mSilDaysCount.setRightTextColor(getResources().getColor(R.color.colorText));
            mSilTimeStart.setLeftTextColor(getResources().getColor(R.color.colorText));
            mSilTimeStart.setRightTextColor(getResources().getColor(R.color.colorText));
            mSilTimeEnd.setLeftTextColor(getResources().getColor(R.color.colorText));
            mSilTimeEnd.setRightTextColor(getResources().getColor(R.color.colorText));
        }
    }

    @Click(resName = "sil_ipc_setting_days_count")
    void setDetectionDays() {
        BottomDialog dialog = new BottomDialog.Builder(this)
                .setTitle(R.string.ipc_setting_detection_time_day)
                .setCancelButton(R.string.sm_cancel)
                .setOkButton(R.string.str_complete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDetectionDaysView(mDaySelect);
                        // TODO: API
                    }
                })
                .setContent(R.layout.ipc_setting_dialog_detection_days)
                .create();
        initDialogWeek(dialog.findViewById(R.id.detection_day_monday), WEEK_MONDAY);
        initDialogWeek(dialog.findViewById(R.id.detection_day_tuesday), WEEK_TUESDAY);
        initDialogWeek(dialog.findViewById(R.id.detection_day_wednesday), WEEK_WEDNESDAY);
        initDialogWeek(dialog.findViewById(R.id.detection_day_thursday), WEEK_THURSDAY);
        initDialogWeek(dialog.findViewById(R.id.detection_day_friday), WEEK_FRIDAY);
        initDialogWeek(dialog.findViewById(R.id.detection_day_saturday), WEEK_SATURDAY);
        initDialogWeek(dialog.findViewById(R.id.detection_day_sunday), WEEK_SUNDAY);
        dialog.show();
    }

    private void initDialogWeek(View v, int week) {
        v.setOnClickListener(mOnWeekSelectListener);
        v.setSelected((mDaySelect & week) != 0);
    }

    @Click(resName = {"sil_ipc_setting_time_start", "sil_ipc_setting_time_end"})
    void setDetectionTime(View item) {
        int id = item.getId();
        if (id == R.id.sil_ipc_setting_time_start) {
            showTimeDialog(true);
        } else if (id == R.id.sil_ipc_setting_time_end) {
            showTimeDialog(false);
        }
    }

    private void showTimeDialog(final boolean isTimeStart) {
        final DatePicker picker = new DatePicker(this, DateType.TYPE_HM);
        picker.setStartDate(intToDate(isTimeStart ? mTimeStart : mTimeEnd));
        picker.init();
        int margin = (int) getResources().getDimension(R.dimen.dp_20);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, margin, 0, margin);

        new BottomDialog.Builder(this)
                .setTitle(isTimeStart ? R.string.ipc_setting_detection_time_start : R.string.ipc_setting_detection_time_end)
                .setCancelButton(R.string.sm_cancel)
                .setOkButton(R.string.str_complete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isTimeStart) {
                            mTimeStart = dateToInt(picker.getSelectDate());
                            updateTimeStartView(mTimeStart);
                            // TODO: API
                        } else {
                            mTimeEnd = dateToInt(picker.getSelectDate());
                            updateTimeEndView(mTimeEnd);
                            // TODO: API
                        }
                    }
                })
                .setContent(picker, layoutParams)
                .create()
                .show();
    }

    private void updateDetectionDaysView(int days) {
        // TODO: update views
    }

    private void updateTimeStartView(int time) {
        // TODO: update views
    }

    private void updateTimeEndView(int time) {
        // TODO: update views
    }

    private boolean isWeekChecked(int witchDay) {
        return (mDaySelect & witchDay) != 0;
    }

    private int dateToInt(Date time) {
        mCalendar.setTime(time);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        return hour * 3600 + minute * 60;
    }

    private Date intToDate(int time) {
        int hour = time / 3600;
        int minute = (time % 3600) / 60;
        mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        return mCalendar.getTime();
    }

}
