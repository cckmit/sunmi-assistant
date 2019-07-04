package com.sunmi.assistant.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.ChooseShopActivity_;

import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.Utils;

/**
 * Description:
 * Created by bruce on 2019/7/1.
 */
public class MainTopBar extends LinearLayout implements BaseNotification.NotificationCenterDelegate {

    private Context mContext;
    private TextView tvCompanyName, tvShopName;

    public MainTopBar(@NonNull Context context) {
        this(context, null);
    }

    public MainTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        BaseNotification.newInstance().addStickObserver(this, NotificationConstant.shopSwitched);
        BaseNotification.newInstance().addStickObserver(this, NotificationConstant.shopNameChanged);
        BaseNotification.newInstance().addStickObserver(this, NotificationConstant.companyNameChanged);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.MainTopBar, 0, 0);
        int bottomMargin = (int) typedArray.getDimension(R.styleable.MainTopBar_bottom_margin, 0);
        typedArray.recycle();
        setBackgroundColor(Color.WHITE);
        setOrientation(VERTICAL);
        tvCompanyName = new TextView(mContext);
        tvCompanyName.setTextColor(mContext.getResources().getColor(R.color.colorText));
        tvCompanyName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp_16));
        tvCompanyName.setSingleLine();
        tvCompanyName.setEllipsize(TextUtils.TruncateAt.END);
        tvCompanyName.setText(SpUtils.getCompanyName());
        LayoutParams lpCompany = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lpCompany.setMargins((int) mContext.getResources().getDimension(R.dimen.dp_20),
                Utils.getStatusBarHeight(mContext) +
                        (int) mContext.getResources().getDimension(R.dimen.dp_4),
                (int) mContext.getResources().getDimension(R.dimen.dp_20), 0);
        addView(tvCompanyName, lpCompany);

        tvShopName = new TextView(mContext);
        tvShopName.setTextColor(mContext.getResources().getColor(R.color.colorText));
        tvShopName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp_28));
        tvShopName.setSingleLine();
        tvShopName.setEllipsize(TextUtils.TruncateAt.END);
        tvShopName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        tvShopName.setText(SpUtils.getShopName());
        Drawable drawableLeft = getResources().getDrawable(R.drawable.dashboard_ic_location);
        tvShopName.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        LayoutParams lpShop = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lpShop.setMargins((int) mContext.getResources().getDimension(R.dimen.dp_12), 0,
                (int) mContext.getResources().getDimension(R.dimen.dp_12), bottomMargin);
        addView(tvShopName, lpShop);
        tvShopName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseShopActivity_.intent(mContext).start();
                ((Activity) mContext).overridePendingTransition(R.anim.activity_open_down_up, R.anim.bottom_silent);
            }
        });
    }

    public void setCompanyName(String companyName) {
        tvCompanyName.setText(companyName);
    }

    public void setShopName(String shopName) {
        tvShopName.setText(shopName);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BaseNotification.newInstance().removeObserver(this, NotificationConstant.shopSwitched);
        BaseNotification.newInstance().removeObserver(this, NotificationConstant.shopNameChanged);
        BaseNotification.newInstance().removeObserver(this, NotificationConstant.companyNameChanged);
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        setCompanyName(SpUtils.getCompanyName());
        setShopName(SpUtils.getShopName());
    }

}
