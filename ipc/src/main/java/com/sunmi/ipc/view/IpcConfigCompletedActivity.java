package com.sunmi.ipc.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.rpc.IpcConstants;
import com.sunmi.ipc.setting.recognition.RecognitionSettingActivity_;

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
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.activity.StartConfigSMDeviceActivity_;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.ListDialog;

/**
 * Description: IpcConfigCompletedActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_ipc_config_completed")
public class IpcConfigCompletedActivity extends BaseActivity {

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

    @Extra
    String shopId;
    @Extra
    int deviceType;
    @Extra
    boolean isSunmiLink;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;

    private List<SunmiDevice> list = new ArrayList<>();
    private List<SunmiDevice> successList = new ArrayList<>();
    private int failCount;

    SunmiDevice deviceChoose;

    @AfterViews
    void init() {
        if (sunmiDevices != null) {
            for (SunmiDevice sm : sunmiDevices) {
                if (!isBindSuccess(sm)) {
                    failCount++;
                } else {
                    successList.add(sm);
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
                } else {
                    btnComplete.setVisibility(View.VISIBLE);
                }
            }
        }
        list = sunmiDevices;
        initList();
    }

    @Click(resName = "btn_complete")
    void completeClick() {
        if (CommonConstants.TYPE_IPC_FS == deviceType) {
            if (successList.size() == 1) {
                getSDCardStatus(successList.get(0));
            } else if (successList.size() > 1) {
                chooseFsAdjust();
            }
        } else {
            GotoActivityUtils.gotoMainActivity(context);
            finish();
        }
    }

    @Click(resName = "btn_finish")
    void finishClick() {
        GotoActivityUtils.gotoMainActivity(context);
        finish();
    }

    @Click(resName = "btn_retry")
    void retryClick() {
        if (isSunmiLink) {
            setResult(RESULT_OK);
        } else
            StartConfigSMDeviceActivity_.intent(context)
                    .deviceType(deviceType).shopId(shopId).start();
        finish();
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.getSdStatus};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (args == null) {
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (res.getDataErrCode() == 1) {
            if (IpcConstants.getSdStatus == id) {
                try {
                    int status = res.getResult().getInt("sd_status_code");
                    switch (status) {
                        case 2:
                            startCameraAdjust(deviceChoose);
                            break;
                        case 0:
                            showErrorDialog(R.string.tip_no_tf_card,
                                    R.string.ipc_recognition_sd_none);
                            break;
                        case 1:
                            showErrorDialog(R.string.tip_tf_uninitalized,
                                    R.string.ipc_recognition_sd_uninitialized);
                            break;
                        case 3:
                            showErrorDialog(R.string.tip_unrecognition_tf_card,
                                    R.string.ipc_recognition_sd_unknown);
                            break;
                    }
                } catch (JSONException e) {
                    LogCat.e(TAG, "Parse json ERROR: " + res.getResult());
                }
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
                holder.getView(R.id.tv_adjust).setVisibility(View.GONE);
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
                .setConfirmButton(R.string.str_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (adapter.getSelectedIndex() != -1) {
                            dialog.dismiss();
                            getSDCardStatus(successList.get(adapter.getSelectedIndex()));
                        }
                    }
                }).create().show();
    }

    private void getSDCardStatus(SunmiDevice device) {
        showLoadingDialog();
        deviceChoose = device;
        if (!NetworkUtils.isNetworkAvailable(context)) {
            shortTip(R.string.str_net_exception);
            hideLoadingDialog();
            return;
        }
        SunmiDevice sunmiDevice = CommonConstants.SUNMI_DEVICE_MAP.get(device.getDeviceid());
        if (sunmiDevice == null) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            hideLoadingDialog();
            return;
        }
        IPCCall.getInstance().getSdState(device.getIp());
    }

    /**
     * 绑定完的mqtt消息未给UID。。。校准需要看直播，必须要有UID。。。等云端mqtt返回UID可以去掉接口调用
     */
    public void startCameraAdjust(final SunmiDevice device) {
        showLoadingDialog();
        IpcCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        hideLoadingDialog();
                        boolean success = false;
                        if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                if (device.getDeviceid().equalsIgnoreCase(bean.getSn())) {
                                    startCameraAdjustActivity(getSunmiDevice(bean));
                                    success = true;
                                }
                            }
                        }
                        if (!success) {
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

    private void startCameraAdjustActivity(SunmiDevice device) {
        hideLoadingDialog();
        if (!CommonConstants.SUNMI_DEVICE_MAP.containsKey(device.getDeviceid())) {
            shortTip(R.string.ipc_setting_tip_network_dismatch);
            return;
        }
        RecognitionSettingActivity_.intent(this)
                .mDevice(device)
                .mVideoRatio(16f / 9f)
                .start();
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedIndex = holder.getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
            cb.setChecked(selectedIndex == holder.getAdapterPosition());
        }
    }

}
