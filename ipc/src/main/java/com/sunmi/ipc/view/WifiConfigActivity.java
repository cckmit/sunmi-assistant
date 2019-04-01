package com.sunmi.ipc.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.sunmi.ipc.IPCCall;
import com.sunmi.ipc.R;
import com.sunmi.ipc.model.WifiListResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.view.dialog.InputDialog;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
@EActivity(resName = "activity_wifi_config")
public class WifiConfigActivity extends BaseActivity implements WifiListAdapter.OnItemClickListener {

    @ViewById(resName = "rv_wifi")
    RecyclerView rvWifi;

    WifiListAdapter wifiListAdapter;

    @AfterViews
    void init() {
//        IPCCall.getInstance().getWifiList(context);
        List<WifiListResp.ScanResultsBean> list = new ArrayList<>();
        WifiListResp.ScanResultsBean item = new WifiListResp.ScanResultsBean();
        item.setKey_mgmt("121231231");
        item.setSsid("sdfsdfdsfsf");
        list.add(item);
        initApList(list);
    }

    @UiThread
    void initApList(List<WifiListResp.ScanResultsBean> list) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvWifi.setLayoutManager(layoutManager);
        rvWifi.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        wifiListAdapter = new WifiListAdapter(context, list);
        wifiListAdapter.setOnItemClickListener(this);
        rvWifi.setAdapter(wifiListAdapter);
    }

    @Override
    public void onItemClick(String ssid, String mgmt) {
        createDialog(ssid, mgmt);
    }

    private void createDialog(final String ssid, final String mgmt) {
        new InputDialog.Builder(context)
                .setTitle(R.string.str_input_psw)
                .setCancelButton(R.string.str_cancel)
                .setConfirmButton(R.string.str_confirm,
                        new InputDialog.ConfirmClickListener() {
                            @Override
                            public void onConfirmClick(InputDialog dialog, String input) {
                                if (TextUtils.isEmpty(input)) {
                                    shortTip("密码不能为空");
//                                    shortTip(R.string.tip_wifi_psw_error);
                                    return;
                                }
                                WifiConfiguringActivity_.intent(context).start();
//                                WifiConfigCompletedActivity_.intent(context).start();
                                dialog.dismiss();
//                                IPCCall.getInstance().setIPCWifi(context,
//                                        ssid, mgmt, input);
                            }
                        }).create().show();
    }

}
