package com.sunmi.ipc.setting;

import com.sunmi.ipc.R;
import com.sunmi.ipc.contract.IpcSettingNightStyleContract;
import com.sunmi.ipc.presenter.IpcSettingNightStylePresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;

/**
 * Created by YangShiJie on 2019/7/12.
 */
@EActivity(resName = "ipc_activity_night_style")
public class IpcSettingNightStyleActivity extends BaseMvpActivity<IpcSettingNightStylePresenter>
        implements IpcSettingNightStyleContract.View {
    @ViewById(resName = "sil_auto_switch")
    SettingItemLayout silAutoSwitch;
    @ViewById(resName = "sil_open")
    SettingItemLayout silOpen;
    @ViewById(resName = "sil_close")
    SettingItemLayout silClose;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
    }

    @Click(resName = "sil_auto_switch")
    void autoClick() {
        selectNightStyle(0);
    }

    @Click(resName = "sil_open")
    void openClick() {
        selectNightStyle(1);
    }

    @Click(resName = "sil_close")
    void closeClick() {
        selectNightStyle(2);
    }

    /**
     * 0 自动 1 打开 2 关闭
     *
     * @param type
     */
    private void selectNightStyle(int type) {
        if (type == 0) {
            silAutoSwitch.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silAutoSwitch.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silOpen.setRightImage(null);
            silOpen.setLeftTextColor(getResources().getColor(R.color.colorText));
            silClose.setRightImage(null);
            silClose.setLeftTextColor(getResources().getColor(R.color.colorText));
        } else if (type == 1) {
            silAutoSwitch.setRightImage(null);
            silAutoSwitch.setLeftTextColor(getResources().getColor(R.color.colorText));
            silOpen.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silOpen.setLeftTextColor(getResources().getColor(R.color.common_orange));
            silClose.setRightImage(null);
            silClose.setLeftTextColor(getResources().getColor(R.color.colorText));
        } else if (type == 2) {
            silAutoSwitch.setRightImage(null);
            silAutoSwitch.setLeftTextColor(getResources().getColor(R.color.colorText));
            silOpen.setRightImage(null);
            silOpen.setLeftTextColor(getResources().getColor(R.color.colorText));
            silClose.setRightImage(getResources().getDrawable(R.mipmap.ic_yes));
            silClose.setLeftTextColor(getResources().getColor(R.color.common_orange));
        }
    }

    @Override
    public void setNightStyleSuccess(Object data) {

    }

    @Override
    public void setNightStyleFail(int code, String msg) {

    }
}
