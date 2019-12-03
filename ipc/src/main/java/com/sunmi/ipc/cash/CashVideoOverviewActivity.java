package com.sunmi.ipc.cash;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.sunmi.ipc.cash.adapter.CashCalendarAdapter;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.StatusBarUtils;
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

    private CenterLayoutManager llManager;
    private Calendar today = Calendar.getInstance();
    private Calendar selectedCalendar;
    private List<Calendar> calendars = new ArrayList<>(15);
    private CashCalendarAdapter adapter;
    private int selectPos = 14;

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
        initDate();
    }

    private void initDate() {
        calendars.clear();
        Calendar c;
        int index = DateTimeUtils.getDiffDays(today.getTime(), selectedCalendar.getTime());
        if (index < 15) {
            selectPos = 14 - index;
            c = Calendar.getInstance();
            c.add(Calendar.DATE, -14);
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
            });
            rvCalender.setAdapter(adapter);
            rvCalender.scrollToPosition(selectPos);
        } else {
            adapter.notifyDataSetChanged();
            llManager.smoothScrollToPosition(rvCalender, new RecyclerView.State(), selectPos);
        }

    }


}
