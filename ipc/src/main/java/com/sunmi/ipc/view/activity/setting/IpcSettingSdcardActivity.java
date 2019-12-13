package com.sunmi.ipc.view.activity.setting;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeSet;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.CommonProgressDialog;

/**
 * Description:
 * Created by bruce on 2019/10/15.
 */
@EActivity(resName = "activity_ipc_sdcard")
public class IpcSettingSdcardActivity extends BaseActivity {

    @Extra
    SunmiDevice mDevice;

    private static final int DURATION_FORMAT = 15_000;
    private static final int INTERVAL_PROGRESS = 200;

    private CountDownTimer formatTimer;
    private CommonProgressDialog progressDialog;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "sil_sd_format")
    void formatClick() {
        showConfirmDialog();
        TreeSet<String> sunmiDevices = new TreeSet<>();
        String ss = "aaa";
        sunmiDevices.add(ss);
        sunmiDevices.add(ss);

    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{OpcodeConstants.sdcardFormat};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == OpcodeConstants.sdcardFormat) {
            formatTimer.cancel();
            progressDialogDismiss();
            if (args == null) return;
            ResponseBean res = (ResponseBean) args[0];
            if (TextUtils.equals(RpcErrorCode.RPC_COMMON_ERROR + "", res.getErrCode())
                    || TextUtils.equals(RpcErrorCode.RPC_ERR_TIMEOUT + "", res.getErrCode())) {
                shortTip(R.string.toast_networkIsExceptional);
                return;
            }
            try {
                JSONObject result = res.getResult();
                if (result == null || (result.has("sn")
                        && !TextUtils.equals(result.getString("sn"), mDevice.getDeviceid()))) {
                    return;
                }

                if (result.has("sd_status_code")) {
                    int sdStatusCode = result.getInt("sd_status_code");
                    if (sdStatusCode == 2) {
                        showResultDialog(true);
                    } else {
                        tipSdcardError(sdStatusCode);
                    }
                    return;
                }
                shortTip(R.string.toast_networkIsExceptional);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void showConfirmDialog() {
        new CommonDialog.Builder(context).setTitle(R.string.str_sd_format)
                .setMessage(R.string.msg_sdcard_format_confirm)
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.str_sd_format, (dialog, which) -> {
                    IPCCall.getInstance().sdcardFormat(context, mDevice.getModel(), mDevice.getDeviceid());
                    showProgressDialog();
                }).create().show();
    }

    @UiThread
    void showResultDialog(boolean isSuccess) {
        new CommonDialog.Builder(context)
                .setTitle(isSuccess ? R.string.str_sd_format_success : R.string.str_sd_format_fail)
                .setMessage(isSuccess ? R.string.tip_sd_format_success : R.string.tip_sd_format_fail)
                .setConfirmButton(R.string.str_confirm).create().show();
    }

    @UiThread
    void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new CommonProgressDialog.Builder(context)
                    .setMessage(R.string.msg_duration_sd_format)
                    .setProgressFormat(R.string.str_format_in_progress)
                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL).create();
        }
        progressDialog.showWithOutTouchableCancelable(false);
        formatTimer = new CountDownTimer(DURATION_FORMAT, INTERVAL_PROGRESS) {
            @Override
            public void onTick(long millisUntilFinished) {
                setFormatProgress((int) ((DURATION_FORMAT - millisUntilFinished + INTERVAL_PROGRESS) / 150));
            }

            @Override
            public void onFinish() {
                progressDialogDismiss();
                showResultDialog(false);
            }
        };
        formatTimer.start();
    }

    @UiThread
    void setFormatProgress(int progress) {
        if (progressDialog != null) {
            progressDialog.setProgress(progress);
        }
    }

    @UiThread
    void progressDialogDismiss() {
        if (progressDialog != null) {
            progressDialog.setProgress(100);
            progressDialog.dismiss();
        }
    }

    private void tipSdcardError(int sdStatusCode) {
        if (sdStatusCode == 0) {
            shortTip(R.string.ipc_recognition_sd_none);
        } else if (sdStatusCode == 1) {
            shortTip(R.string.ipc_recognition_sd_uninitialized);
        } else if (sdStatusCode == 3) {
            shortTip(R.string.ipc_recognition_sd_unknown);
        } else {
            shortTip(R.string.toast_networkIsExceptional);
        }
    }

}
