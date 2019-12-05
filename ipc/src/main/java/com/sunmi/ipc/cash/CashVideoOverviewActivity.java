package com.sunmi.ipc.cash;

import android.app.Dialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.calendar.Config;
import com.sunmi.ipc.calendar.VerticalCalendar;
import com.sunmi.ipc.cash.adapter.CashCalendarAdapter;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.RouterConfig;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.view.CircleImage;
import sunmi.common.view.dialog.BottomDialog;
import sunmi.common.view.widget.CenterLayoutManager;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-02.
 */
@EActivity(resName = "activity_cash_video_overview")
public class CashVideoOverviewActivity extends BaseMvpActivity {

    @ViewById(resName = "rv_calendar")
    RecyclerView rvCalender;
    @ViewById(resName = "iv_calendar")
    ImageView ivCalender;
    @ViewById(resName = "tv_total_count_cash")
    TextView tvTotalCountCash;
    @ViewById(resName = "tv_total_count_abnormal")
    TextView tvCashCountAbnormal;
    @ViewById(resName = "civ_ipc")
    CircleImage civIpc;
    @ViewById(resName = "tv_ipc_name")
    TextView tvIpcName;
    @ViewById(resName = "tv_ipc_sn")
    TextView tvIpcSn;
    @ViewById(resName = "tv_count_cash")
    TextView tvCountCash;
    @ViewById(resName = "tv_count_abnormal")
    TextView tvCountAbnormal;
    @ViewById(resName = "cl_shop_cash")
    ConstraintLayout clShopCash;

    @Extra
    ArrayList<CashVideoServiceBean> serviceBeans;
    @Extra
    CashVideoServiceBean serviceBean;


    private CenterLayoutManager llManager;
    private Calendar today = Calendar.getInstance();
    private Calendar selectedCalendar, threeMonth = Calendar.getInstance();
    private Dialog calendarDialog;
    private VerticalCalendar calendarView;
    private List<Calendar> calendars = new ArrayList<>(15);
    private CashCalendarAdapter adapter;
    private int selectPos = 14;
    private long startTime;
    private long endTime;

    @RouterAnno(
            path = RouterConfig.Ipc.CASH_VIDEO_OVERVIEW
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), CashVideoOverviewActivity_.class);
        return intent;
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);
        llManager = new CenterLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false);
        rvCalender.setLayoutManager(llManager);
        selectedCalendar = Calendar.getInstance();
        threeMonth.add(Calendar.MONTH, -3);
        threeMonth.add(Calendar.DATE, 1);
        threeMonth.set(Calendar.HOUR_OF_DAY, 0);
        threeMonth.set(Calendar.MINUTE, 0);
        threeMonth.set(Calendar.SECOND, 0);
        threeMonth.set(Calendar.MILLISECOND, 0);
        initDate();
    }

    private void initDate() {
        initStartAndEndTime();
        calendars.clear();
        Calendar c;
        int todayIndex = DateTimeUtils.getDiffDays(selectedCalendar.getTime(), today.getTime());
        int mothIndex = DateTimeUtils.getDiffDays(threeMonth.getTime(), selectedCalendar.getTime());
        if (todayIndex < 15) {
            selectPos = 14 - todayIndex;
            c = Calendar.getInstance();
            c.add(Calendar.DATE, -14);
        } else if (mothIndex < 15 && todayIndex > 15) {
            selectPos = mothIndex;
            c = (Calendar) threeMonth.clone();
        } else {
            selectPos = 7;
            c = selectedCalendar;
            c.add(Calendar.DATE, -7);
        }
        for (int i = 0; i < 15; i++) {
            calendars.add((Calendar) c.clone());
            c.add(Calendar.DATE, 1);
        }
        initAdapter();
    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new CashCalendarAdapter(context, calendars);
            adapter.setOnItemClickListener((calendar, pos) -> {
                llManager.smoothScrollToPosition(rvCalender, new RecyclerView.State(), pos);
                selectedCalendar = calendar;
                initStartAndEndTime();
            });
            rvCalender.setAdapter(adapter);
            rvCalender.scrollToPosition(selectPos);
        } else {
            adapter.setSelectPosition(selectPos);
            llManager.smoothScrollToPosition(rvCalender, new RecyclerView.State(), selectPos);
        }
    }

    private void initStartAndEndTime() {
        startTime = (DateTimeUtils.getDayStart(selectedCalendar).getTimeInMillis()) / 1000;
        endTime = startTime + 3600 * 24;
    }

    @Click(resName = "iv_calendar")
    public void calendarClick() {
        if (isFastClick(1000)) {
            return;
        }
        if (calendarDialog == null || calendarView == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -3);
            Config config = new Config.Builder()
                    .setMinDate(c)
                    .build();
            int height = (int) (Utils.getScreenHeight(context) * 0.85);
            calendarView = new VerticalCalendar(this, config);
            calendarView.setOnCalendarSelectListener(calendar -> selectedCalendar = calendar);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            calendarDialog = new BottomDialog.Builder(context)
                    .setTitle(R.string.str_title_calendar)
                    .setContent(calendarView, lp)
                    .setCancelButton(R.string.sm_cancel)
                    .setOkButton(R.string.str_confirm, (dialog, which) -> {
                        initDate();
                    }).create();
        }
        calendarView.setSelected(startTime * 1000);
        calendarDialog.show();
    }

}
