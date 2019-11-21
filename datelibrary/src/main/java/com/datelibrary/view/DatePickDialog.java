package com.datelibrary.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.datelibrary.R;
import com.datelibrary.bean.DateType;
import com.datelibrary.listener.OnChangeListener;
import com.datelibrary.listener.OnSureListener;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by codbking on 2016/8/11.
 */
public class DatePickDialog extends Dialog implements OnChangeListener {

    private TextView tvTitle;
    private FrameLayout flWheel;
    private TextView tvCancel;
    private TextView tvSure;
    private TextView tvMessage;

    private String title;
    private String format = "yyyy-MM-dd";
    private DateType type = DateType.TYPE_ALL;

    //开始时间
    private Date startDate = new Date();
    //年分限制，默认上下100年
    private int yearLimit = 100;

    private OnChangeListener onChangeListener;

    private OnSureListener onSureListener;

    private DatePicker mDatePicker;

    //设置标题
    public void setTitle(String title) {
        this.title = title;
    }

    //设置模式
    public void setType(DateType type) {
        this.type = type;
    }

    //设置选择日期显示格式，设置显示message,不设置不显示message
    public void setMessageFormat(String format) {
        this.format = format;
    }

    //设置开始时间
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    //设置年份限制，上下年份
    public void setYearLimit(int yearLimit) {
        this.yearLimit = yearLimit;
    }

    //设置选择回调
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    //设置点击确定按钮，回调
    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public DatePickDialog(Context context) {
        super(context, R.style.dialog_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbk_dialog_pick_time);

        initView();
        initParas();
    }

    private DatePicker getDatePicker() {
        DatePicker picker = new DatePicker(getContext(), type);
        picker.setStartDate(startDate);
        picker.setYearLimt(yearLimit);
        picker.setOnChangeListener(this);
        picker.init();
        return picker;
    }

    private void initView() {
        this.tvSure = findViewById(R.id.sure);
        this.tvCancel = findViewById(R.id.cancel);
        this.flWheel = findViewById(R.id.wheelLayout);
        this.tvTitle = findViewById(R.id.title);
        tvMessage = findViewById(R.id.message);

        mDatePicker = getDatePicker();
        this.flWheel.addView(mDatePicker);

        //setValue
        this.tvTitle.setText(title);

        this.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onSureListener != null) {
                    onSureListener.onSure(mDatePicker.getSelectDate());
                }
            }
        });
    }

    private void initParas() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = getScreenWidth(getContext());
        getWindow().setAttributes(params);
    }

    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    @Override
    public void onChanged(Date date) {
        if (onChangeListener != null) {
            onChangeListener.onChanged(date);
        }

        if (!TextUtils.isEmpty(format)) {
            String message = "";
            try {
                message = new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvMessage.setText(message);
        }
    }

}
