package com.datelibrary.view;

import android.content.Context;
import android.widget.TextView;

import com.datelibrary.R;
import com.datelibrary.adapter.WheelGeneralAdapter;
import com.datelibrary.bean.DateType;
import com.datelibrary.listener.OnChangeListener;
import com.datelibrary.utils.DatePickerHelper;
import com.datelibrary.utils.DateUtils;

import java.util.Date;

/**
 * Created by codbking on 2016/8/10.
 */
public class DatePicker extends BaseWheelPick {

    private WheelView yearView;
    private WheelView perWeekView;
    private WheelView monthView;
    private WheelView dayView;
    private TextView weekView;
    private WheelView hourView;
    private WheelView minuteView;

    private Integer[] yearArr, mothArr, dayArr, hourArr, minutArr;
    private String[] weekArr;

    private DatePickerHelper datePicker;

    public DateType type = DateType.TYPE_ALL;

    //开始时间
    private Date startDate = new Date();
    //年分限制，默认上下5年
    private int yearLimt = 5;

    private OnChangeListener onChangeListener;
    private int selectDay;
    private int selectWeek;
    private int yearEnd;

    //选择时间回调
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public DatePicker(Context context, DateType type) {
        super(context);
        if (this.type != null) {
            this.type = type;
        }
        yearEnd = DateUtils.getYear(new Date());
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setYearLimt(int yearLimt) {
        this.yearLimt = yearLimt;
    }

    public void setYearEnd(int yearEnd) {
        this.yearEnd = yearEnd;
    }

    //初始化值
    public void init() {
        this.minuteView = findViewById(R.id.minute);
        this.hourView = findViewById(R.id.hour);
        this.weekView = findViewById(R.id.week);
        this.dayView = findViewById(R.id.day);
        this.monthView = findViewById(R.id.month);
        this.perWeekView = findViewById(R.id.per_week);
        this.yearView = findViewById(R.id.year);
        switch (type) {
            case TYPE_ALL:
                this.minuteView.setVisibility(VISIBLE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(VISIBLE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.perWeekView.setVisibility(GONE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YW:
                this.minuteView.setVisibility(GONE);
                this.hourView.setVisibility(GONE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(GONE);
                this.monthView.setVisibility(GONE);
                this.perWeekView.setVisibility(VISIBLE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YMDHM:
                this.minuteView.setVisibility(VISIBLE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.perWeekView.setVisibility(GONE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YMDH:
                this.minuteView.setVisibility(GONE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.perWeekView.setVisibility(GONE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YMD:
                this.minuteView.setVisibility(GONE);
                this.hourView.setVisibility(GONE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(VISIBLE);
                this.monthView.setVisibility(VISIBLE);
                this.perWeekView.setVisibility(GONE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_YM:
                this.minuteView.setVisibility(GONE);
                this.hourView.setVisibility(GONE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(GONE);
                this.monthView.setVisibility(VISIBLE);
                this.perWeekView.setVisibility(GONE);
                this.yearView.setVisibility(VISIBLE);
                break;
            case TYPE_HM:
                this.minuteView.setVisibility(VISIBLE);
                this.hourView.setVisibility(VISIBLE);
                this.weekView.setVisibility(GONE);
                this.dayView.setVisibility(GONE);
                this.monthView.setVisibility(GONE);
                this.perWeekView.setVisibility(GONE);
                this.yearView.setVisibility(GONE);
                break;
            default:
                break;
        }

        datePicker = new DatePickerHelper(getContext(), yearEnd);
        datePicker.setStartDate(startDate, yearLimt);

        weekArr = datePicker.genWeek();
        dayArr = datePicker.genDay();
        yearArr = datePicker.genYear();
        mothArr = datePicker.genMonth();
        hourArr = datePicker.genHour();
        minutArr = datePicker.genMinut();

        weekView.setText(datePicker.getDisplayStartWeek());

        setWheelListener(yearView, yearArr, false);
        setWheelListener(perWeekView, weekArr, true);
        setWheelListener(monthView, mothArr, true);
        setWheelListener(dayView, dayArr, true);
        setWheelListener(hourView, hourArr, true);
        setWheelListener(minuteView, minutArr, true);

        yearView.setCurrentItem(datePicker.findIndextByValue(datePicker.getToady(DatePickerHelper.Type.YEAR), yearArr));
        perWeekView.setCurrentItem(datePicker.getToady(DatePickerHelper.Type.WEEK) - 1);
        monthView.setCurrentItem(datePicker.findIndextByValue(datePicker.getToady(DatePickerHelper.Type.MOTH), mothArr));
        dayView.setCurrentItem(datePicker.findIndextByValue(datePicker.getToady(DatePickerHelper.Type.DAY), dayArr));
        hourView.setCurrentItem(datePicker.findIndextByValue(datePicker.getToady(DatePickerHelper.Type.HOUR), hourArr));
        minuteView.setCurrentItem(datePicker.findIndextByValue(datePicker.getToady(DatePickerHelper.Type.MINUTE), minutArr));
    }

    @Override
    protected String[] convertData(WheelView wheelView, Integer[] data) {
        if (wheelView == yearView) {
            return datePicker.getDisplayValue(data, "年");
        } else if (wheelView == monthView) {
            return datePicker.getDisplayValue(data, "月");
        } else if (wheelView == dayView) {
            return datePicker.getDisplayValue(data, "日");
        } else if (wheelView == hourView) {
            return datePicker.getDisplayValue(data, "");
        } else if (wheelView == minuteView) {
            return datePicker.getDisplayValue(data, "");
        }
        return new String[0];
    }

    @Override
    protected int getLayout() {
        return R.layout.cbk_wheel_picker;
    }

    @Override
    protected int getItemHeight() {
        return dayView.getItemHeight();
    }

    @Override
    protected void setData(Object[] datas) {
    }

    private void setChangeDaySelect(int year, int moth) {
        dayArr = datePicker.genDay(year, moth);
        WheelGeneralAdapter adapter = (WheelGeneralAdapter) dayView.getViewAdapter();
        adapter.setData(convertData(dayView, dayArr));

        int indxt = datePicker.findIndextByValue(selectDay, dayArr);
        if (indxt == -1) {
            dayView.setCurrentItem(0);
        } else {
            dayView.setCurrentItem(indxt);
        }
    }

    private void setChangeWeekSelect(int year) {
        weekArr = datePicker.genWeek(year);
        WheelGeneralAdapter adapter = (WheelGeneralAdapter) perWeekView.getViewAdapter();
        adapter.setData(weekArr);
        if (selectWeek < DateUtils.getMaxWeekNumOfYear(year)) {
            perWeekView.setCurrentItem(selectWeek);
        } else {
            perWeekView.setCurrentItem(0);
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

        int year = yearArr[yearView.getCurrentItem()];
        int moth = mothArr[monthView.getCurrentItem()];
        int day = dayArr[dayView.getCurrentItem()];
        int hour = hourArr[hourView.getCurrentItem()];
        int minut = minutArr[minuteView.getCurrentItem()];

        if (wheel == yearView || wheel == monthView) {
            setChangeDaySelect(year, moth);
        } else {
            selectDay = day;
        }

        if (wheel == yearView) {
            setChangeWeekSelect(year);
        } else {
            selectWeek = perWeekView.getCurrentItem();
        }

        if (wheel == yearView || wheel == monthView || wheel == dayView) {
            weekView.setText(datePicker.getDisplayWeek(year, moth, day));
        }

        if (onChangeListener != null) {
            onChangeListener.onChanged(DateUtils.getDate(year, moth, day, hour, minut));
        }
    }

    @Override
    public void onScrollingStarted(WheelView wheel) {
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {
    }

    //获取选中日期
    public Date getSelectDate() {
        int year = yearArr[yearView.getCurrentItem()];
        int week = perWeekView.getCurrentItem();
        int moth = mothArr[monthView.getCurrentItem()];
        int day = dayArr[dayView.getCurrentItem()];
        int hour = hourArr[hourView.getCurrentItem()];
        int minut = minutArr[minuteView.getCurrentItem()];
        if (type == DateType.TYPE_YW) {
            return DateUtils.getFirstDayOfWeek(year, week);
        } else {
            return DateUtils.getDate(year, moth, day, hour, minut);
        }
    }

}
