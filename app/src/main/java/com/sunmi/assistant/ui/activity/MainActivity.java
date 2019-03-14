package com.sunmi.assistant.ui.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.receiver.MyNetworkCallback;
import com.sunmi.apmanager.rpc.mqtt.MQTTManager;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SpUtils;
import com.sunmi.assistant.MyApplication;
import com.sunmi.assistant.R;
import com.sunmi.assistant.utils.MainTab;
import com.tencent.bugly.crashreport.CrashReport;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.base.BaseApplication;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.MyFragmentTabHost;

/**
 * main activity
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements TabHost.OnTabChangeListener {
    public static final int TAB_STORE = 0;
    public static final int TAB_SUPPORT = 1;
    public static final int TAB_MINE = 2;

    @ViewById(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

    public static MainActivity instance = null;
    MyNetworkCallback networkCallback = new MyNetworkCallback();

    private long mExitTime;

    int currentTabIndex;// 当前fragment的index

    @AfterViews
    void init() {
        instance = this;
        registerNetworkReceiver();
        CrashReport.setUserId(SpUtils.getUID());
        if (MyApplication.isCheckedToken)
            MQTTManager.getInstance().createEmqToken(true);//初始化长连接
        initTabs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetworkReceiver();
    }

    private void initTabs() {
        mTabHost.setup(context, getSupportFragmentManager(), R.id.fl_content);
        mTabHost.getTabWidget().setShowDividers(0);
        MainTab[] mainTabs = MainTab.values();
        for (MainTab mainTab : mainTabs) {
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

        mTabHost.setCurrentTab(TAB_STORE);
        currentTabIndex = TAB_STORE;
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
        initStatusBar(tabId);
        final int size = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                v.setSelected(true);
                currentTabIndex = i;
            } else {
                v.setSelected(false);
            }
        }
    }

    private void initStatusBar(String tabId) {
        if (TextUtils.equals(getStringById(R.string.str_store), tabId)) {
            HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        } else {
            StatusBarUtils.setStatusBarColor(this,
                    StatusBarUtils.TYPE_DARK);//状态栏
        }
    }

    private void trackTabEvent(String tabId) {
        CommonUtils.trackCommonEvent(context,
                TextUtils.equals(getStringById(R.string.str_store), tabId) ? "store" : "myinfo",
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

    private Fragment getFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

}
