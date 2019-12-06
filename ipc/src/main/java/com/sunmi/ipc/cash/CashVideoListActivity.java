package com.sunmi.ipc.cash;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.StatusBarUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-04.
 */

@EActivity(resName = "activity_cash_video_list")
public class CashVideoListActivity extends BaseMvpActivity {

    @ViewById(resName = "tv_date")
    TextView tvDate;
    @ViewById(resName = "bga_refresh")
    BGARefreshLayout refreshLayout;
    @ViewById(resName = "rv_cash_video")
    RecyclerView rvCashVideo;
    @ViewById(resName = "layout_network_error")
    View networkError;

    @Extra
    int deviceId = 0;
    @Extra
    long startTime;
    @Extra
    long endTime;
    @Extra
    int videoType = 0;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);
        tvDate.setText(DateTimeUtils.secondToDate(startTime,"yyyy.MM.dd"));
    }
}
