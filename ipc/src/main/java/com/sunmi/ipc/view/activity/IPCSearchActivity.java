package com.sunmi.ipc.view.activity;

import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.view.IPCListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.SMDeviceDiscoverUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SmRecyclerView;

/**
 * Description: 搜索ipc设备
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
    SmRecyclerView rvDevice;
    @ViewById(resName = "rl_no_device")
    RelativeLayout rlNoWifi;
    @ViewById(resName = "rl_loading")
    RelativeLayout rlLoading;
    @ViewById(resName = "tv_no_ipc")
    TextView tvNoIpc;
    @ViewById(resName = "btn_refresh")
    Button btnRefresh;
    @ViewById(resName = "tv_check_network")
    TextView tvCheckNetwork;

    @Extra
    String shopId;
    @Extra
    int deviceType;
    @Extra
    boolean isSunmiLink;//是否是sunmi link模式
    @Extra
    int network;
    @Extra
    int source;

    private boolean isApMode;//是否ap模式
    IPCListAdapter ipcListAdapter;
    List<SunmiDevice> ipcList = new ArrayList<>();

    private Map<String, SunmiDevice> ipcMap = new HashMap<>();

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (CommonConstants.TYPE_IPC_FS == deviceType) {
            tvNoIpc.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_no_fs, 0, 0);
        }else if (CommonConstants.TYPE_IPC_SS == deviceType) {
            tvNoIpc.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_no_ss, 0, 0);
        }
        isApMode = (network == IpcConstants.IPC_CONFIG_MODE_AP);
        if (network == IpcConstants.IPC_CONFIG_MODE_WIRED) {
            tvCheckNetwork.setText(R.string.tip_check_ipc_wired);
        } else {
            tvCheckNetwork.setText(R.string.tip_check_ipc_wireless);
        }
        rvDevice.init(R.drawable.shap_line_divider);
        startScan();
        initApList();
    }

    private void startScan() {
        rlNoWifi.setVisibility(View.GONE);
        rlSearch.setVisibility(View.VISIBLE);
        SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered);
        new Handler().postDelayed(() -> {
            if (ipcList.size() <= 0) {
                rlSearch.setVisibility(View.GONE);
                rlNoWifi.setVisibility(View.VISIBLE);
            } else {
                rlLoading.setVisibility(View.GONE);
                btnRefresh.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    @UiThread
    void initApList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvDevice.setLayoutManager(layoutManager);
        ipcListAdapter = new IPCListAdapter(context, ipcList);
        rvDevice.setAdapter(ipcListAdapter);
    }

    @Click(resName = "btn_retry")
    void retryClick() {
        ipcList.clear();
        ipcMap.clear();
        startScan();
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        ipcList.clear();
        ipcMap.clear();
        ipcListAdapter.notifyDataSetChanged();
        rlLoading.setVisibility(View.VISIBLE);
        btnRefresh.setVisibility(View.GONE);
        startScan();
    }

    @Click(resName = "btn_config")
    void configClick() {
        if (isFastClick(1000) || ipcList == null || ipcList.size() < 1) {
            return;
        }
        boolean isSelectNone = true;
        for (SunmiDevice device : ipcList) {
            if (device.isSelected()) {
                isSelectNone = false;
                break;
            }
        }
        if (isSelectNone) {
            shortTip(R.string.tip_please_select_ipc);
            return;
        }
        if (network == IpcConstants.IPC_CONFIG_MODE_AP) {
            gotoWifiConfigActivity();
        } else {
            gotoIpcConfigActivity();
        }
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

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcDiscovered, OpcodeConstants.getIpcToken, OpcodeConstants.getIsWire};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == IpcConstants.ipcDiscovered) {
            SunmiDevice ipc = (SunmiDevice) args[0];
            ipcFound(ipc);
        } else if (id == OpcodeConstants.getIpcToken) {
            ResponseBean res = (ResponseBean) args[0];
            try {
                if (TextUtils.equals(res.getErrCode(), RpcErrorCode.RPC_COMMON_ERROR + "")) {
                    hideLoadingDialog();
                    return;
                }
                if (res.getResult() != null && res.getResult().has("ipc_info")) {
                    JSONObject jsonObject = res.getResult().getJSONObject("ipc_info");
                    if (jsonObject.has("sn") && jsonObject.has("token")) {
                        String sn = jsonObject.getString("sn");
                        SunmiDevice device = ipcMap.get(sn);
                        if (device != null) {
                            device.setToken(jsonObject.getString("token"));
                            addDevice(device);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (id == OpcodeConstants.getIsWire) {
            ResponseBean res = (ResponseBean) args[0];
            try {
                if (TextUtils.equals(res.getErrCode(), RpcErrorCode.RPC_COMMON_ERROR + "")) {
                    hideLoadingDialog();
                    return;
                }
                if (res.getResult() != null && res.getResult().has("wire")
                        && res.getResult().getInt("wire") == 1
                        || res.getResult().has("wireless")
                        && res.getResult().getInt("wireless") == 1) {
                    gotoIpcConfigActivity();
                } else
                    gotoWifiConfigActivity();
            } catch (JSONException e) {
                hideLoadingDialog();
                e.printStackTrace();
            }
        }
    }

    @UiThread
    void addDevice(SunmiDevice device) {
        for (SunmiDevice sunmiDevice : ipcList) {
            if (TextUtils.equals(sunmiDevice.getDeviceid(), device.getDeviceid())) {
                return;
            }
        }
        ipcList.add(device);
        if (ipcListAdapter != null) {
            ipcListAdapter.notifyDataSetChanged();
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //1 udp搜索到设备
    private synchronized void ipcFound(SunmiDevice ipc) {
        if ((CommonConstants.TYPE_IPC_FS == deviceType
                && DeviceTypeUtils.getInstance().isSS1(ipc.getModel()))
                || (CommonConstants.TYPE_IPC_SS == deviceType
                && DeviceTypeUtils.getInstance().isFS1(ipc.getModel()))) {
            return;
        }
        if (!ipcMap.containsKey(ipc.getDeviceid())) {
            ipc.setSelected(true);
            ipcMap.put(ipc.getDeviceid(), ipc);
            // isApMode = TextUtils.equals("AP", ipc.getNetwork());
            getToken(ipc);
        }
    }

    //2 获取token
    private void getToken(SunmiDevice ipc) {
        IPCCall.getInstance().getToken(context, ipc.getIp());
    }

    private void getIsWire() {
        IPCCall.getInstance().getIsWire(context, ipcList.get(0).getIp());
    }

    private void gotoWifiConfigActivity() {
        hideLoadingDialog();
        if (ipcList != null && ipcList.size() > 0)
            WifiConfigActivity_.intent(context)
                    .deviceType(deviceType).sunmiDevice(ipcList.get(0)).shopId(shopId).source(source).start();
    }

    private void gotoIpcConfigActivity() {
        ArrayList<SunmiDevice> selectedList = new ArrayList<>();
        for (SunmiDevice device : ipcList) {
            if (device.isSelected())
                selectedList.add(device);
        }
        if (selectedList.size() > 0)
            IpcConfiguringActivity_.intent(context)
                    .deviceType(deviceType).sunmiDevices(selectedList).shopId(shopId).source(source).start();
    }

}
