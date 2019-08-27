package com.sunmi.ipc.setting;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.datelibrary.DatePicker;
import com.datelibrary.bean.DateType;
import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.entity.DetectionConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.dialog.BottomDialog;

import static com.sunmi.ipc.setting.entity.DetectionConfig.INTENT_EXTRA_DETECTION_CONFIG;

/**
 * @author yinhui
 * @date 2019-07-16
 */
@EActivity(resName = "ipc_setting_activity_detection_time")
public class IpcSettingDetectionTimeActivity extends BaseActivity {

    private static final int WEEK_FIRST_DAY_MASK = 0x1;
    private static final int WEEK_ALL_DAY_MASK = 0x7f;
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
    SunmiDevice mDevice;
    @Extra
    DetectionConfig mConfig;

    private boolean mIsAllTime;
    private int mDays;
    private int mTimeStart;
    private int mTimeEnd;

    Calendar mCalendar = Calendar.getInstance();
    int mTempDaySelect;

    private View.OnClickListener mOnWeekSelectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.detection_day_monday) {
                mTempDaySelect ^= WEEK_MONDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_MONDAY));
            } else if (id == R.id.detection_day_tuesday) {
                mTempDaySelect ^= WEEK_TUESDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_TUESDAY));
            } else if (id == R.id.detection_day_wednesday) {
                mTempDaySelect ^= WEEK_WEDNESDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_WEDNESDAY));
            } else if (id == R.id.detection_day_thursday) {
                mTempDaySelect ^= WEEK_THURSDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_THURSDAY));
            } else if (id == R.id.detection_day_friday) {
                mTempDaySelect ^= WEEK_FRIDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_FRIDAY));
            } else if (id == R.id.detection_day_saturday) {
                mTempDaySelect ^= WEEK_SATURDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_SATURDAY));
            } else if (id == R.id.detection_day_sunday) {
                mTempDaySelect ^= WEEK_SUNDAY;
                v.setSelected(isWeekChecked(mTempDaySelect, WEEK_SUNDAY));
            }
        }
    };

    @AfterViews
    void init() {
        initData(mConfig);
        setViewChecked(mIsAllTime);
        updateView(mDays, mTimeStart, mTimeEnd);
        mSwitchDetectionAllTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked != mIsAllTime) {
                    mIsAllTime = isChecked;
                    setViewChecked(isChecked);
                    setDetectionEnable(isChecked);
                }
            }
        });
    }

    private void initData(DetectionConfig config) {
        mIsAllTime = config.detectionDays == DetectionConfig.DETECTION_ALL_TIME;
        mDays = mIsAllTime ? 0 : config.detectionDays;
        mTimeStart = config.detectionTimeStart;
        mTimeEnd = config.detectionTimeEnd;
    }

    private void setViewChecked(boolean isAllTime) {
        mSwitchDetectionAllTime.setChecked(isAllTime);
        mSilDaysCount.setEnabled(!isAllTime);
        mSilTimeStart.setEnabled(!isAllTime);
        mSilTimeEnd.setEnabled(!isAllTime);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_right_arrow_24dp, getTheme());
        drawable = DrawableCompat.wrap(drawable);
        if (isAllTime) {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSilDaysCount.setRightImage(drawable);
            mSilTimeStart.setRightImage(drawable);
            mSilTimeEnd.setRightImage(drawable);
            mSilDaysCount.setLeftTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSilDaysCount.setRightTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSilTimeStart.setLeftTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSilTimeStart.setRightTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSilTimeEnd.setLeftTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
            mSilTimeEnd.setRightTextColor(ContextCompat.getColor(this, R.color.color_BBBBC7));
        } else {
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorText_60));
            mSilDaysCount.setRightImage(drawable);
            mSilTimeStart.setRightImage(drawable);
            mSilTimeEnd.setRightImage(drawable);
            mSilDaysCount.setLeftTextColor(ContextCompat.getColor(this, R.color.colorText));
            mSilDaysCount.setRightTextColor(ContextCompat.getColor(this, R.color.colorText));
            mSilTimeStart.setLeftTextColor(ContextCompat.getColor(this, R.color.colorText));
            mSilTimeStart.setRightTextColor(ContextCompat.getColor(this, R.color.colorText));
            mSilTimeEnd.setLeftTextColor(ContextCompat.getColor(this, R.color.colorText));
            mSilTimeEnd.setRightTextColor(ContextCompat.getColor(this, R.color.colorText));
        }
    }

    private void updateView(int days, int timeStart, int timeEnd) {
        updateDetectionDaysView(days);
        updateTimeStartView(timeStart);
        updateTimeEndView(timeEnd);
    }

    @Click(resName = "sil_ipc_setting_days_count")
    void onDetectionDaysClick() {
        mTempDaySelect = mDays;
        Dialog dialog = new BottomDialog.Builder(this)
                .setTitle(R.string.ipc_setting_detection_time_day)
                .setCancelButton(R.string.sm_cancel)
                .setOkButton(R.string.str_complete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDays = mTempDaySelect;
                        updateDetectionDaysView(mDays);
                        setDetectionDays(mDays);
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
        v.setSelected((mTempDaySelect & week) != 0);
    }

    @Click(resName = {"sil_ipc_setting_time_start", "sil_ipc_setting_time_end"})
    void onDetectionTimeClick(View item) {
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
                            setDetectionTimeStart(mTimeStart);
                        } else {
                            mTimeEnd = dateToInt(picker.getSelectDate());
                            updateTimeEndView(mTimeEnd);
                            setDetectionTimeEnd(mTimeEnd);
                        }
                    }
                })
                .setContent(picker, layoutParams)
                .create()
                .show();
    }

    private void updateDetectionDaysView(int days) {
        if ((days & WEEK_ALL_DAY_MASK) == WEEK_ALL_DAY_MASK) {
            mSilDaysCount.setRightText(getString(R.string.ipc_setting_detection_time_day_all));
            return;
        }
        if ((days & WEEK_ALL_DAY_MASK) == 0) {
            mSilDaysCount.setRightText(getString(R.string.ipc_setting_detection_time_day_none));
            return;
        }
        String[] weekName = getResources().getStringArray(R.array.week_name);
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String day : weekName) {
            if ((days & WEEK_FIRST_DAY_MASK) != 0) {
                if (isFirst) {
                    sb.append(day);
                } else {
                    sb.append("、").append(day);
                }
                isFirst = false;
            }
            days >>= 1;
        }
        mSilDaysCount.setRightText(sb.toString());
    }

    private void updateTimeStartView(int time) {
        String hour = String.format(Locale.getDefault(), "%02d", time / 3600);
        String minute = String.format(Locale.getDefault(), "%02d", (time % 3600) / 60);
        mSilTimeStart.setRightText(hour + ":" + minute);
        updateTimeEndView(mTimeEnd);
    }

    private void updateTimeEndView(int time) {
        String hour = String.format(Locale.getDefault(), "%02d", time / 3600);
        String minute = String.format(Locale.getDefault(), "%02d", (time % 3600) / 60);
        StringBuilder sb = new StringBuilder();
        sb.append(hour).append(":").append(minute);
        if (time <= mTimeStart) {
            sb.append(getString(R.string.ipc_setting_detection_time_tomorrow));
        }
        mSilTimeEnd.setRightText(sb.toString());
    }

    private boolean isWeekChecked(int daySelect, int witchDay) {
        return (daySelect & witchDay) != 0;
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

    private void setDetectionEnable(boolean enable) {
        mConfig.detectionDays = enable ? DetectionConfig.DETECTION_ALL_TIME : mDays;
        postSetConfig();
    }

    private void setDetectionDays(int detectionDays) {
        mConfig.detectionDays = detectionDays;
        postSetConfig();
    }

    private void setDetectionTimeStart(int start) {
        mConfig.detectionTimeStart = start;
        postSetConfig();
    }

    private void setDetectionTimeEnd(int end) {
        mConfig.detectionTimeEnd = end;
        postSetConfig();
    }

    private void postSetConfig() {
        showLoadingDialog();
        new IPCCall().setIpcDetection(this, mDevice.getModel(), mDevice.getDeviceid(),
                mConfig.activeDetection, mConfig.soundDetection, mConfig.detectionDays,
                mConfig.detectionTimeStart, mConfig.detectionTimeEnd);
        Intent intent = getIntent();
        intent.putExtra(INTENT_EXTRA_DETECTION_CONFIG, mConfig);
        setResult(RESULT_OK, intent);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.setIpcDetection};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.setIpcDetection) {
            hideLoadingDialog();
            if (res.getDataErrCode() == 1) {
                shortTip(R.string.tip_set_complete);
            } else {
                shortTip(R.string.tip_set_fail);
            }
        }
    }

}
