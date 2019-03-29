package com.sunmi.ipc.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sunmi.ipc.IPCCall;
import com.sunmi.ipc.model.WifiListResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseActivity;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
@EActivity(resName = "activity_config_wifi")
public class WifiConfigActivity extends BaseActivity {

    @ViewById(resName = "rv_wifi")
    RecyclerView rvWifi;

    WifiListAdapter wifiListAdapter;

    @AfterViews
    void init() {
        IPCCall.getInstance().getWifiList(context);
    }

    @UiThread
    void initApList(List<WifiListResp.ScanResultsBean> list) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvWifi.setLayoutManager(layoutManager);
        rvWifi.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        wifiListAdapter = new WifiListAdapter(context, list);
        rvWifi.setAdapter(wifiListAdapter);
    }

}
