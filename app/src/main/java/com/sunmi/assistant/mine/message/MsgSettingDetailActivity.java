package com.sunmi.assistant.mine.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MsgSettingDetailContract;
import com.sunmi.assistant.mine.model.MsgSettingChildren;
import com.sunmi.assistant.mine.presenter.MsgSettingDetailPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 *
 * @author linyuanpeng on 2019-08-26.
 */

@EActivity(R.layout.activity_msg_setting_detail)
public class MsgSettingDetailActivity extends BaseMvpActivity<MsgSettingDetailPresenter>
        implements MsgSettingDetailContract.View, CompoundButton.OnCheckedChangeListener {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.switch_main)
    Switch sMian;
    @ViewById(R.id.rv_setting_detail)
    RecyclerView rvSetting;
    @ViewById(R.id.tv_main)
    TextView tvMain;

    @Extra
    String title;
    @Extra
    MsgSettingChildren child;

    Switch changedSwitch;


    @AfterViews
    void init() {
        titleBar.setAppTitle(getString(R.string.str_msg_setting_detail, title));
        titleBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvMain.setText(title);
        sMian.setChecked(child.getStatus() == 1);
        mPresenter = new MsgSettingDetailPresenter();
        mPresenter.attachView(this);
        sMian.setOnCheckedChangeListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvSetting.setLayoutManager(layoutManager);
        initDetail();
    }

    private void initDetail() {
        rvSetting.setAdapter(new CommonListAdapter<MsgSettingChildren>(context, R.layout.item_msg_device_setting_detail, child.getChildren()) {
            @Override
            public void convert(ViewHolder holder, MsgSettingChildren children) {
                String name = children.getName();
                LogCat.e(TAG,"  setting name 99999 ="+name);
                String title = "";
                if (name.contains(MsgConstants.NOTIFY_IPC_TF_DETECT)) {
                    title = context.getString(R.string.str_tf_detect);
                } else if (name.contains(MsgConstants.NOTIFY_IPC_ON_OFFLINE)) {
                    title = context.getString(R.string.str_ipc_on_offline);
                } else if (name.contains(MsgConstants.NOTIFY_IPC_DETECT_AUDIO)) {
                    title = context.getString(R.string.str_ipc_audio);
                } else if (name.contains(MsgConstants.NOTIFY_IPC_DETECT_VIDEO)) {
                    title = context.getString(R.string.str_ipc_video);
                } else if (name.contains(MsgConstants.NOTIFY_IPC_OTA)) {
                    title = context.getString(R.string.str_device_ota);
                } else if (name.contains(MsgConstants.NOTIFY_ESL_ON_OFFLINE)) {
                    title = context.getString(R.string.str_esl_on_offline);
                } else if (name.contains(MsgConstants.NOTIFY_ESL_OTA)) {
                    title = context.getString(R.string.str_device_ota);
                }
                holder.setText(R.id.tv_msg_setting, title);
                Switch sw = holder.getView(R.id.switch_msg);
                sw.setChecked(children.getStatus() == 1);
                sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        changeStatus(isChecked, children.getId());
                        changedSwitch = sw;
                    }
                });

            }
        });
        if (child.getStatus() == 0) {
            rvSetting.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        BaseNotification.newInstance().postNotificationName(NotificationConstant.msgDeviceChange);
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
        changedSwitch.setChecked(status == 0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.switch_main) {
            changeStatus(isChecked, child.getId());
            changedSwitch = sMian;
        }
    }

    private void changeStatus(boolean isChecked, int settingId) {
        if (isChecked) {
            mPresenter.updateSettingStatus(settingId, 1);
        } else {
            mPresenter.updateSettingStatus(settingId, 0);
        }
    }
}
