package com.sunmi.assistant.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.rpc.ap.APCall;
import com.sunmi.assistant.R;
import com.sunmi.ipc.contract.IpcConfiguringContract;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.presenter.IpcConfiguringPresenter;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.rpc.mqtt.MqttManager;
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

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.view.TitleBarView;

/**
 * Description:
 * Created by bruce on 2019/4/24.
 */
@EActivity(R.layout.activity_search_ipc)
public class SunmiLinkSearchActivity extends BaseMvpActivity<IpcConfiguringPresenter>
        implements IpcConfiguringContract.View {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.nsv_ipc)
    NestedScrollView scrollView;
    @ViewById(R.id.rl_search)
    RelativeLayout rlSearch;
    @ViewById(R.id.rv_ipc)
    RecyclerView rvDevice;
    @ViewById(R.id.rl_no_device)
    RelativeLayout rlNoWifi;
    @ViewById(R.id.tv_1)
    TextView tvTipTitle;
    @ViewById(R.id.tip_select_multi)
    TextView tvSummary;
    @ViewById(R.id.tv_no_wifi)
    TextView tvNoWifi;
    @ViewById(R.id.tv_check_network)
    TextView tvCheckNetwork;
    @ViewById(R.id.rl_loading)
    RelativeLayout rlLoading;

    @Extra
    String shopId;
    @Extra
    String sn;
    @Extra
    boolean isSunmiLink;//是否是sunmiLink模式

    private static final int REQUEST_COMPLETE = 100;//配置完成

    IPCListAdapter devListAdapter;
    List<SunmiDevice> devList = new ArrayList<>();
    Set<String> devSet = new HashSet<>();

    private Timer timer = null;//定时器用于主动获取ap搜到的设备
    private TimerTask myTask = null;
    private boolean isTimeoutStart;

    @AfterViews
    void init() {
        mPresenter = new IpcConfiguringPresenter();
        mPresenter.attachView(this);
        titleBar.setAppTitle(R.string.str_search_nearby_sunmi_devices);
        tvTipTitle.setText(R.string.tip_title_choose_device);
        tvSummary.setText(R.string.tip_support_multi_select);
        tvNoWifi.setText(R.string.tip_no_device_found);
        tvCheckNetwork.setText(getString(R.string.tip_keep_device_in));
        startSunmiLink();
        initList();
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
        sunmiLinkBlock(devList);
        sunmiLinkConfig();
        stopSearch();
        rlLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopSearch();
    }

    private void stopSearch() {
        closeTimer();
        APCall.getInstance().searchStop(context, sn);//停止搜索商米设备
    }

    @Override
    public void ipcBindWifiSuccess(String sn) {
        MqttManager.getInstance().isConnect();
        setTimeout();
    }

    @Override
    public void ipcBindWifiFail(String sn, int code, String msg) {
        setDeviceStatus(sn, code);
        setTimeout();
    }

    @Override
    public void getIpcListSuccess(List<IpcListResp.SsListBean> ipcList) {

    }

    @Override
    public void getIpcListFail(int code, String msg) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_COMPLETE) {
            startSunmiLink();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.apSearchStart, NotificationConstant.apSearchStop,
                NotificationConstant.apGetSearchInfo, IpcConstants.ipcDiscovered, IpcConstants.bindIpc};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        ResponseBean res = (ResponseBean) args[0];
        if (NotificationConstant.apGetSearchInfo == id) {//sunmilink搜索的设备列表
            apGetSearchResult(res);
        } else if (id == IpcConstants.bindIpc) {//绑定结果的mqtt消息
            try {
                setDeviceStatus(res.getResult().getString("sn"), res.getDataErrCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //开启sunmi link搜索，时长120s
    private void startSunmiLink() {
        if (devList.size() > 0) {
            devList.clear();
            devListAdapter.notifyDataSetChanged();
        }
        rlNoWifi.setVisibility(View.GONE);
        rlSearch.setVisibility(View.VISIBLE);
        APCall.getInstance().searchStart(context, sn);//开始搜索商米设备
        new Handler().postDelayed(new Runnable() {
            public void run() {
                stopSearch();
                if (devList.size() <= 0) {
                    rlSearch.setVisibility(View.GONE);
                    rlNoWifi.setVisibility(View.VISIBLE);
                }
            }
        }, 120000);
        startGetDeviceInfoTimer();
    }

    //start Timer (轮询)获取搜索商米设备
    private void startGetDeviceInfoTimer() {
        timer = new Timer();
        timer.schedule(myTask = new TimerTask() {
            @Override
            public void run() {
                APCall.getInstance().getSearchInfo(context, sn);
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

    //为选中的设备加黑名单
    private void sunmiLinkBlock(List<SunmiDevice> list) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (SunmiDevice sunmiDevice : list) {
                if (!sunmiDevice.isSelected()) {
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
                APCall.getInstance().apSunmiMeshBlock(context, sn, jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //已选中的设备继续配置
    private void sunmiLinkConfig() {
        int ipcDeviceCount = 0;
        for (SunmiDevice sunmiDevice : devList) {
            if (TextUtils.equals("FS1", sunmiDevice.getModel())
                    || TextUtils.equals("SS1", sunmiDevice.getModel())) {
                ipcDeviceCount++;
                mPresenter.ipcBind(shopId, sunmiDevice.getDeviceid(), "", 1, 1);
            }
        }
        if (ipcDeviceCount == 0) {
            configComplete();
        }
    }

    void setTimeout() {
        if (!isTimeoutStart) {
            isTimeoutStart = true;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (devSet.isEmpty()) return;
                    for (SunmiDevice device : devList) {
                        if (devSet.contains(device.getDeviceid())) {
                            if (TextUtils.equals("FS1", device.getModel())
                                    || TextUtils.equals("SS1", device.getModel())) {
                                device.setStatus(RpcErrorCode.RPC_ERR_TIMEOUT);
                            }
                            devSet.remove(device.getDeviceid());
                        }
                    }
                    configComplete();
                }
            }, 30000);
        }
    }

    /**
     * 设置设备的绑定状态
     */
    private void setDeviceStatus(String sn, int status) {
        if (devSet.isEmpty()) return;
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

    /**
     * 配置完成，跳到结果页
     */
    private void configComplete() {
        hideLoadingDialog();
        APCall.getInstance().searchStop(context, sn);//停止搜索商米设备
        ArrayList<SunmiDevice> devicesChoose = new ArrayList<>();
        for (SunmiDevice device : devList) {
            if (device.isSelected())
                devicesChoose.add(device);
        }
        IpcConfigCompletedActivity_.intent(context).shopId(shopId).sunmiDevices(devicesChoose)
                .isSunmiLink(true).startForResult(REQUEST_COMPLETE);
    }

    private void apGetSearchResult(ResponseBean res) {
        if (TextUtils.equals(res.getErrCode(), "0")) {
            try {
                JSONObject objectResult = res.getResult();
                JSONObject jsonObject2 = objectResult.getJSONObject("sunmimesh");
                JSONArray array = jsonObject2.getJSONArray("devices");
                for (int i = 0; i < array.length(); i++) {
                    SunmiDevice sd = new SunmiDevice();
                    JSONObject object = (JSONObject) array.opt(i);
                    sd.setModel(object.getString("model"));
                    sd.setSelected(true);
                    String mac = object.getString("mac");
                    sd.setMac(mac);
                    if (object.has("devid")) {
                        String sn = object.getString("devid");
                        if (!TextUtils.isEmpty(sn)) {
                            sd.setDeviceid(sn);
                        } else {
                            sd.setDeviceid(mac);
                        }
                    } else {
                        sd.setDeviceid(mac);
                    }
                    sunmiLinkFoundDevice(sd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sunmiLinkFoundDevice(SunmiDevice device) {
        if (!devSet.contains(device.getDeviceid())) {
            device.setStatus(1);
            devSet.add(device.getDeviceid());
            addDevice(device);
        }
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
