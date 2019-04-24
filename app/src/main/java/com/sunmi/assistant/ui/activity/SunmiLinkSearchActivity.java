package com.sunmi.assistant.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.rpc.ap.APCall;
import com.sunmi.assistant.R;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.view.IPCListAdapter;
import com.sunmi.ipc.view.WifiConfigActivity_;

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

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.log.LogCat;

/**
 * Description:
 * Created by bruce on 2019/4/24.
 */
@EActivity(R.layout.activity_search_ipc)
public class SunmiLinkSearchActivity extends BaseActivity
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
    String sn;
    @Extra
    boolean isSunmiLink;//是否是sunmi link模式

    IPCListAdapter devListAdapter;
    List<SunmiDevice> devList = new ArrayList<>();
    Set<String> devSet = new HashSet<>();

    @AfterViews
    void init() {
        startScan();
        initApList();
    }

    private int timerNum;
    private Timer timer = null;
    private TimerTask myTask = null;

    //start Timer
    private void startTimer() {
        timer = new Timer();
        timer.schedule(myTask = new TimerTask() {
            @Override
            public void run() {
                APCall.getInstance().searchStop(context, sn);//停止搜索商米设备
            }
        }, 0, 1000);

        timer.schedule(myTask = new TimerTask() {
            @Override
            public void run() {
                APCall.getInstance().getSearchInfo(context, sn);//(轮询)获取搜索商米设备
            }
        }, 0, 4000);
    }

    //close Timer
    private void closeTimer() {
        timerNum = 0;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
    }

    private void startScan() {
        rlNoWifi.setVisibility(View.GONE);
        rlSearch.setVisibility(View.VISIBLE);
        APCall.getInstance().searchStart(context, sn);//开始搜索商米设备
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (devList.size() <= 0) {
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
        devListAdapter = new IPCListAdapter(context, devList);
        rvDevice.setAdapter(devListAdapter);
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        startScan();
    }

    @Click(resName = "btn_config")
    void configClick() {
        if (devList == null || devList.size() < 1) return;
        sunmiLinkConfig();
    }

    private void sunmiLinkConfig() {//todo sunmi link
        WifiConfigActivity_.intent(context).sunmiDevice(devList.get(0)).shopId(shopId).start();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.apSearchStart, NotificationConstant.apSearchStop,
                NotificationConstant.apGetSearchInfo};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        ResponseBean res = (ResponseBean) args[0];
        if (NotificationConstant.apSearchHistory == id) {//搜索的历史列表
            Message message = new Message();
            message.what = AppConfig.WHAT_SUNMI_HISTORY_SEARCH;
            message.obj = res;
        } else if (NotificationConstant.apDevItem == id) {//搜索的历史列表item名称
            Message message = new Message();
            message.what = AppConfig.WHAT_SUNMI_HISTORY_SEARCH_NAME;
            message.obj = res;
        } else if (NotificationConstant.apSearchStart == id) {//开始搜索
            Message message = new Message();
            message.what = AppConfig.WHAT_SUNMI_START;
            message.obj = res;
        } else if (NotificationConstant.apSearchStop == id) {//停止搜索

        } else if (NotificationConstant.apGetSearchInfo == id) {//搜索的设备列表
            if (TextUtils.equals(res.getErrCode(), "0")) {
                try {
                    JSONObject objectResult = res.getResult();
                    JSONObject jsonObject2 = objectResult.getJSONObject("sunmimesh");
                    JSONArray array = jsonObject2.getJSONArray("devices");

                    for (int j = 0; j < array.length(); j++) {
                        SunmiDevice sd = new SunmiDevice();
                        JSONObject object = (JSONObject) array.opt(j);
                        sd.setMac(object.getString("mac"));
                        sd.setDeviceid(object.getString("mac"));
                        sd.setModel(object.getString("model"));
                        sd.setName(object.getString("model"));
                        String sn = "";//todo
                        if (!devSet.contains(sn)) {
                            devSet.add(sn);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
        if (devSet.contains(ipc.getDeviceid())) {
            return true;
        } else {
            IpcConstants.IPC_SN = ipc.getDeviceid();
            IpcConstants.IPC_IP = "http://" + ipc.getIp() + "/api/";//192.168.100.159/api/192.168.103.122
            ipc.setSelected(true);
            devSet.add(ipc.getDeviceid());
            addDevice(ipc);
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
