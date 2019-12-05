package com.sunmi.ipc.cash;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpActivity;

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

    @Extra
    int deviceId;
    @Extra
    long startTime;
    @Extra
    long EndTime;
    @Extra
    String deviceName;
    @Extra
    HashMap<Integer,String> ipcName;

    @AfterViews
    void init(){

    }
}
