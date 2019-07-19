package com.sunmi.ipc.setting;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcSettingNightStyleContract;
import com.sunmi.ipc.presenter.IpcSettingNightStylePresenter;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;

/**
 * Created by YangShiJie on 2019/7/12.
 */
@EActivity(resName = "ipc_activity_night_style")
public class IpcSettingNightStyleActivity extends BaseMvpActivity<IpcSettingNightStylePresenter>
        implements IpcSettingNightStyleContract.View, View.OnClickListener {
    //夜视模式 0:始终关闭/1:始终开启/2:自动切换
    private final int NIGHT_MODE_OFF = 0;
    private final int NIGHT_MODE_ON = 1;
    private final int NIGHT_MODE_AUTO = 2;

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "sil_auto_switch")
    SettingItemLayout silAutoSwitch;
    @ViewById(resName = "sil_open")
    SettingItemLayout silOpen;
    @ViewById(resName = "sil_close")
    SettingItemLayout silClose;

    @Extra
    SunmiDevice mDevice;
    @Extra
    int nightMode, ledIndicator, rotation;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftImg().setOnClickListener(this);
        selectNightStyle(nightMode);//初始化
    }

    @Override
    public void onClick(View v) {
        Intent intent = getIntent();
        intent.putExtra("nightMode", nightMode);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Click(resName = "sil_auto_switch")
    void autoClick() {
        selectNightStyle(NIGHT_MODE_AUTO);
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(), mDevice.getDeviceid(),
                NIGHT_MODE_AUTO, ledIndicator, rotation);
    }

    @Click(resName = "sil_open")
    void openClick() {
        selectNightStyle(NIGHT_MODE_ON);
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(), mDevice.getDeviceid(),
                NIGHT_MODE_ON, ledIndicator, rotation);
    }

    @Click(resName = "sil_close")
    void closeClick() {
        selectNightStyle(NIGHT_MODE_OFF);
        showLoadingDialog();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(), mDevice.getDeviceid(),
                NIGHT_MODE_OFF, ledIndicator, rotation);
    }

    /**
     * 0 自动 1 打开 2 关闭
     *
     * @param type
     */
    private void selectNightStyle(int type) {
        if (type == NIGHT_MODE_AUTO) {
            silAutoSwitch.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silAutoSwitch.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silOpen.setRightImage(null);
            silOpen.setLeftTextColor(getResources().getColor(R.color.colorText));
            silClose.setRightImage(null);
            silClose.setLeftTextColor(getResources().getColor(R.color.colorText));
            nightMode = NIGHT_MODE_AUTO;
        } else if (type == NIGHT_MODE_ON) {
            silAutoSwitch.setRightImage(null);
            silAutoSwitch.setLeftTextColor(getResources().getColor(R.color.colorText));
            silOpen.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silOpen.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silClose.setRightImage(null);
            silClose.setLeftTextColor(getResources().getColor(R.color.colorText));
            nightMode = NIGHT_MODE_ON;
        } else if (type == NIGHT_MODE_OFF) {
            silAutoSwitch.setRightImage(null);
            silAutoSwitch.setLeftTextColor(getResources().getColor(R.color.colorText));
            silOpen.setRightImage(null);
            silOpen.setLeftTextColor(getResources().getColor(R.color.colorText));
            silClose.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silClose.setLeftTextColor(getResources().getColor(R.color.common_orange));
            nightMode = NIGHT_MODE_OFF;
        }
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{IpcConstants.setIpcNightIdeRotation};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (args == null) return;
        ResponseBean res = (ResponseBean) args[0];
        if (id == IpcConstants.setIpcNightIdeRotation) {
            setIpcNightIdeRotation(res);
        }
    }

    //led_indicator   rotation设置结果
    @UiThread
    void setIpcNightIdeRotation(ResponseBean res) {
        if (TextUtils.isEmpty(res.getResult().toString())) return;
        if (TextUtils.equals("1", res.getErrCode())) {
            shortTip(R.string.tip_set_complete);
        } else {
            shortTip(R.string.tip_set_fail);
        }
    }

    @Override
    public void setNightStyleSuccess(Object data) {

    }

    @Override
    public void setNightStyleFail(int code, String msg) {

    }

}
