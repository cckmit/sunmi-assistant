package com.sunmi.assistant.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.constant.enums.DeviceStatus;
import com.sunmi.apmanager.receiver.MyNetworkCallback;
import com.sunmi.apmanager.rpc.ap.APCall;
import com.sunmi.apmanager.ui.activity.config.PrimaryRouteStartActivity;
import com.sunmi.apmanager.ui.activity.router.RouterManagerNewActivity;
import com.sunmi.apmanager.ui.activity.router.RouterMangerActivity;
import com.sunmi.apmanager.utils.ApCompatibleUtils;
import com.sunmi.apmanager.utils.ApIsNewVersionUtils;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.DBUtils;
import com.sunmi.apmanager.utils.EncryptUtils;
import com.sunmi.apmanager.utils.pulltorefresh.library.PullToRefreshBase;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.DeviceContract;
import com.sunmi.assistant.data.response.AdListBean;
import com.sunmi.assistant.data.response.AdListResp;
import com.sunmi.assistant.presenter.DevicePresenter;
import com.sunmi.assistant.ui.DeviceSettingDialog;
import com.sunmi.assistant.ui.MainTopBar;
import com.sunmi.assistant.ui.adapter.DeviceListAdapter;
import com.sunmi.assistant.utils.GlideImageLoader;
import com.sunmi.cloudprinter.ui.Activity.PrinterManageActivity_;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.IpcSettingActivity_;
import com.sunmi.ipc.view.VideoPlayActivity_;
import com.sunmi.sunmiservice.WebViewActivity_;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpFragment;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotificationConstant;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.ChooseDeviceDialog;
import sunmi.common.view.dialog.CommonDialog;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
@EFragment(R.layout.fragment_device)
public class DeviceFragment extends BaseMvpFragment<DevicePresenter>
        implements DeviceContract.View, PullToRefreshBase.OnRefreshListener2<NestedScrollView>,
        DeviceListAdapter.OnDeviceClickListener, DeviceSettingDialog.OnSettingsClickListener,
        OnBannerListener, BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {

    @ViewById(R.id.shop_title)
    MainTopBar topBar;
    @ViewById(R.id.bga_refresh)
    BGARefreshLayout refreshView;
    @ViewById(R.id.rv_device)
    RecyclerView rvDevice;
    @ViewById(R.id.btn_add)
    TextView btnAdd;

    Banner vpBanner;
    RelativeLayout rlNoDevice;

    List<AdListBean> adList = new ArrayList<>();//广告
    private List<SunmiDevice> deviceList = new ArrayList<>();//设备列表全集
    List<SunmiDevice> routerList = new ArrayList<>();
    List<SunmiDevice> ipcList = new ArrayList<>();
    List<SunmiDevice> printerList = new ArrayList<>();

    private Timer timer = null, timerException = null;
    private DeviceListAdapter deviceListAdapter;
    private LinearLayoutManager layoutManager;
    private DeviceSettingDialog deviceSettingDialog;

    private Dialog dialogPassword = null;
    private String password = "";    //路由管理密码
    private String mPassword;
    private SunmiDevice clickedDevice;

    @AfterViews
    protected void init() {
        mPresenter = new DevicePresenter();
        mPresenter.attachView(this);
        adList.addAll(DataSupport.findAll(AdListBean.class));
        deviceList.addAll(DataSupport.findAll(SunmiDevice.class));
        for (SunmiDevice device : deviceList) {
            device.setStatus(DeviceStatus.UNKNOWN.ordinal());
        }
        initViews();
        loadData();
        startTimer();
    }

    protected void initViews() {
        topBar.setCompanyName(SpUtils.getCompanyName());
        topBar.setShopName(SpUtils.getShopName());
        initRefreshLayout();
        initBanner();
        layoutManager = new LinearLayoutManager(mActivity);
        rvDevice.setLayoutManager(layoutManager);
        deviceListAdapter = new DeviceListAdapter(mActivity, deviceList);
        View headerView = getLayoutInflater().inflate(R.layout.include_banner,
                (ViewGroup) refreshView.getParent(), false);
        deviceListAdapter.addHeaderView(headerView);
        rlNoDevice = headerView.findViewById(R.id.rl_empty);
        vpBanner = headerView.findViewById(R.id.vp_banner);
        headerView.findViewById(R.id.btn_add_device).setOnClickListener(this);
        deviceListAdapter.setClickListener(this);
        rvDevice.setAdapter(deviceListAdapter);
        showEmptyView();
        showLoadingDialog();

    }

    private void initRefreshLayout() {
        refreshView.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(getActivity(), false);
        refreshView.setRefreshViewHolder(refreshViewHolder);
        refreshView.setIsShowLoadingMoreView(false); // 设置正在加载更多时的文本
    }

    private void loadData() {
        mPresenter.getBannerList();
        mPresenter.getRouterList();
        mPresenter.getIpcList();
        mPresenter.getPrinterList();
    }

    @Override
    public void onClick(View v) {
        addClick();
    }

    @Click(R.id.btn_add)
    void addClick() {
        if (isFastClick(1500)) return;
        CommonUtils.trackCommonEvent(mActivity, "addDevice",
                "主页_店铺_添加设备", Constants.EVENT_MAIN_PAGE);
        CommonUtils.trackDurationEventBegin(mActivity, "bindDeviceDuration",
                "添加设备开始到立即绑定弹框", Constants.EVENT_DURATION_ADD_DEVICE);
        //选择设备
        ChooseDeviceDialog chooseDeviceDialog = new ChooseDeviceDialog(mActivity, SpUtils.getShopId());
        chooseDeviceDialog.show();
        AppConfig.globalDevList = routerList;//todo
    }

    @Override
    public void onStop() {
        super.onStop();
        closeTimer();
    }

    @Override
    public void getAdListSuccess(AdListResp adListResp) {
        adList.clear();
        adList.addAll(adListResp.getAd_list());
        initBanner();
    }

    @UiThread
    void initBanner() {
        vpBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);//设置banner样式
        vpBanner.setImageLoader(new GlideImageLoader());//设置图片加载器
        vpBanner.setImages(adList);//设置图片集合
        vpBanner.setBannerAnimation(Transformer.DepthPage);//设置banner动画效果
        vpBanner.setOnBannerListener(this);
        vpBanner.isAutoPlay(true);//设置自动轮播，默认为true
        vpBanner.setDelayTime(4000);//设置轮播时间
        vpBanner.setIndicatorGravity(BannerConfig.CENTER);//设置指示器位置（当banner模式中有指示器时）
        vpBanner.start();//banner设置方法全部调用完毕时最后调用
    }

    @Override
    public void getRouterListSuccess(List<SunmiDevice> devices) {
        routerList.clear();
        routerList.addAll(devices);
        refreshList();
    }

    @Override
    public void unbindRouterSuccess(String sn, int code, String msg, Object data) {

    }

    @Override
    public void getIpcListSuccess(List<SunmiDevice> devices) {
        ipcList.clear();
        ipcList.addAll(devices);
        refreshList();
    }

    @Override
    public void getPrinterListSuccess(List<SunmiDevice> devices) {
        printerList.clear();
        printerList.addAll(devices);
        refreshList();
    }

    @Override
    public void unbindIpcSuccess(int code, String msg, Object data) {
        mPresenter.getIpcList();
    }

    @Override
    public void unbindIpcFail(int code, String msg) {

    }

    @Override
    public void getPrinterStatusSuccess(SunmiDevice device) {

    }

    @Override
    public void unbindPrinterSuccess(String sn) {
        mPresenter.getPrinterList();
    }

    //关闭刷新状态
    @UiThread
    @Override
    public void endRefresh() {
        if (refreshView != null) {
            refreshView.endRefreshing();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
        if (NetworkUtils.isNetworkAvailable(mActivity)) {
            endRefresh();
            shortTip(R.string.toast_network_Exception);
        } else {
            loadData();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {

    }

    @Override
    public void onDeviceClick(SunmiDevice device) {
        if (isFastClick(1500)) return;
        if (cannotManagerDevice(device)) {
            return;
        }
        clickedDevice = device;
        if (TextUtils.equals(device.getType(), "ROUTER")) {
            showLoadingDialog();
            //校验ap是已初始化配置
            if (TextUtils.equals(device.getDeviceid(), MyNetworkCallback.CURRENT_ROUTER)) {
                APCall.getInstance().apIsConfig(mActivity, device.getDeviceid());
            } else {
                isComeRouterManager(device.getDeviceid(), device.getStatus());
            }
        } else if (TextUtils.equals(device.getType(), "IPC")) {
            if (TextUtils.isEmpty(device.getUid())) {
                shortTip(R.string.tip_play_fail);
            } else {
                VideoPlayActivity_.intent(mActivity).UID(device.getUid())
                        .deviceId(device.getId()).ipcType(device.getModel()).start();
            }
        } else if (TextUtils.equals(device.getType(), "PRINTER")) {
            PrinterManageActivity_.intent(mActivity).sn(device.getDeviceid()).userId(SpUtils.getUID())
                    .merchantId(SpUtils.getShopId() + "").channelId(device.getChannelId()).start();
        }
    }

    @Override
    public void onMoreClick(SunmiDevice item, int position) {
        if (deviceSettingDialog != null) {
            deviceSettingDialog.dismiss();
            deviceSettingDialog = null;
        } else {
            deviceSettingDialog = new DeviceSettingDialog(mActivity, item);
            deviceSettingDialog.setOnSettingsClickListener(this);
            deviceSettingDialog.show(layoutManager.findViewByPosition(position)
                    .findViewById(R.id.iv_more));
        }
    }

    private boolean cannotManagerDevice(SunmiDevice device) {
        if (device.getStatus() == DeviceStatus.UNKNOWN.ordinal()
                || device.getStatus() == DeviceStatus.OFFLINE.ordinal()) {
            shortTip(getString(R.string.str_cannot_manager_device));
            return true;
        }
        return false;
    }

    @Override
    public void onSettingsClick(SunmiDevice device, int type) {
        if (type == 0) {
            if (cannotManagerDevice(device)) {
                return;
            }
            onDeviceClick(device);
        } else if (type == 1) {
            deleteDevice(device);
        } else if (type == 2) {
            if (cannotManagerDevice(device)) {
                return;
            }
            IpcSettingActivity_.intent(mActivity).mDevice(device).start();
        }
    }

    @Override
    public void OnBannerClick(int position) {
        WebViewActivity_.intent(mActivity).url(adList.get(position).getLink()).start();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        loadData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{
                NotificationConstant.bindRouterChanged,
                NotificationConstant.apPostStatus, NotificationConstant.apStatusException,
                NotificationConstant.checkApPassword, IpcConstants.refreshIpcList,
                NotificationConstant.checkLogin, NotificationConstant.apisConfig,
                NotificationConstant.checkLoginAgain, CommonConstants.tabDevice,
                com.sunmi.cloudprinter.constant.Constants.NOTIFICATION_PRINTER_ADDED
        };
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.shopSwitched, CommonNotificationConstant.netConnected,
                CommonNotificationConstant.netDisconnection, NotificationConstant.updateConnectComplete,
                NotificationConstant.connectedTosunmiDevice, NotificationConstant.unBindRouterChanged};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == CommonConstants.tabDevice
                || NotificationConstant.bindRouterChanged == id
                || NotificationConstant.updateConnectComplete == id
                || CommonNotificationConstant.netConnected == id
                || NotificationConstant.unBindRouterChanged == id) {
            loadData();
        } else if (id == NotificationConstant.shopSwitched) {
            topBar.setShopName(SpUtils.getShopName());
            loadData();
        } else if (CommonNotificationConstant.netDisconnection == id) {//网络断开
            networkDisconnected();
        } else if (NotificationConstant.apStatusException == id) {//异常
            if (TextUtils.isEmpty(MyNetworkCallback.CURRENT_ROUTER)) return;
            devStatusChangeList(MyNetworkCallback.CURRENT_ROUTER, DeviceStatus.EXCEPTION);
        } else if (NotificationConstant.connectedTosunmiDevice == id) {//异常
            devStatusUnknownToException();
        } else if (NotificationConstant.apPostStatus == id) {//在线 离线 设备状态
            String msg = (String) args[0];
            if (TextUtils.isEmpty(msg)) return;
            try {
                JSONObject object = new JSONObject(msg);
                JSONArray jsonArray = object.getJSONArray("params");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                    int event = EncryptUtils.decodeOp(jsonObject.getString("event"));
                    if (NotificationConstant.apOnline == event) {
                        JSONObject object1 = jsonObject.getJSONObject("param");
                        String sn = object1.getString("sn");
                        devStatusChangeList(sn, DeviceStatus.ONLINE); //在线状态
                    } else if (NotificationConstant.apOffline == event) {
                        JSONObject object1 = jsonObject.getJSONObject("param");
                        String sn = object1.getString("sn");
                        devStatusChangeList(sn, DeviceStatus.OFFLINE); //离线状态

                        //发送广播
                        if (!TextUtils.equals(sn, AppConfig.GLOBAL_SN)) return;
                        Intent intent = new Intent();
                        intent.setAction(AppConfig.BROADCAST_ACTION);
                        intent.putExtra("type", AppConfig.BROADCAST_STATUS);
                        mActivity.sendBroadcast(intent);
                    } else if (NotificationConstant.apStatusList == event) {
                        JSONObject object1 = jsonObject.getJSONObject("param");
                        JSONArray jsonArrayList = object1.getJSONArray("device_list");//所有设备列表
                        routerList.clear();
                        for (int j = 0; j < jsonArrayList.length(); j++) {
                            JSONObject object2 = (JSONObject) jsonArrayList.opt(j);
                            int shopId = object2.getInt("ShopId");
                            if (shopId == SpUtils.getShopId()) {
                                SunmiDevice device = new SunmiDevice();
                                device.setDeviceid(object2.getString("Sn"));
                                device.setShopId(shopId);
                                device.setName("SUNMI-W1");
                                device.setModel("W1");
                                device.setStatus(object2.getInt("ActiveStatus"));
                                device.setType("ROUTER");
                                routerList.add(device);
                            }
                        }
                        refreshList();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (NotificationConstant.shopNameChanged == id) {
            topBar.setShopName(SpUtils.getShopName());
        } else if (NotificationConstant.apisConfig == id) {//ap是否配置2034
            ResponseBean res = (ResponseBean) args[0];
            checkApIsConfig(res);
        } else if (NotificationConstant.checkApPassword == id) {
            ResponseBean res = (ResponseBean) args[0];
            checkApLoginMangerPsd(res);
        } else if (NotificationConstant.checkLogin == id) {
            ResponseBean res = (ResponseBean) args[0];
            checkApLoginMangerPsd(res);
        } else if (NotificationConstant.checkLoginAgain == id) {
            ResponseBean res = (ResponseBean) args[0];
            apLoginCheckPsdAgain(res);
        } else if (com.sunmi.cloudprinter.constant.Constants.NOTIFICATION_PRINTER_ADDED == id) {
            mPresenter.getPrinterList();
        } else if (IpcConstants.refreshIpcList == id) {
            mPresenter.getIpcList();
        }
    }

    @UiThread
    void saveMangerPasswordDialog(String type) {
        if (mActivity == null) return;
        hideLoadingDialog();
        if (dialogPassword != null) {
            dialogPassword = null;
            return;
        }
        dialogPassword = new Dialog(mActivity, R.style.Son_dialog);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_manger_password, null);
        final ClearableEditText etPassword = view.findViewById(R.id.et_password);
        TextView tvContent = view.findViewById(R.id.tvContent);
        if (TextUtils.equals(type, "0")) {
            tvContent.setText(R.string.curr_router_manager_password);
        } else {
            tvContent.setText(R.string.curr_router_error_password);
        }
        password = "";
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dialogPassword.dismiss();
            dialogPassword = null;
        });
        view.findViewById(R.id.btnSure).setOnClickListener(v -> {
            if (isFastClick(1500)) return;
            password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                shortTip(R.string.hint_input_manger_password);
                return;
            }
            APCall.getInstance().checkLoginAgain(mActivity, password);//todo opcode
        });
        dialogPassword.setContentView(view);
        dialogPassword.setCancelable(false);
        dialogPassword.show();
    }

    //设备ap登录，检测管理密码item  dialogPassword
    private void apLoginCheckPsdAgain(ResponseBean res) {
        LogCat.e(TAG, "what_ap_login_check_mangerPsd  again>>>" + res);
        try {
            if (TextUtils.equals(res.getErrCode(), "0")) {//成功
                JSONObject object2 = res.getResult();
                JSONObject object3 = object2.getJSONObject("account");
                String token = object3.getString("token");
                SpUtils.saveRouterToken(token);//保存token
                DBUtils.saveLocalMangerPassword(clickedDevice.getDeviceid(), password);//保存本地管理密码
                dialogPasswordDismiss();
                checkApVersion(clickedDevice.getDeviceid(), clickedDevice.getStatus());
//                gotoRouterManager();
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_ERROR)) {// 账户密码错误
                shortTip(getString(R.string.tip_password_error));
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_INCORRECT_MANY)) { // 账户密码错误次数过多
                shortTip(R.string.tip_password_fail_too_often);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void dialogPasswordDismiss() {
        if (dialogPassword != null) {
            dialogPassword.dismiss();
            dialogPassword = null;
        }
    }

    //检测ap是否配置
    private void checkApIsConfig(ResponseBean res) {
        try {
            if (TextUtils.equals("0", res.getErrCode())) {
                JSONObject object = res.getResult();
                JSONObject object1 = object.getJSONObject("system");
                String factory = object1.getString("factory");
                if (TextUtils.equals("0", factory)) {//0已初始配置 1未初始化设置
                    isComeRouterManager(clickedDevice.getDeviceid(), clickedDevice.getStatus());
                } else {
                    openActivity(mActivity, PrimaryRouteStartActivity.class);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //设备ap登录，检测管理密码item
    private void checkApLoginMangerPsd(ResponseBean res) {
        LogCat.e(TAG, "checkApLoginMangerPsd res = " + res);
        try {
            if (TextUtils.equals(res.getErrCode(), "0")) {//成功
                JSONObject object2 = res.getResult();
                JSONObject object3 = object2.getJSONObject("account");
                String token = object3.getString("token");
                SpUtils.saveRouterToken(token);//保存token
                SpUtils.saveRouterMangerPassword(mPassword);//保存管理密码
                //SpUtils.saveApMangerMsg(mSn, mPassword);
                //跳转路由器管理页面
//                gotoRouterManager();
                checkApVersion(clickedDevice.getDeviceid(), clickedDevice.getStatus());
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_ERROR)) {// 账户密码错误
                dialogPassword = null;
                saveMangerPasswordDialog("1"); //检测管理密码是否为空或丢失dialog
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_INVALID)) {// 账户登录缺少密码
                dialogPassword = null;
                saveMangerPasswordDialog("0");
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_INCORRECT_MANY)) { // 账户密码错误次数过多
                shortTip(R.string.tip_password_fail_too_often);
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_UNSET_PASSWORD)) { // 账户密码未设置
                openActivity(mActivity, PrimaryRouteStartActivity.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void devStatusChangeList(String currentRouter, DeviceStatus status) {
        for (SunmiDevice device : deviceList) {
            if (TextUtils.equals(device.getDeviceid(), currentRouter)) {
                device.setStatus(status.ordinal());
                deviceListRefresh();
                return;
            }
        }
    }

    private void devStatusUnknownToException() {
        if (TextUtils.isEmpty(MyNetworkCallback.CURRENT_ROUTER)) return;
        for (SunmiDevice device : deviceList) {
            if (TextUtils.equals(device.getDeviceid(), MyNetworkCallback.CURRENT_ROUTER)) {
                if (device.getStatus() == DeviceStatus.UNKNOWN.ordinal()
                        || device.getStatus() == DeviceStatus.OFFLINE.ordinal()) {
                    device.setStatus(DeviceStatus.EXCEPTION.ordinal());
                    deviceListRefresh();
                    return;
                }
            }
        }
    }

    @UiThread
    void deviceListRefresh() {
        deviceListAdapter.notifyDataSetChanged();
    }

    private void startTimerExceptionRefreshList() {
        timerException = new Timer();
        timerException.schedule(new TimerTask() {
            @Override
            public void run() {
//                getBindList(false);//刷新列表
            }
        }, 0, 1000 * 30);
    }

    private void closeTimerExceptionRefreshList() {
        if (timerException != null) {
            timerException.cancel();
            timerException = null;
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mPresenter.getIpcList();
                mPresenter.getPrinterList();
            }
        }, 30000, 120000);
    }

    private void closeTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @UiThread
    void refreshList() {
        hideLoadingDialog();
        if (deviceList == null) return;

        endRefresh();
        deviceList.clear();
        deviceList.addAll(ipcList);
        deviceList.addAll(routerList);
        deviceList.addAll(printerList);
        showEmptyView();
        deviceListRefresh();
    }

    @UiThread
    void showEmptyView() {
        if (rlNoDevice == null) return;
        if (deviceList.size() > 0) {
            rlNoDevice.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
        } else {
            rlNoDevice.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
        }
    }

    @UiThread
    void networkDisconnected() {
        hideLoadingDialog();
        for (SunmiDevice device : deviceList) {
            device.setStatus(DeviceStatus.UNKNOWN.ordinal());
        }
        deviceListRefresh();
        endRefresh();
    }

    private void isComeRouterManager(String sn, int status) {
        //1通过sn号判断是否当前路由器WiFi
        if (TextUtils.equals(sn, MyNetworkCallback.CURRENT_ROUTER)) {
            //2通过查询数据库管理密码check登录（管理密码是否正确）
            mPassword = DBUtils.queryApPassword(sn);//查询数据库保存的路由器密码
            APCall.getInstance().checkLogin(mActivity, mPassword);
        } else {
            checkApVersion(sn, status);
        }
    }

    //检查ap版本
    private void checkApVersion(String sn, int status) {
        ApCompatibleUtils.getInstance().checkVersion(mActivity, sn, (isCompatible, currSn) -> {
            gotoRouterManager(sn, DeviceStatus.valueOf(status).getValue());
        });
    }

    private void gotoRouterManager(String sn, String status) {
        Bundle bundle = new Bundle();
        bundle.putString("shopId", SpUtils.getShopId() + "");
        bundle.putString("sn", sn);
        bundle.putString("status", status);
        if (ApIsNewVersionUtils.isNewVersion())
            openActivity(mActivity, RouterManagerNewActivity.class, bundle);
        else
            openActivity(mActivity, RouterMangerActivity.class, bundle);
    }

    private void deleteDevice(SunmiDevice device) {
        String msg = "";
        if (TextUtils.equals(device.getType(), "ROUTER")) {
            msg = getString(R.string.str_unbind_ap_dialog_tip);
        } else if (TextUtils.equals(device.getType(), "IPC")) {
            msg = getString(R.string.tip_delete_ipc);
        } else if (TextUtils.equals(device.getType(), "PRINTER")) {
            msg = getString(R.string.tip_delete_printer);
        }
        new CommonDialog.Builder(mActivity).setMessage(msg)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_delete, R.color.read_deep_more,
                        (dialog, which) -> {
                            dialog.dismiss();
                            if (-1 == NetworkUtils.getNetStatus(mActivity)) {
                                unBindNetDisConnected();
                                return;
                            }
                            if (TextUtils.equals(device.getType(), "ROUTER")) {
                                mPresenter.unbindRouter(device.getDeviceid());
                            } else if (TextUtils.equals(device.getType(), "IPC")) {
                                mPresenter.unbindIPC(device.getId());
                            } else if (TextUtils.equals(device.getType(), "PRINTER")) {
                                mPresenter.unBindPrinter(device.getDeviceid());
                            }
                        }).create().show();
    }

    //无网络
    private void unBindNetDisConnected() {
        new CommonDialog.Builder(mActivity)
                .setMessage(getString(R.string.str_dialog_net_disconnected))
                .setCancelButton(R.string.str_confirm, (dialog, which) -> dialog.dismiss()).create().show();
    }

}
