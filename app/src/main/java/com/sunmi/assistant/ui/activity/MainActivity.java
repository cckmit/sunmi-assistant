package com.sunmi.assistant.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.receiver.MyNetworkCallback;
import com.sunmi.apmanager.rpc.mqtt.MQTTManager;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.MyApplication;
import com.sunmi.assistant.R;
import com.sunmi.assistant.utils.MainTab;
import com.sunmi.ipc.rpc.IPCCloudApi;
import com.sunmi.ipc.rpc.RetrofitClient;
import com.sunmi.ipc.rpc.mqtt.MqttManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.base.BaseApplication;
import sunmi.common.constant.CommonConstants;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.MyFragmentTabHost;

/**
 * main activity
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    @ViewById(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

    public static MainActivity instance = null;
    MyNetworkCallback networkCallback = new MyNetworkCallback();

    private long mExitTime;

    int currentTabIndex;// 当前fragment的index

    @AfterViews
    void init() {
        instance = this;
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        registerNetworkReceiver();
        CrashReport.setUserId(SpUtils.getUID());
        if (MyApplication.isCheckedToken)
            MQTTManager.getInstance().createEmqToken(true);//初始化长连接
        initIpc();
        if (TextUtils.isEmpty(SpUtils.getCompanyName())) {
            CommonUtils.logout();
            CommonUtils.gotoLoginActivity(context, "");
        } else {
            initTabs();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetworkReceiver();
    }

    //ipc初始化
    private void initIpc() {
        if (TextUtils.isEmpty(SpUtils.getSsoToken()))
            IPCCloudApi.getStoreToken(new RetrofitCallback() {
                @Override
                public void onSuccess(int code, String msg, Object data) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        SpUtils.setSsoToken(jsonObject.getString("store_token"));
                        RetrofitClient.createInstance();//初始化retrofit
                        MqttManager.getInstance().createEmqToken(true);//初始化ipc长连接
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(int code, String msg, Object data) {

                }
            });
        else {
            MqttManager.getInstance().createEmqToken(true);//初始化ipc长连接
        }
    }

    private void initTabs() {
        mTabHost.setup(context, getSupportFragmentManager(), R.id.fl_content);
        mTabHost.getTabWidget().setShowDividers(0);

        MainTab[] mainTabs = MainTab.values();
        for (MainTab mainTab : mainTabs) {
            if (SpUtils.getSaasExist() == 0 && TextUtils.equals(getString(mainTab.getResName()),
                    getString(R.string.ic_tab_data_title))) {//saas平台需要显示数据tab
                continue;
            }
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            TextView title = indicator.findViewById(R.id.tab_title);

            if (mainTab.getResIcon() != -1) {
                Drawable drawable = this.getResources().getDrawable(mainTab.getResIcon());
                title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
        }

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);
    }

    /**
     * 系统返回退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                shortTip(R.string.toast_press_again_quit);
                mExitTime = System.currentTimeMillis();
            } else {
                BaseApplication.getInstance().quit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabChanged(String tabId) {
        trackTabEvent(tabId);
        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
                if (TextUtils.equals(mTabHost.getCurrentTabTag(),
                        getString(R.string.str_tab_device))) {
                    BaseNotification.newInstance().postNotificationName(CommonConstants.tabDevice);
                } else if (TextUtils.equals(mTabHost.getCurrentTabTag(),
                        getString(R.string.str_support))) {
                    BaseNotification.newInstance().postNotificationName(CommonConstants.tabSupport);
                }
            } else {
                v.setSelected(false);
            }
        }
    }

    private void trackTabEvent(String tabId) {
        CommonUtils.trackCommonEvent(context,
                TextUtils.equals(getStringById(R.string.str_tab_device), tabId) ? "store" : "myinfo",
                "主页_" + tabId, Constants.EVENT_MAIN_PAGE);
    }

    /**
     * 初始化网络链接状态的监听 ，在没网络的时候提供更好的交互
     */
    private void registerNetworkReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.requestNetwork(new NetworkRequest.Builder().
                    build(), networkCallback);
        }
    }

    /**
     * 取消网络链接状态的监听
     */
    private void unRegisterNetworkReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{NotificationConstant.netConnectedMainActivity};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (NotificationConstant.netConnectedMainActivity == id) {
            MqttManager.getInstance().createEmqToken(true);
        }
    }

}
