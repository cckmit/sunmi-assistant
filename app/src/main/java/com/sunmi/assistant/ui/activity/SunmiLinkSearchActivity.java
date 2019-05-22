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
import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.view.IPCListAdapter;
import com.sunmi.ipc.view.IpcConfigCompletedActivity_;

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
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.SpUtils;

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
    //    List<SunmiDevice> devListUdp = new ArrayList<>();
    Set<String> devSet = new HashSet<>();
    //    private int ipcCountSunmiLinkSearched;
    int ipcCount = 0, ipcBoundCount = 0;

    private Timer timer = null;//定时器用于主动获取ap搜到的设备
    private TimerTask myTask = null;

    @AfterViews
    void init() {
        startSunmiLink();
        initList();
    }

    //开启sunmi link搜索
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

    @Click(resName = "btn_retry")
    void refreshClick() {
        startSunmiLink();
    }

    @Click(resName = "btn_config")
    void configClick() {
        if (devList == null || devList.size() < 1) return;
        showLoadingDialog();
        sunmiLinkBlock();
        sunmiLinkConfig();
        setTimeout();
    }

    //为选中的设备加黑名单
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

    //以选中的设备继续配置
    private void sunmiLinkConfig() {
        for (SunmiDevice sunmiDevice : devList) {
            if (TextUtils.equals("FS1", sunmiDevice.getModel())
                    || TextUtils.equals("SS1", sunmiDevice.getModel()))
                bindIpc(sunmiDevice);
        }
    }

    private void bindIpc(SunmiDevice sunmiDevice) {
        ipcCount++;
        IPCCloudApi.bindIPC(SpUtils.getMerchantUid(), shopId, sunmiDevice.getDeviceid(), 1,
                "", 1, 1, new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                    }
                });
    }

    void setTimeout() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (devSet.isEmpty()) return;
                for (SunmiDevice device : devList) {
                    if (devSet.contains(device.getDeviceid())) {
                        device.setStatus(RpcErrorCode.RPC_ERR_TIMEOUT);
                        devSet.remove(device.getDeviceid());
                    }
                }
                configComplete();
            }
        }, 30000);
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.apSearchStart, NotificationConstant.apSearchStop,
                NotificationConstant.apGetSearchInfo, IpcConstants.ipcDiscovered};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        ResponseBean res = (ResponseBean) args[0];
        if (NotificationConstant.apGetSearchInfo == id) {//搜索的设备列表
            apGetSearchResult(res);
        } else if (id == IpcConstants.bindIpc) {
            try {
                setDeviceStatus(res.getResult().getString("sn"), res.getDataErrCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (NotificationConstant.apSearchStop == id) {//停止搜索
        }
    }

    private void setDeviceStatus(String sn, int status) {
        for (SunmiDevice device : devList) {
            if (TextUtils.equals(device.getDeviceid(), sn)) {
                device.setStatus(status);
                devSet.remove(sn);
            }
        }
        if (devSet.isEmpty()) {
            closeTimer();
            configComplete();
        }
    }

    private void configComplete() {
        ArrayList<SunmiDevice> devicesChoose = new ArrayList<>();
        for (SunmiDevice device : devList) {
            if (device.isSelected())
                devicesChoose.add(device);
        }
        hideLoadingDialog();
        APCall.getInstance().searchStop(context, sn);//停止搜索商米设备
        IpcConfigCompletedActivity_.intent(context).shopId(shopId).sunmiDevices(devicesChoose).start();
    }

    private void apGetSearchResult(ResponseBean res) {
        if (TextUtils.equals(res.getErrCode(), "0")) {
            try {
                JSONObject objectResult = res.getResult();
                JSONObject jsonObject2 = objectResult.getJSONObject("sunmimesh");
                JSONArray array = jsonObject2.getJSONArray("devices");
                for (int j = 0; j < array.length(); j++) {
                    SunmiDevice sd = new SunmiDevice();
                    JSONObject object = (JSONObject) array.opt(j);
                    sd.setModel(object.getString("model"));
                    sd.setSelected(true);
                    String mac = object.getString("mac");
                    sd.setMac(mac);
                    if (object.has("devid")) {
                        sd.setDeviceid(object.getString("devid"));
                    }
                    sunmiLinkFoundDevice(sd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean sunmiLinkFoundDevice(SunmiDevice device) {
        if (devSet.contains(device.getDeviceid())) {
            return true;
        } else {
            devSet.add(device.getDeviceid());
            addDevice(device);
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
