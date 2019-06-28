package com.sunmi.assistant.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.constant.enums.DeviceStatus;
import com.sunmi.apmanager.receiver.MyNetworkCallback;
import com.sunmi.apmanager.rpc.ap.APCall;
import com.sunmi.apmanager.ui.activity.config.PrimaryRouteStartActivity;
import com.sunmi.apmanager.ui.activity.router.RouterManagerNewActivity;
import com.sunmi.apmanager.ui.activity.router.RouterMangerActivity;
import com.sunmi.apmanager.ui.adapter.StorePagerAdapter;
import com.sunmi.apmanager.utils.ApCompatibleUtils;
import com.sunmi.apmanager.utils.ApIsNewVersionUtils;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.DBUtils;
import com.sunmi.apmanager.utils.EncryptUtils;
import com.sunmi.apmanager.utils.pulltorefresh.library.PullToRefreshBase;
import com.sunmi.apmanager.utils.pulltorefresh.library.PullToRefreshScrollView1;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.DeviceContract;
import com.sunmi.assistant.data.response.AdListResp;
import com.sunmi.assistant.presenter.DevicePresenter;
import com.sunmi.assistant.ui.DeviceSettingDialog;
import com.sunmi.assistant.ui.adapter.DeviceListAdapter;
import com.sunmi.cloudprinter.ui.Activity.PrinterManageActivity_;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.view.VideoPlayActivity_;
import com.sunmi.sunmiservice.WebViewActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.constant.CommonConstants;
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
        DeviceListAdapter.OnDeviceClickListener, DeviceSettingDialog.OnSettingsClickListener {

    @ViewById(R.id.tv_dashboard_company_name)
    TextView tvCompanyName;
    @ViewById(R.id.tv_dashboard_shop_name)
    TextView tvShopName;
    @ViewById(R.id.prsv_device)
    PullToRefreshScrollView1 refreshView;
    @ViewById(R.id.vp_banner)
    ViewPager vpBanner;
    @ViewById(R.id.rv_device)
    RecyclerView rvDevice;
    @ViewById(R.id.rl_empty)
    RelativeLayout rlNoDevice;
    @ViewById(R.id.btn_add)
    TextView btnAdd;

    private List<SunmiDevice> deviceList = new ArrayList<>();
    List<SunmiDevice> routerList = new ArrayList<>();
    List<SunmiDevice> ipcList = new ArrayList<>();
    List<SunmiDevice> printerList = new ArrayList<>();

    private ArrayList<CardView> viewList = new ArrayList<>();//banner数据源
    private StorePagerAdapter mCardAdapter;
    private Timer timer = null, timerException = null;
    ChooseDeviceDialog chooseDeviceDialog;//选择设备
    DeviceListAdapter deviceListAdapter;
    private LinearLayoutManager layoutManager;
    DeviceSettingDialog deviceSettingDialog;

    @AfterViews
    protected void init() {
        mPresenter = new DevicePresenter();
        mPresenter.attachView(this);
        initViews();
    }

    protected void initViews() {
        tvCompanyName.setText(SpUtils.getCompanyName());
        tvShopName.setText(SpUtils.getShopName());
        layoutManager = new LinearLayoutManager(mActivity);
        rvDevice.setLayoutManager(layoutManager);
        deviceListAdapter = new DeviceListAdapter(mActivity, deviceList);
        deviceListAdapter.setClickListener(this);
        rvDevice.setAdapter(deviceListAdapter);
        // 刷新label的设置
        refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(getString(R.string.str_refresh_loading1));
        //设置刷新的模式
        refreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//both  可以上拉、可以下拉
        refreshView.setOnRefreshListener(this);
        mCardAdapter = new StorePagerAdapter(viewList);
        vpBanner.setAdapter(mCardAdapter);
        showLoadingDialog();
        getDeviceList();
    }

    private void getDeviceList() {
        mPresenter.getBannerList();
        mPresenter.getRouterList();
        mPresenter.getIpcList();
        mPresenter.getPrinterList();
    }

    @Click({R.id.btn_add, R.id.btn_add_device})
    void addClick() {
        if (isFastClick(1500)) return;
        CommonUtils.trackCommonEvent(mActivity, "addDevice",
                "主页_店铺_添加设备", Constants.EVENT_MAIN_PAGE);
        CommonUtils.trackDurationEventBegin(mActivity, "bindDeviceDuration",
                "添加设备开始到立即绑定弹框", Constants.EVENT_DURATION_ADD_DEVICE);
        chooseDeviceDialog = new ChooseDeviceDialog(mActivity, SpUtils.getShopId());
        chooseDeviceDialog.show();
//        globalDevList = devList;//todo
    }

    List<AdListResp.AdListBean> adList = new ArrayList<>();

    @Override
    public void getAdListSuccess(AdListResp adListResp) {
        adList.clear();
        adList.addAll(adListResp.getAd_list());
        for (AdListResp.AdListBean bean : adListResp.getAd_list()) {
            addPage(bean);
        }
    }

    /**
     * 该方法封装了添加页面的代码逻辑实现，参数text为要展示的数据
     */
    public void addPage(AdListResp.AdListBean item) {
        if (mCardAdapter == null) return;
        LayoutInflater inflater = LayoutInflater.from(mActivity);//获取LayoutInflater的实例
        //调用LayoutInflater实例的inflate()方法来加载页面的布局
        CardView view = (CardView) inflater.inflate(R.layout.item_card_adapter, null);
        ImageView ivPic = view.findViewById(R.id.ivPic);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity_.intent(mActivity).url(item.getLink()).start();
            }
        });
        if (!TextUtils.isEmpty(item.getImage_addr())) {
            Glide.with(mActivity).load(item.getImage_addr()).into(ivPic);
        }
        view.setTag(item.getName());
        viewList.add(view);
        mCardAdapter.notifyDataSetChanged();//通知UI更新
    }

    @Override
    public void getRouterListSuccess(List<SunmiDevice> devices) {
        routerList.clear();
        routerList = devices;
        refreshList();
    }

    @Override
    public void unbindRouterSuccess(String sn, int code, String msg, Object data) {
        if (code == 1) {//解绑成功后发送通知
            shortTip(R.string.str_delete_success);
            DBUtils.deleteUnBindDevLocal(sn);
            getDeviceList();
        } else {
            shortTip(R.string.str_delete_fail);
        }
    }

    @Override
    public void getIpcListSuccess(List<SunmiDevice> devices) {
        ipcList.clear();
        ipcList = devices;
        refreshList();
    }

    @Override
    public void getPrinterListSuccess(List<SunmiDevice> devices) {
        printerList.clear();
        printerList = devices;
        refreshList();
    }

    @Override
    public void unbindIpcSuccess(int code, String msg, Object data) {
        getDeviceList();
    }

    @Override
    public void unbindIpcFail(int code, String msg) {

    }

    @Override
    public void getPrinterStatusSuccess(SunmiDevice device) {

    }

    @Override
    public void unbindPrinterSuccess(String sn) {
        getDeviceList();
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{
                NotificationConstant.netDisconnection, NotificationConstant.bindRouterChanged,
                NotificationConstant.apPostStatus, NotificationConstant.apStatusException,
                NotificationConstant.checkApPassword,
                NotificationConstant.updateConnectComplete, NotificationConstant.checkLogin,
                NotificationConstant.checkLoginAgain, CommonConstants.tabDevice,
                NotificationConstant.apisConfig, IpcConstants.refreshIpcList,
                com.sunmi.cloudprinter.constant.Constants.NOTIFICATION_PRINTER_ADDED
        };
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) return;
        if (id == CommonConstants.tabDevice) {
            getDeviceList();
        } else if (NotificationConstant.netDisconnection == id) {//网络断开 todo 列表状态更新
        } else if (NotificationConstant.apStatusException == id) {//异常
            LogCat.e(TAG, "exception***CURRENT_ROUTER>>>" + MyNetworkCallback.CURRENT_ROUTER);
            if (TextUtils.isEmpty(MyNetworkCallback.CURRENT_ROUTER)) return;
            devStatusChangeList(MyNetworkCallback.CURRENT_ROUTER, DeviceStatus.EXCEPTION);
        } else if (NotificationConstant.updateConnectComplete == id) {//mqtt断开重连刷新
            getDeviceList();
        } else if (NotificationConstant.apPostStatus == id) {//在线 离线 设备状态
            String msg = (String) args[0];
            LogCat.e(TAG, "在线 离线 设备状态***>>>" + msg);
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
                        LogCat.e(TAG, "111111111111111111 0x211e return  ok ***>>>" + msg);
                        JSONObject object1 = jsonObject.getJSONObject("param");
                        JSONArray jsonArrayList = object1.getJSONArray("device_list");//所有设备列表
                        routerList.clear();
                        for (int j = 0; j < jsonArrayList.length(); j++) {
                            JSONObject object2 = (JSONObject) jsonArrayList.opt(j);
                            SunmiDevice device = new SunmiDevice();
                            device.setDeviceid(object2.getString("Sn"));
                            device.setShopId(object2.getInt("ShopId"));
                            device.setName("SUNMI-W1");
                            device.setModel("W1");
                            device.setStatus(object2.getInt("ActiveStatus"));
                            device.setType("ROUTER");
                            routerList.add(device);
                        }
                        refreshList();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (NotificationConstant.storeNameChanged == id) {
            String shopName = (String) args[1];
            if (!TextUtils.isEmpty(shopName)) {
                tvShopName.setText(shopName);
            }
        } else if (NotificationConstant.bindRouterChanged == id) {
            getDeviceList();
        } else if (NotificationConstant.apisConfig == id) {//ap是否配置2034
            ResponseBean res = (ResponseBean) args[0];
            LogCat.e(TAG, "apisConfig=" + res.getResult());
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

    private Dialog dialogPassword = null;
    //路由管理密码
    private String password = "";
    String mPassword;

    private void saveMangerPasswordDialog(String type) {
        hideLoadingDialog();
        if (dialogPassword != null) {
            dialogPassword = null;
            return;
        }
        dialogPassword = new Dialog(mActivity, com.sunmi.apmanager.R.style.Son_dialog);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(com.sunmi.apmanager.R.layout.dialog_manger_password, null);
        final ClearableEditText etPassword = view.findViewById(com.sunmi.apmanager.R.id.etPassword);
        TextView tvContent = view.findViewById(com.sunmi.apmanager.R.id.tvContent);
        Button btnCancel = view.findViewById(com.sunmi.apmanager.R.id.btnCancel);
        Button btnSure = view.findViewById(com.sunmi.apmanager.R.id.btnSure);
        if (TextUtils.equals(type, "0")) {
            tvContent.setText(com.sunmi.apmanager.R.string.curr_router_manager_password);
        } else {
            tvContent.setText(com.sunmi.apmanager.R.string.curr_router_error_password);
        }
        password = "";
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPassword.dismiss();
                dialogPassword = null;
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    shortTip(com.sunmi.apmanager.R.string.hint_input_manger_password);
                    return;
                }
                APCall.getInstance().checkLoginAgain(mActivity, password);//todo opcode
            }
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
                if (dialogPassword != null) {
                    dialogPassword.dismiss();
                    dialogPassword = null;
                }
                checkApVersion(clickedDevice.getDeviceid(), clickedDevice.getStatus());
//                gotoRouterManager();
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_ERROR)) {// 账户密码错误
                shortTip(getString(com.sunmi.apmanager.R.string.tip_password_error));
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_PASSWORD_INCORRECT_MANY)) { // 账户密码错误次数过多
                shortTip(com.sunmi.apmanager.R.string.tip_password_fail_too_often);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //检测ap是否配置
    private void checkApIsConfig(ResponseBean res) {
        try {
            if (TextUtils.equals("0", res.getErrCode())) {
                JSONObject object = res.getResult();
                //0已初始配置 1未初始化设置
                JSONObject object1 = object.getJSONObject("system");
                String factory = object1.getString("factory");
                if (TextUtils.equals("0", factory)) {
                    isComeRouterManager(clickedDevice.getDeviceid(), clickedDevice.getStatus());
                } else {
                    openActivity(getActivity(), PrimaryRouteStartActivity.class);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //设备ap登录，检测管理密码item
    private void checkApLoginMangerPsd(ResponseBean res) {
        LogCat.e(TAG, "what_ap_login_check_mangerPsd    ######>>>" + res);
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
                shortTip(com.sunmi.apmanager.R.string.tip_password_fail_too_often);
            } else if (TextUtils.equals(res.getErrCode(), AppConfig.ERROR_CODE_UNSET_PASSWORD)) { // 账户密码未设置
                openActivity(getActivity(), PrimaryRouteStartActivity.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void devStatusChangeList(String currentRouter, DeviceStatus status) {
        for (SunmiDevice device : deviceList) {
            if (TextUtils.equals(device.getDeviceid(), currentRouter)) {
                device.setStatus(status.ordinal());
                deviceListAdapter.notifyDataSetChanged();
                return;
            }
        }
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
                if (printerList == null || printerList.size() == 0) {
                    closeTimer();
                }
                getDeviceList();
            }
        }, 30000, 30000);
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
        if (deviceList != null) {
            deviceList.clear();
            deviceList.addAll(routerList);
            deviceList.addAll(ipcList);
            deviceList.addAll(printerList);
            if (deviceList.size() > 0) {
                rlNoDevice.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
            } else {
                rlNoDevice.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.GONE);
            }
        }
        deviceListAdapter.notifyDataSetChanged();
        endRefresh();
    }

    //关闭刷新状态
    @UiThread
    @Override
    public void endRefresh() {
        if (refreshView != null && refreshView.isRefreshing()) {
            refreshView.onRefreshComplete();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {
        getDeviceList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<NestedScrollView> refreshView) {

    }

    SunmiDevice clickedDevice;

    @Override
    public void onDeviceClick(SunmiDevice device) {
        if (isFastClick(1500)) return;
        clickedDevice = device;
        if (TextUtils.equals(device.getType(), "ROUTER")) {

            if (device.getStatus() == DeviceStatus.OFFLINE.ordinal()
                    || device.getStatus() == DeviceStatus.UNKNOWN.ordinal()) {
                shortTip(getString(R.string.str_cannot_manager_ap));
                return;
            }
            showLoadingDialog();
            //校验ap是已初始化配置
            if (TextUtils.equals(device.getDeviceid(), MyNetworkCallback.CURRENT_ROUTER)) {
                APCall.getInstance().apIsConfig(mActivity, device.getDeviceid());
            } else {
                isComeRouterManager(device.getDeviceid(), device.getStatus());
            }
        } else if (TextUtils.equals(device.getType(), "IPC")) {
            if (TextUtils.isEmpty(device.getUid())) {
                shortTip("播放失败");
            } else {
                VideoPlayActivity_.intent(mActivity).UID(device.getUid())
                        .deviceId(device.getId()).ipcType(device.getModel()).start();
            }
        } else if (TextUtils.equals(device.getType(), "PRINTER")) {
            PrinterManageActivity_.intent(mActivity).sn(device.getDeviceid()).userId(SpUtils.getUID())
                    .merchantId(SpUtils.getShopId() + "").channelId(device.getChannelId()).start();
        }
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
        ApCompatibleUtils.getInstance().checkVersion(mActivity, sn,
                new ApCompatibleUtils.CompatibleCallback() {
                    @Override
                    public void onCheckCompatible(boolean isCompatible, String currSn) {
                        gotoRouterManager(sn, DeviceStatus.valueOf(status).getValue());
                    }
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

    @Override
    public void onMoreClick(SunmiDevice item, int position) {
        if (deviceSettingDialog != null && deviceSettingDialog.isShowing()) {
            deviceSettingDialog.dismiss();
        } else {
            deviceSettingDialog = new DeviceSettingDialog(mActivity, item);
            deviceSettingDialog.setOnSettingsClickListener(this);
            deviceSettingDialog.show(layoutManager.findViewByPosition(position)
                    .findViewById(R.id.iv_more));
        }
    }

    @Override
    public void onSettingsClick(SunmiDevice device, int type) {
        if (type == 0) {
            onDeviceClick(device);
        } else if (type == 1) {
            deleteDevice(device);
        }
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
                .setCancelButton(R.string.sm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setConfirmButton(R.string.str_delete, R.color.read_deep_more,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (-1 == NetworkUtils.getNetStatus(mActivity)) {
                                    unBindNetDisConnected();
                                    return;
                                }
                                if (TextUtils.equals(device.getType(), "ROUTER")) {
                                    mPresenter.unbindRouter(device.getDeviceid());
                                } else if (TextUtils.equals(device.getType(), "IPC")) {
                                    mPresenter.unbindIPC(device.getDeviceid());
                                } else if (TextUtils.equals(device.getType(), "PRINTER")) {
                                    mPresenter.unBindPrinter(device.getDeviceid());
                                }
                            }
                        }).create().show();
    }

    //无网络
    private void unBindNetDisConnected() {
        new CommonDialog.Builder(mActivity)
                .setMessage(getString(R.string.str_dialog_net_disconnected))
                .setCancelButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

}
