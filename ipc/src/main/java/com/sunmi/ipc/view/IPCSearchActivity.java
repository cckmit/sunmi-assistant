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

import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.rpc.IpcConstants;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
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

    private Map<String, SunmiDevice> ipcMap = new HashMap<>();
    Set<String> ipcSet = new HashSet<>();
    int selectCount, tokenGetCount;

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
        }, 3000);
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
//        if (ipcList == null || ipcList.size() < 1) return;
//        getToken();
//        if (isApMode) {
//            getIsWire();
//        } else {
//            gotoIpcConfigActivity();
//        }

//        public static void bindIPC(int companyId, int shopId, String sn, int bindMode, String bindToken,
//        float longitude, float latitude, RetrofitCallback callback) {
        IPCCloudApi.getDetailList(6680, shopId, new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {

            }

            @Override
            public void onFail(int code, String msg, Object data) {

            }
        });

    }

    private void gotoWifiConfigActivity() {
        if (selectCount == tokenGetCount) {
            WifiConfigActivity_.intent(context).sunmiDevice(ipcList.get(0)).shopId(shopId).start();
        } else {
            shortTip("请求失败请重试");
        }
    }

    private void gotoIpcConfigActivity() {
        if (selectCount == tokenGetCount) {
            IpcConfiguringActivity_.intent(context).sunmiDevices((ArrayList<SunmiDevice>) ipcList).shopId(shopId).start();
        } else {
            shortTip("请求失败请重试");
        }
    }

    private void getToken() {
        for (SunmiDevice sunmiDevice : ipcList) {
            if (sunmiDevice.isSelected()) {
                selectCount++;
                IPCCall.getInstance().getToken(context);
            }
        }
    }

    private void getIsWire() {
        IPCCall.getInstance().getIsWire(context);
    }

    private void sunmiLinkConfig() {//todo sunmi link
        WifiConfigActivity_.intent(context).sunmiDevice(ipcList.get(0)).shopId(shopId).start();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{IpcConstants.ipcDiscovered,
                IpcConstants.getIpcToken, IpcConstants.getIsWire};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == IpcConstants.ipcDiscovered) {
            SunmiDevice ipc = (SunmiDevice) args[0];
            ipcFound(ipc);
        } else if (id == IpcConstants.getIpcToken) {
            ResponseBean res = (ResponseBean) args[0];
            try {
                if (res.getResult().has("ipc_info")) {
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
        } else if (id == IpcConstants.getIsWire) {
            ResponseBean res = (ResponseBean) args[0];
            try {
                LogCat.e(TAG, "getIsWire res = " + res);
                if (res.getResult().has("ipc_info")) {
                    JSONObject jsonObject = res.getResult().getJSONObject("ipc_info");
                    if (jsonObject.has("sn") && jsonObject.has("token")) {
                        for (SunmiDevice sunmiDevice : ipcList) {
                            tokenGetCount++;
                            sunmiDevice.setToken(jsonObject.getString("token"));
                        }
                    }
                }
                gotoWifiConfigActivity();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //1 udp搜索到设备
    private void ipcFound(SunmiDevice ipc) {
        if (TextUtils.equals("SS1", ipc.getModel()) || TextUtils.equals("FS1", ipc.getModel())) {
            LogCat.e(TAG, "ipcFound fs ipcdevice = " + ipc.toString());
            if (!ipcMap.containsKey(ipc.getDeviceid())) {
                ipc.setSelected(true);
                isApMode = TextUtils.equals("AP", ipc.getNetwork());
                ipcMap.put(ipc.getDeviceid(), ipc);
                getToken(ipc);
            }
        }
    }

    //2 获取token
    private void getToken(SunmiDevice ipc) {
        IPCCall.getInstance().getToken(context, ipc.getIp());
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

    //    private boolean hasFound(SunmiDevice ipc) {
    //        if (ipcSet.contains(ipc.getDeviceid())) {
    //            return true;
    //        } else {
    //            ipc.setSelected(true);
    //            isApMode = TextUtils.equals("AP", ipc.getNetwork());
    //            if (isApMode) {
    //                IpcConstants.IPC_SN = ipc.getDeviceid();
    //                IpcConstants.IPC_IP = "http://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
    //            }
    //            ipcSet.add(ipc.getDeviceid());
    //            addDevice(ipc);
    //        }
    //        return false;
    //    }

}
