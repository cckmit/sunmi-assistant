package com.sunmi.assistant.pos;

import android.annotation.SuppressLint;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.sunmi.assistant.R;
import com.sunmi.assistant.pos.contract.PosContract;
import com.sunmi.assistant.pos.contract.PosPresenter;
import com.sunmi.assistant.pos.response.PosDetailsResp;
import com.sunmi.assistant.pos.response.PosWarrantyResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;

/**
 * @author yangShiJie
 * @date 2019-11-20
 */
@SuppressLint("Registered")
@EActivity(R.layout.pos_activity_guarantee)
public class PosGuaranteeActivity extends BaseMvpActivity<PosPresenter> implements PosContract.View {
    @ViewById(R.id.sil_status)
    SettingItemLayout silStatus;
    @ViewById(R.id.sil_activated_time)
    SettingItemLayout silActivatedTime;
    @ViewById(R.id.sil_expire_time)
    SettingItemLayout silExpireTime;
    @Extra
    SunmiDevice device;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new PosPresenter(device.getDeviceid());
        mPresenter.attachView(this);
        mPresenter.getPosGuarantee();
    }

    @Override
    public void getPosDetailsSuccess(PosDetailsResp resp) {

    }

    @Override
    public void getPosGuaranteeSuccess(PosWarrantyResp resp) {
        //0=没过保，1=已过保
        int status = resp.getStatus();
        if (status == 0) {
            silStatus.setContent(R.string.pos_activated);
            silStatus.getContent().setTextColor(ContextCompat.getColor(this, R.color.text_caption));
        } else {
            silStatus.setContent(R.string.pos_expire);
            silStatus.getContent().setTextColor(ContextCompat.getColor(this, R.color.caution_primary));
        }
        silActivatedTime.setContent(TextUtils.isEmpty(resp.getActivatedTime()) ? "" : resp.getActivatedTime());
        silExpireTime.setContent(TextUtils.isEmpty(resp.getExpireTime()) ? "" : resp.getExpireTime());
    }

    @Override
    public void getPosTypeSuccess(boolean isDesktop) {

    }
}
