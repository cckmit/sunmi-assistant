package com.sunmi.ipc.setting;

import android.content.Intent;
import android.view.View;

import com.sunmi.ipc.R;
import com.sunmi.ipc.rpc.IPCCall;
import com.sunmi.ipc.rpc.OpcodeConstants;
import com.sunmi.ipc.utils.TimeoutTimer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.sunmicall.ResponseBean;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;

/**
 * Created by YangShiJie on 2019/7/24.
 */
@EActivity(resName = "ipc_activity_rotate")
public class IpcSettingRotateActivity extends BaseActivity implements View.OnClickListener {
    private final int ROTATE_DEGREE0 = 0;
    private final int ROTATE_DEGREE1 = 1;
    private final int ROTATE_DEGREE2 = 2;
    private final int ROTATE_DEGREE3 = 3;

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "sil_degree0")
    SettingItemLayout silDegree0;
    @ViewById(resName = "sil_degree90")
    SettingItemLayout silDegree90;
    @ViewById(resName = "sil_degree180")
    SettingItemLayout silDegree180;
    @ViewById(resName = "sil_degree270")
    SettingItemLayout silDegree270;

    @Extra
    SunmiDevice mDevice;
    @Extra
    int nightMode, wdrMode, ledIndicator, rotation;
    private boolean isNetException;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftImg().setOnClickListener(this);
        initRotateView();
        setModelRotate(rotation);
        selectRotate(rotation);//初始化
    }

    private void initRotateView() {
        if (DeviceTypeUtils.getInstance().isSS1(mDevice.getModel())) {
            silDegree90.setVisibility(View.VISIBLE);
            silDegree270.setVisibility(View.VISIBLE);
        } else if (DeviceTypeUtils.getInstance().isFS1(mDevice.getModel())) {
            silDegree90.setVisibility(View.GONE);
            silDegree270.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra("rotate", rotation);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    private void setRotationCall() {
        isNetException = false;
        showLoadingDialog();
        TimeoutTimer.getInstance().start();
        IPCCall.getInstance().setIpcNightIdeRotation(context, mDevice.getModel(), mDevice.getDeviceid(),
                nightMode, wdrMode, ledIndicator, rotation);
    }

    @Click(resName = "sil_degree0")
    void degree0Click() {
        selectRotate(ROTATE_DEGREE0);
        setRotationCall();
    }

    @Click(resName = "sil_degree90")
    void degree90Click() {
        selectRotate(ROTATE_DEGREE1);
        setRotationCall();
    }

    @Click(resName = "sil_degree180")
    void degree180Click() {
        selectRotate(ROTATE_DEGREE2);
        setRotationCall();
    }

    @Click(resName = "sil_degree270")
    void degree270Click() {
        selectRotate(ROTATE_DEGREE3);
        setRotationCall();
    }

    /**
     * 0 自动 1 打开 2 关闭
     *
     * @param type
     */
    private void selectRotate(int type) {
        //0  180
        if (DeviceTypeUtils.getInstance().isFS1(mDevice.getModel()) && type == ROTATE_DEGREE1) {
            silDegree0.setRightImage(null);
            silDegree0.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree90.setRightImage(null);
            silDegree90.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree180.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silDegree180.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silDegree270.setRightImage(null);
            silDegree270.setLeftTextColor(getResources().getColor(R.color.colorText));
            setModelRotate(ROTATE_DEGREE1);
        } else if (type == ROTATE_DEGREE0) {
            silDegree0.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silDegree0.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silDegree90.setRightImage(null);
            silDegree90.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree180.setRightImage(null);
            silDegree180.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree270.setRightImage(null);
            silDegree270.setLeftTextColor(getResources().getColor(R.color.colorText));
            setModelRotate(ROTATE_DEGREE0);
        } else if (type == ROTATE_DEGREE1) {
            silDegree0.setRightImage(null);
            silDegree0.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree90.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silDegree90.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silDegree180.setRightImage(null);
            silDegree180.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree270.setRightImage(null);
            silDegree270.setLeftTextColor(getResources().getColor(R.color.colorText));
            setModelRotate(ROTATE_DEGREE1);
        } else if (type == ROTATE_DEGREE2) {
            silDegree0.setRightImage(null);
            silDegree0.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree90.setRightImage(null);
            silDegree90.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree180.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silDegree180.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silDegree270.setRightImage(null);
            silDegree270.setLeftTextColor(getResources().getColor(R.color.colorText));
            setModelRotate(ROTATE_DEGREE2);
        } else if (type == ROTATE_DEGREE3) {
            silDegree0.setRightImage(null);
            silDegree0.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree90.setRightImage(null);
            silDegree90.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree180.setRightImage(null);
            silDegree180.setLeftTextColor(getResources().getColor(R.color.colorText));
            silDegree270.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silDegree270.setLeftTextColor(getResources().getColor(R.color.common_orange));
            setModelRotate(ROTATE_DEGREE3);
        }
    }

    private void setModelRotate(int type) {
        if (type == ROTATE_DEGREE0) {
            rotation = ROTATE_DEGREE0;
            return;
        }
        if (DeviceTypeUtils.getInstance().isSS1(mDevice.getModel())) {
            if (type == ROTATE_DEGREE1) {
                rotation = ROTATE_DEGREE1;
            } else if (type == ROTATE_DEGREE2) {
                rotation = ROTATE_DEGREE2;
            } else if (type == ROTATE_DEGREE3) {
                rotation = ROTATE_DEGREE3;
            }
        } else if (DeviceTypeUtils.getInstance().isFS1(mDevice.getModel())) {
            if (type == ROTATE_DEGREE2) {
                rotation = ROTATE_DEGREE1;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeoutTimer.getInstance().stop();
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{OpcodeConstants.setIpcNightIdeRotation,
                CommonNotifications.mqttResponseTimeout};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        hideLoadingDialog();
        if (args == null) {
            return;
        }
        if (id == CommonNotifications.mqttResponseTimeout) { //连接超时
            isNetException = true;
            shortTip(R.string.str_server_exception);
            return;
        }
        ResponseBean res = (ResponseBean) args[0];
        if (id == OpcodeConstants.setIpcNightIdeRotation) {
            if (isNetException) {
                return;
            }
            setIpcNightIdeRotation(res);
        }
    }

    //led_indicator   rotation设置结果
    @UiThread
    void setIpcNightIdeRotation(ResponseBean res) {
        if (res.getResult() != null && res.getDataErrCode() == 1) {
            shortTip(R.string.tip_set_complete);
        } else {
            shortTip(R.string.tip_set_fail);
        }
    }
}
