package com.sunmi.assistant.ui.activity;

import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.rpc.ap.APCall;
import com.sunmi.assistant.R;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.view.IPCListAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;

/**
 * Description:
 * Created by bruce on 2019/4/24.
 */
@EActivity(R.layout.activity_search_ipc)
public class SunmiLinkSearchActivity extends BaseActivity {

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
    String sn;
    @Extra
    boolean isSunmiLink;//是否是sunmi link模式

    IPCListAdapter devListAdapter;
    List<SunmiDevice> devList = new ArrayList<>();
    List<SunmiDevice> devListUdp = new ArrayList<>();
    Set<String> devSet = new HashSet<>();
    private int ipcCountSunmiLinkSearched;

    private Timer timer = null;//定时器用于主动获取ap搜到的设备
    private TimerTask myTask = null;

    @AfterViews
    void init() {
        startSunmiLink();
        initList();
    }

    private void startSunmiLink() {
        rlNoWifi.setVisibility(View.GONE);
        rlSearch.setVisibility(View.VISIBLE);
        APCall.getInstance().searchStart(context, sn);//开始搜索商米设备
        new Handler().postDelayed(new Runnable() {
            public void run() {
                closeTimer();
                APCall.getInstance().searchStop(context, sn);//停止搜索商米设备
                if (devList.size() <= 0) {
                    rlSearch.setVisibility(View.GONE);
                    rlNoWifi.setVisibility(View.VISIBLE);
                }
            }
        }, 120000);
        startGetDeviceInfoTimer();
    }

    //start Timer
    private void startGetDeviceInfoTimer() {
        timer = new Timer();
        timer.schedule(myTask = new TimerTask() {
            @Override
            public void run() {
                APCall.getInstance().getSearchInfo(context, sn);//(轮询)获取搜索商米设备
            }
        }, 0, 4000);
    }

    //close Timer
    private void closeTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
    }

    @UiThread
    void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvDevice.setLayoutManager(layoutManager);
        rvDevice.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        devListAdapter = new IPCListAdapter(context, devList);
        rvDevice.setAdapter(devListAdapter);
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        startSunmiLink();
    }

    @Click(resName = "btn_config")
    void configClick() {
        if (devList == null || devList.size() < 1) return;
        sunmiLinkBlock();
        sunmiLinkConfig();
    }

    private void sunmiLinkBlock() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (SunmiDevice sunmiDevice : devList) {
                if (!sunmiDevice.isSelected()) {//todo
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("mac", sunmiDevice.getMac());
                    jsonArray.put(jsonObject1);
                }
            }
            if (jsonArray.length() > 0) {
                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("devices", jsonArray);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("sunmimesh", jsonObject2);
                APCall.getInstance().apSunmiMeshBlock(context, sn, jsonObject);//获取设备状态
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sunmiLinkConfig() {//todo sunmi link
//        udpScanIpc();
    }

//    private void udpScanIpc() {//todo sunmi link
//        SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered);
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//
//            }
//        }, 5000);
//    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.apSearchStart, NotificationConstant.apSearchStop,
                NotificationConstant.apGetSearchInfo, IpcConstants.ipcDiscovered};
    }

    Set<String> deviceIds = new HashSet<>();//以获取token的列表

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        ResponseBean res = (ResponseBean) args[0];
//        if (id == IpcConstants.ipcDiscovered) {
//            SunmiDevice ipc = (SunmiDevice) args[0];
//            if (devSet.contains(ipc.getMac())) {
//                devListUdp.add(ipc);
//            }
//            if (devListUdp.size() == ipcCountSunmiLinkSearched) {
//                bindIpcDevice();
//            }
//        } else if (id == IpcConstants.getIpcToken) {
//            if (res.getResult().has("ipc_info")) {
//                try {//"ipc_info":{"sn":"sn123456", "token":"fgu766fekjgllfkekajgiorag8tr..."}
//                    JSONObject jsonObject = res.getResult().getJSONObject("ipc_info");
//                    if (jsonObject.has("sn") && jsonObject.has("token")) {
//                        deviceIds.add(jsonObject.getString("sn"));
//                        IPCCloudApi.bindIPC(Integer.parseInt(shopId), IpcConstants.IPC_SN,
//                                jsonObject.getString("token"), 1, 1,
//                                new HttpCallback<Object>(null) {
//                                    @Override
//                                    public void onSuccess(int code, String msg, Object data) {
//
//                                    }
//                                });
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else
        if (id == IpcConstants.bindIpc) {

        } else if (NotificationConstant.apSearchStop == id) {//停止搜索

        } else if (NotificationConstant.apGetSearchInfo == id) {//搜索的设备列表
            apGetSearchResult(res);
        }
    }

    private void apGetSearchResult(ResponseBean res) {
//            LogCat.e(TAG, "888888 list = " + res.getReturnData());
        if (TextUtils.equals(res.getErrCode(), "0")) {
            try {
                JSONObject objectResult = res.getResult();
                JSONObject jsonObject2 = objectResult.getJSONObject("sunmimesh");
                JSONArray array = jsonObject2.getJSONArray("devices");
                for (int j = 0; j < array.length(); j++) {
                    SunmiDevice sd = new SunmiDevice();
                    JSONObject object = (JSONObject) array.opt(j);
                    String mac = object.getString("mac");
                    sd.setMac(mac);
                    sd.setDeviceid(mac);
                    if (object.has("sn")){

                    }
                    sd.setModel(object.getString("model"));
                    sd.setName(object.getString("model"));
//                        shortTip(object.getString("mac"));
                    sunmiLinkFoundDevice(sd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindIpcDevice() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
//                for (SunmiDevice device : sunmiDevices) {
//                    if (deviceIds.contains(device.getDeviceid())) {
//                        device.setStatus(RpcErrorCode.RPC_ERR_TIMEOUT);
//                    }
//                }
            }
        }, 10000);
    }

    private boolean sunmiLinkFoundDevice(SunmiDevice ipc) {
        if (devSet.contains(ipc.getDeviceid())) {
            return true;
        } else {
            IpcConstants.IPC_SN = ipc.getDeviceid();
            IpcConstants.IPC_IP = "http://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
            ipc.setSelected(true);
            devSet.add(ipc.getDeviceid());
            addDevice(ipc);
            if (TextUtils.equals("SS1", ipc.getModel())) {
                ipcCountSunmiLinkSearched++;
            }
        }
        return false;
    }

    @UiThread
    void addDevice(SunmiDevice device) {
        devList.add(device);
        if (devListAdapter != null) devListAdapter.notifyDataSetChanged();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

}
