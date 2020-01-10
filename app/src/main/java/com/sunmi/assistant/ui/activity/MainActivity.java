package com.sunmi.assistant.ui.activity;

import android.content.Context;
import android.content.Intent;
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

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.receiver.MyNetworkCallback;
import com.sunmi.apmanager.rpc.mqtt.MQTTManager;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.MyApplication;
import com.sunmi.assistant.R;
import com.sunmi.assistant.dashboard.DashboardFragment;
import com.sunmi.assistant.mine.MineFragment;
import com.sunmi.assistant.mine.MineFragment_;
import com.sunmi.assistant.mine.contract.MainContract;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.presenter.MainPresenter;
import com.sunmi.assistant.utils.MainTab;
import com.sunmi.sunmiservice.SupportFragment;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import cn.bingoogolapple.badgeview.BGABadgeTextView;
import me.leolin.shortcutbadger.ShortcutBadger;
import sunmi.common.base.BaseApplication;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.constant.RouterConfig;
import sunmi.common.notification.BaseNotification;
import sunmi.common.router.AppApi;
import sunmi.common.rpc.mqtt.MqttManager;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.ThreadPool;
import sunmi.common.view.MyFragmentTabHost;

/**
 * main activity
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends BaseMvpActivity<MainPresenter>
        implements TabHost.OnTabChangeListener, MainContract.View {

    @ViewById(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;

    MyNetworkCallback networkCallback = new MyNetworkCallback();
    @Extra
    int currentTabIndex;// 要显示的fragment的index
    private long mExitTime;
    private BGABadgeTextView mineTitle;

    @RouterAnno(
            path = RouterConfig.App.MAIN
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), MainActivity_.class);
        return intent;
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        if (!CommonHelper.isGooglePlay()) {
            mPresenter.getMessageCount();
            ThreadPool.getCachedThreadPool().submit(() -> mPresenter.syncIpcDevice());
        }
        registerNetworkReceiver();
        CrashReport.setUserId(SpUtils.getUID());

        if (MyApplication.isCheckedToken) {
            MQTTManager.getInstance().createEmqToken(true);//初始化长连接
            initIpc();
        }
        if (TextUtils.isEmpty(SpUtils.getCompanyName())) {
            Router.withApi(AppApi.class).goToLogin(context, "");
        } else {
            initTabs();
            initMessageBadge();
            ShortcutBadger.applyCount(BaseApplication.getInstance(), SpUtils.getRemindUnreadMsg()); //for 1.1.4+
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMessageBadge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetworkReceiver();
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
                if (TextUtils.equals(mTabHost.getCurrentTabTag(), getString(R.string.str_tab_device))) {
                    // 切换到设备页
                    BaseNotification.newInstance().postNotificationName(CommonConstants.tabDevice);
                }
                String serviceTab = getString(R.string.str_tab_support);
                if (TextUtils.equals(mTabHost.getCurrentTabTag(), serviceTab)) {
                    // 切换到服务页
                    SupportFragment fragment = (SupportFragment) getFragment(serviceTab);
                    if (fragment != null) {
                        fragment.refresh();
                    }
                }
            } else {
                v.setSelected(false);
            }
        }
    }

    private void initStatusBar(String tabId) {
        if (TextUtils.equals(getStringById(R.string.str_tab_dashboard), tabId)) {
            DashboardFragment fragment = (DashboardFragment) getFragment(
                    getString(R.string.str_tab_dashboard));
            if (fragment != null) {
                fragment.updateStatusBar();
            }
        } else if (TextUtils.equals(getStringById(R.string.str_tab_device), tabId)) {
            StatusBarUtils.setStatusBarFullTransparent(this);
        } else {
            StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.homePageBadgeUpdate, CommonNotifications.pushMsgArrived};
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{NotificationConstant.netConnectedMainActivity,
                CommonNotifications.importShop};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (NotificationConstant.netConnectedMainActivity == id) {
            initIpc();
        } else if (CommonNotifications.importShop == id) {
            if (mTabHost.getChildCount() == 4) {
                return;
            }
            initTabs();
        } else if (CommonNotifications.homePageBadgeUpdate == id
                || CommonNotifications.pushMsgArrived == id) {
            initMessageBadge();
        }
    }

    @Override
    public void getMessageCountSuccess(MessageCountBean data) {
        initMessageBadge();
    }

    @Override
    public void getMessageCountFail(int code, String msg) {

    }

    //ipc初始化
    private void initIpc() {
        MqttManager.getInstance().createEmqToken(true);//初始化ipc长连接
    }

    void initTabs() {
        mTabHost.setup(context, getSupportFragmentManager(), R.id.fl_content);
        mTabHost.getTabWidget().setShowDividers(0);
        if (mTabHost.getChildCount() > 0) {
            mTabHost.clearAllTabs();
        }
        MainTab[] mainTabs = MainTab.values();
        for (MainTab mainTab : mainTabs) {
            if (isHideTab(mainTab.getResName())) {
                continue;
            }
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            BGABadgeTextView title = indicator.findViewById(R.id.tab_title);

            if (mainTab.getResIcon() != -1) {
                Drawable drawable = this.getResources().getDrawable(mainTab.getResIcon());
                title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }
            title.setText(getString(mainTab.getResName()));
            if (mainTab.getClz() == MineFragment_.class) {
                mineTitle = title;
            }
            tab.setIndicator(indicator);
            tab.setContent(tag -> new View(context));
            mTabHost.addTab(tab, mainTab.getClz(), null);
        }

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);
    }

    private Fragment getFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    @UiThread
    void initMessageBadge() {
        if (mineTitle == null) {
            return;
        }
        final Fragment fragment = getFragment(getString(R.string.str_tab_mine));
        if (fragment != null && fragment instanceof MineFragment) {
            ((MineFragment) fragment).setMsgBadge();
        }
        if (SpUtils.getUnreadMsg() > 0) {
            int count = SpUtils.getRemindUnreadMsg();
            if (count <= 0) {
                mineTitle.showCirclePointBadge();
            } else if (count > 99) {
                mineTitle.showTextBadge("99+");
            } else {
                mineTitle.showTextBadge(String.valueOf(count));
            }
        } else {
            mineTitle.hiddenBadge();
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

    private boolean isHideTab(int tabNameRes) {
        return CommonHelper.isGooglePlay() &&
                (TextUtils.equals(getString(tabNameRes), getString(R.string.str_tab_dashboard))
                        || TextUtils.equals(getString(tabNameRes), getString(R.string.str_tab_support)));
    }

}
