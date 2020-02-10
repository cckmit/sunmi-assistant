package com.sunmi.ipc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.utils.IpcUtils;
import com.sunmi.ipc.view.activity.setting.IpcSettingSdcardActivity_;
import com.sunmi.ipc.view.activity.setting.ScreenAdjustSettingActivity_;
import com.xiaojinzi.component.impl.Router;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ServiceResp;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.AppApi;
import sunmi.common.router.SunmiServiceApi;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SMDeviceDiscoverUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.WebViewParamsUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.ListDialog;

/**
 * Description: IpcConfigCompletedActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_ipc_config_completed")
public class IpcConfigCompletedActivity extends BaseActivity {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "rv_result")
    SmRecyclerView rvResult;
    @ViewById(resName = "btn_complete")
    Button btnComplete;
    @ViewById(resName = "btn_retry")
    Button btnRetry;
    @ViewById(resName = "btn_finish")
    Button btnFinish;
    @ViewById(resName = "tv_result")
    TextView tvResult;
    @ViewById(resName = "tv_tip")
    TextView tvTip;
    @ViewById(resName = "btn_cloud")
    Button btnCloud;

    @Extra
    String shopId;
    @Extra
    int deviceType;
    @Extra
    boolean isSunmiLink;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;
    @Extra
    int source;

    SunmiDevice deviceChoose;
    private List<SunmiDevice> list = new ArrayList<>();
    private List<SunmiDevice> successList = new ArrayList<>();
    private ArrayList<String> snList = new ArrayList<>();
    private int failCount;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (sunmiDevices != null) {
            for (SunmiDevice sm : sunmiDevices) {
                if (!isBindSuccess(sm)) {
                    failCount++;
                } else {
                    successList.add(sm);
                    snList.add(sm.getDeviceid());
                }
            }
            if (failCount == sunmiDevices.size()) {
                tvResult.setText(R.string.tip_ipc_config_fail);
                btnFinish.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
            } else {
                if (CommonConstants.TYPE_IPC_FS == deviceType) {
                    tvTip.setVisibility(View.VISIBLE);
                    btnComplete.setText(R.string.str_adjust_screen);
                    btnComplete.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                } else if (CommonConstants.TYPE_IPC_SS == deviceType && snList.size() > 0) {
                    initSs();
                } else {
                    btnComplete.setVisibility(View.VISIBLE);
                }
            }
        }
        list = sunmiDevices;
        if (isSunmiLink) {
            titleBar.setAppTitle(R.string.str_sunmi_link);
            tvResult.setText(getString(R.string.str_wifi_config_finish));
        }
        initList();
        SMDeviceDiscoverUtils.scanDevice(context, IpcConstants.ipcDiscovered);
    }

    @Click(resName = "btn_complete")
    void completeClick() {
        if (CommonConstants.TYPE_IPC_FS == deviceType) {
            if (successList.size() == 1) {
                fsAdjustPrepare(successList.get(0));
            } else if (successList.size() > 1) {
                chooseFsAdjust();
            }
        } else {
            if (isSunmiLink) {
                Intent intent = new Intent();
                intent.putExtra("isComplete", true);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                if (source == CommonConstants.CONFIG_IPC_FROM_CASH_VIDEO) {
                    Router.withApi(SunmiServiceApi.class)
                            .goToWebViewCloudSingle(context, CommonConstants.H5_CASH_VIDEO, WebViewParamsUtils.getCashVideoParams());
                } else {
                    Router.withApi(AppApi.class).goToMain(context);
                }
            }
        }
    }

    @Click(resName = "btn_finish")
    void finishClick() {
        if (source == CommonConstants.CONFIG_IPC_FROM_CASH_VIDEO) {
            Router.withApi(SunmiServiceApi.class)
                    .goToWebViewCloudSingle(context, CommonConstants.H5_CASH_VIDEO, WebViewParamsUtils.getCashVideoParams());
        } else {
            Router.withApi(AppApi.class).goToMain(context, this::finish);
        }
    }

    @Click(resName = "btn_retry")
    void retryClick() {
        if (isSunmiLink) {
            setResult(RESULT_OK);
        } else {
            IpcStartConfigActivity_.intent(context).ipcType(deviceType).source(source).start();
        }
        finish();
    }

    @Click(resName = "btn_cloud")
    void cloudClick() {
        Router.withApi(SunmiServiceApi.class)
                .goToWebViewCloud(context, CommonConstants.H5_CLOUD_STORAGE, WebViewParamsUtils.getCloudStorageParams(snList, ""));
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.getSdcardStatus, IpcConstants.ipcDiscovered};
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.cloudStorageChange};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        hideLoadingDialog();
        if (args == null) {
            return;
        }
        if (IpcConstants.ipcDiscovered == id) {
            SMDeviceDiscoverUtils.saveInfo((SunmiDevice) args[0]);
            return;
        } else if (CommonNotifications.cloudStorageChange == id) {
            initSs();
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (IpcConstants.getSdcardStatus == id) {
            try {
                if (res.getDataErrCode() == 1) {
                    int status = res.getResult().getInt("sd_status_code");
                    switch (status) {
                        case 2:
                            startFsAdjust(deviceChoose);
                            break;
                        case 0:
                            showErrorDialog(R.string.tip_no_tf_card,
                                    R.string.ipc_recognition_sd_none);
                            break;
                        case 1:
                            showFormatDialog(deviceChoose);
                            break;
                        case 3:
                            showErrorDialog(R.string.tip_unrecognition_tf_card,
                                    R.string.ipc_recognition_sd_unknown);
                            break;
                        case 4:
                            showErrorDialog(R.string.tip_unrecognition_tf_card,
                                    R.string.tip_tf_card_removed_software);
                            break;
                        default:
                            shortTip(R.string.network_wifi_low);
                            break;
                    }
                } else {
                    shortTip(R.string.network_wifi_low);
                }
            } catch (JSONException e) {
                shortTip(R.string.network_wifi_low);
            }
        }
    }

    @UiThread
    public void showErrorDialog(@StringRes int title, @StringRes int msgResId) {
        hideLoadingDialog();
        new CommonDialog.Builder(context)
                .setTitle(title)
                .setMessage(msgResId)
                .setConfirmButton(R.string.str_confirm).create().show();
    }

    @UiThread
    public void showFormatDialog(SunmiDevice device) {
        hideLoadingDialog();
        new CommonDialog.Builder(context)
                .setTitle(R.string.tip_sdcard_unformat)
                .setMessage(R.string.msg_dialog_format_sd_before_adjust)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm, (dialog, which) ->
                        IpcSettingSdcardActivity_.intent(this).mDevice(device).start())
                .create().show();
    }


    protected void initSs() {
        IpcCloudApi.getInstance().getStorageList(snList, new RetrofitCallback<ServiceResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceResp data) {
                List<ServiceResp.Info> beans = data.getList();
                snList.clear();
                for (ServiceResp.Info bean : beans) {
                    if (bean.getActiveStatus() == CommonConstants.SERVICE_INACTIVATED) {
                        snList.add(bean.getDeviceSn());
                    }
                }
                if (snList.size() > 0) {
                    btnCloud.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    btnComplete.setVisibility(View.VISIBLE);
                    btnCloud.setVisibility(View.GONE);
                    btnFinish.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceResp data) {
                btnComplete.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initList() {
        rvResult.init(R.drawable.shap_line_divider);
        rvResult.setAdapter(new CommonListAdapter<SunmiDevice>(context,
                R.layout.item_device_config_complete, list) {
            @Override
            public void convert(ViewHolder holder, final SunmiDevice device) {
                if (!TextUtils.isEmpty(device.getDeviceid())) {
                    holder.setText(R.id.tv_name, device.getDeviceid());
                } else {
                    holder.setText(R.id.tv_name, device.getName());
                }
                holder.setImageResource(R.id.iv_device, DeviceTypeUtils.getInstance()
                        .getSunmiDeviceImage(device.getModel()));
                if (isBindSuccess(device)) {
                    holder.setText(R.id.tv_status, getString(R.string.str_add_success));
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_done);
                } else {
                    holder.setText(R.id.tv_status, getErrorString(device));
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_error);
                }
            }

            private String getErrorString(SunmiDevice device) {
                String errStr = getString(R.string.str_bind_fail);
                if (device.getStatus() == 5501) {
                    errStr = getString(R.string.tip_device_not_exist);
                } else if (device.getStatus() == 5509) {
                    errStr = getString(R.string.tip_already_bound);
                } else if (device.getStatus() == 5511) {
                    errStr = getString(R.string.tip_device_offline);
                } else if (device.getStatus() == 5013) {
                    errStr = getString(R.string.tip_error_company_or_shop_id);
                } else if (device.getStatus() == 5514) {
                    errStr = getString(R.string.tip_device_bound_by_other_shop);
                } else if (device.getStatus() == RpcErrorCode.RPC_ERR_TIMEOUT) {
                    errStr = getString(R.string.tip_bind_timeout);
                }
                return errStr;
            }
        });
    }

    private boolean isBindSuccess(SunmiDevice device) {
        return device.getStatus() == 1 || device.getStatus() == 5512;
    }

    private void chooseFsAdjust() {
        final FsAdjustAdapter adapter = new FsAdjustAdapter(context,
                R.layout.item_fs_adjust, successList);
        new ListDialog.Builder<FsAdjustAdapter>(context)
                .setTitle(R.string.str_choose_fs_adjust)
                .setAdapter(adapter)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_confirm, (dialog, which) -> {
                    if (adapter.getSelectedIndex() != -1) {
                        dialog.dismiss();
                        fsAdjustPrepare(successList.get(adapter.getSelectedIndex()));
                    }
                }).create().show();
    }

    /**
     * FS画面调整的准备，包括网络判断，局域网判断，获取FS直播UID。
     * 注：绑定完的MQTT消息未给UID，后续MQTT消息携带UID后，可以免去接口调用
     */
    private void fsAdjustPrepare(SunmiDevice device) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            shortTip(R.string.str_net_exception);
            return;
        }
        SunmiDevice sunmiDevice = CommonConstants.SUNMI_DEVICE_MAP.get(device.getDeviceid());
        if (sunmiDevice == null) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            return;
        }
        showLoadingDialog();
        IpcCloudApi.getInstance().getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                if (device.getDeviceid().equalsIgnoreCase(bean.getSn())) {
                                    fsAdjust(getSunmiDevice(bean));
                                    return;
                                }
                            }
                            hideLoadingDialog();
                            shortTip(R.string.tip_device_not_exist);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {
                        hideLoadingDialog();
                        shortTip(R.string.str_net_exception);
                    }
                });
    }

    /**
     * FS画面调整入口，需要判断版本，新版无需SD卡就绪
     */
    private void fsAdjust(SunmiDevice device) {
        deviceChoose = device;
        String versionName = device.getFirmware();
        if (IpcUtils.isNewVersion(versionName, IpcConstants.IPC_VERSION_NO_SDCARD_CHECK)) {
            startFsAdjust(device);
        } else {
            IPCCall.getInstance().getSdState(context, device.getModel(), device.getDeviceid());
        }
    }

    private void startFsAdjust(SunmiDevice device) {
        hideLoadingDialog();
        ScreenAdjustSettingActivity_.intent(this)
                .mDevice(device)
                .mVideoRatio(16f / 9f)
                .start();
    }

    private SunmiDevice getSunmiDevice(IpcListResp.SsListBean bean) {
        SunmiDevice device = new SunmiDevice();
        device.setType("IPC");
        device.setStatus(bean.getActive_status());
        device.setDeviceid(bean.getSn());
        device.setModel(bean.getModel());
        device.setName(bean.getDevice_name());
        device.setIp(bean.getCdn_address());
        device.setUid(bean.getUid());
        device.setShopId(bean.getShop_id());
        device.setId(bean.getId());
        device.setFirmware(bean.getBin_version());
        return device;
    }

    public static class FsAdjustAdapter extends CommonListAdapter<SunmiDevice> {

        int selectedIndex = -1;

        FsAdjustAdapter(Context context, int layoutId, List<SunmiDevice> list) {
            super(context, layoutId, list);
        }

        int getSelectedIndex() {
            return selectedIndex;
        }

        @Override
        public void convert(final ViewHolder holder, SunmiDevice device) {
            CheckBox cb = holder.getView(R.id.cb_item);
            holder.setText(R.id.tv_name, device.getDeviceid());
            holder.itemView.setOnClickListener(v -> {
                selectedIndex = holder.getAdapterPosition();
                notifyDataSetChanged();
            });
            cb.setChecked(selectedIndex == holder.getAdapterPosition());
        }
    }

}
