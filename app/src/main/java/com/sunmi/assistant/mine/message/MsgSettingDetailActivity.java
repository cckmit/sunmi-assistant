package com.sunmi.assistant.mine.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MsgSettingDetailContract;
import com.sunmi.assistant.mine.model.MsgSettingChildren;
import com.sunmi.assistant.mine.presenter.MsgSettingDetailPresenter;
import com.sunmi.assistant.utils.MessageUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * @author linyuanpeng on 2019-08-26.
 */

@EActivity(R.layout.activity_msg_setting_detail)
public class MsgSettingDetailActivity extends BaseMvpActivity<MsgSettingDetailPresenter>
        implements MsgSettingDetailContract.View, CompoundButton.OnCheckedChangeListener {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.switch_main)
    SettingItemLayout sMian;
    @ViewById(R.id.rv_setting_detail)
    SmRecyclerView rvSetting;

    @Extra
    String title;
    @Extra
    MsgSettingChildren child;

    private SettingItemLayout changedSil;
    private boolean allowCheck = true;


    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(getString(R.string.str_msg_setting_detail, title));
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        sMian.setTitle(title);
        sMian.setChecked(child.getStatus() == 1);
        mPresenter = new MsgSettingDetailPresenter();
        mPresenter.attachView(this);
        sMian.setOnCheckedChangeListener(this);
        rvSetting.init(R.drawable.shap_line_divider);
        initDetail();
    }

    private void initDetail() {
        rvSetting.setAdapter(new CommonListAdapter<MsgSettingChildren>(context, R.layout.item_common_switch, child.getChildren()) {
            @Override
            public void convert(ViewHolder holder, MsgSettingChildren children) {
                SettingItemLayout item = holder.getView(R.id.sil_item);
                item.setTitle(MessageUtils.getInstance().getMsgFirst(children.getName()));
                item.setChecked(children.getStatus() == 1);
                item.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    changeStatus(isChecked, children.getId(), item);
                    changedSil = item;
                });

            }
        });
        if (child.getStatus() == 0) {
            rvSetting.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        BaseNotification.newInstance().postNotificationName(CommonNotifications.msgSettingsChange);
        finish();
    }

    @Override
    public void updateSettingStatusSuccess(int msgId, int status) {
        if (msgId == child.getId()) {
            if (status == 1) {
                rvSetting.setVisibility(View.VISIBLE);
            } else {
                rvSetting.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void updateSettingStatusFail(int msgId, int status) {
        allowCheck = false;
        changedSil.setChecked(status == 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switch_main) {
            changeStatus(isChecked, child.getId(), sMian);
            changedSil = sMian;
        }
    }

    private void changeStatus(boolean isChecked, int settingId, SettingItemLayout sil) {
        if (noNetCannotClick()) {
            sil.setChecked(!isChecked);
            return;
        }
        if (!allowCheck) {
            allowCheck = true;
            return;
        }
        if (isChecked) {
            mPresenter.updateSettingStatus(settingId, 1);
        } else {
            mPresenter.updateSettingStatus(settingId, 0);
        }
    }

    private boolean noNetCannotClick() {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            shortTip(R.string.toast_network_error);
            return true;
        }
        return false;
    }
}
