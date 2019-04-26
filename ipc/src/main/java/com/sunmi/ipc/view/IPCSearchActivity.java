package com.sunmi.ipc.view;

import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.SMDeviceDiscoverUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/4/16.
 */
@EActivity(resName = "activity_search_ipc")
public class IPCSearchActivity extends BaseActivity
        implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(resName = "nsv_ipc")
    NestedScrollView scrollView;
    @ViewById(resName = "rl_search")
    RelativeLayout rlSearch;
    @ViewById(resName = "rv_ipc")
    RecyclerView rvDevice;
    @ViewById(resName = "rl_no_device")
    RelativeLayout rlNoWifi;

    @Extra
    String shopId;
    @Extra
    boolean isSunmiLink;//是否是sunmi link模式

    private boolean isApMode;//是否ap模式
    IPCListAdapter ipcListAdapter;
    List<SunmiDevice> ipcList = new ArrayList<>();
    Set<String> ipcSet = new HashSet<>();

    @AfterViews
    void init() {
        startScan();
        initApList();
    }

    private void startScan() {
        rlNoWifi.setVisibility(View.GONE);
        rlSearch.setVisibility(View.VISIBLE);
        SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (ipcList.size() <= 0) {
                    rlSearch.setVisibility(View.GONE);
                    rlNoWifi.setVisibility(View.VISIBLE);
                }
            }
        }, 5000);
    }

    @UiThread
    void initApList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvDevice.setLayoutManager(layoutManager);
        rvDevice.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        ipcListAdapter = new IPCListAdapter(context, ipcList);
        rvDevice.setAdapter(ipcListAdapter);
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        startScan();
    }

    @Click(resName = "btn_config")
    void configClick() {
        if (ipcList == null || ipcList.size() < 1) return;
        if (isApMode) {
            WifiConfigActivity_.intent(context).sunmiDevice(ipcList.get(0)).shopId(shopId).start();
        } else if (isSunmiLink) {
            sunmiLinkConfig();
        } else {
            WifiConfiguringActivity_.intent(context).sunmiDevices((ArrayList<SunmiDevice>) ipcList).shopId(shopId).start();
        }
    }

    private void sunmiLinkConfig() {//todo sunmi link
        WifiConfigActivity_.intent(context).sunmiDevice(ipcList.get(0)).shopId(shopId).start();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcDiscovered};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == IpcConstants.ipcDiscovered) {
            SunmiDevice ipc = (SunmiDevice) args[0];
            ipcFound(ipc);
        }
    }

    //1 udp搜索完成 --> ap登录
    private void ipcFound(SunmiDevice ipc) {
        if (TextUtils.equals("SS1", ipc.getModel()) || TextUtils.equals("FS1", ipc.getModel())) {
            LogCat.e(TAG, "ipcFound fs ipcdevice = " + ipc.toString());
            hasFound(ipc);
        }
    }

    private boolean hasFound(SunmiDevice ipc) {
        if (ipcSet.contains(ipc.getDeviceid())) {
            return true;
        } else {
            IpcConstants.IPC_SN = ipc.getDeviceid();
            IpcConstants.IPC_IP = "http://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
            isApMode = TextUtils.equals("AP", ipc.getNetwork());
            ipc.setSelected(true);
            ipcSet.add(ipc.getDeviceid());
            addDevice(ipc);
        }
        return false;
    }

    @UiThread
    void addDevice(SunmiDevice device) {
        ipcList.add(device);
        if (ipcListAdapter != null) ipcListAdapter.notifyDataSetChanged();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @UiThread
    public void setNoWifiVisible(int visibility) {
        rlNoWifi.setVisibility(visibility);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

}
