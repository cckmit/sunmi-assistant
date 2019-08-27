package com.sunmi.assistant.mine.message;

import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MsgSettingContract;
import com.sunmi.assistant.mine.model.MsgSettingChildren;
import com.sunmi.assistant.mine.model.MsgSettingListBean;
import com.sunmi.assistant.mine.presenter.MsgSettingPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.SmRecyclerView;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.ViewHolder;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
@EActivity(R.layout.activity_msg_setting)
public class MsgSettingActivity extends BaseMvpActivity<MsgSettingPresenter>
        implements MsgSettingContract.View, CompoundButton.OnCheckedChangeListener {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.nsv_setting)
    NestedScrollView nsvSetting;
    @ViewById(R.id.rv_device)
    SmRecyclerView rvDevice;
    @ViewById(R.id.layout_network_error)
    View networkError;
    @ViewById(R.id.switch_task)
    Switch sTask;
    @ViewById(R.id.switch_service)
    Switch sService;
    @ViewById(R.id.switch_promotion)
    Switch sPromotion;

    private CommonListAdapter adapter;
    private MsgSettingChildren taskChild, serviceChild, promotionChild;
    private List<MsgSettingChildren> deviceMsg = new ArrayList<>();
    private Map<String, String> titleMap = new HashMap<>();

    @AfterViews
    void init() {
        titleBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPresenter = new MsgSettingPresenter();
        mPresenter.attachView(this);
        mPresenter.getSettingList();
        rvDevice.init(R.drawable.shap_line_divider);
    }

    @Override
    public void getSettingListSuccess(List<MsgSettingListBean.ReminderSettingListBean> beans) {
        nsvSetting.setVisibility(View.VISIBLE);
        networkError.setVisibility(View.GONE);
        addData(beans.get(0).getChildren());
        for (MsgSettingChildren child : beans.get(1).getChildren()) {
            String name = child.getName();
            if (TextUtils.equals(name, MsgConstants.NOTIFY_TASK_SETTING)) {
                taskChild = child;
            } else if (TextUtils.equals(name, MsgConstants.NOTOFY_SERVICE_SETTING)) {
                serviceChild = child;
            } else if (TextUtils.equals(name, MsgConstants.NOTIFY_PROMOTION_SETTING)) {
                promotionChild = child;
            }
        }
        initSystem(taskChild.getStatus() == 1, sTask);
        initSystem(serviceChild.getStatus() == 1, sService);
        initSystem(promotionChild.getStatus() == 1, sPromotion);
    }

    private void addData(List<MsgSettingChildren> data) {
        if (data.size() > 0) {
            initDevice();
            deviceMsg.clear();
            deviceMsg.addAll(data);
        }
    }

    private void initDevice() {
        if (adapter == null) {
            adapter = new CommonListAdapter<MsgSettingChildren>(context, R.layout.item_msg_device_setting, deviceMsg) {
                @Override
                public void convert(ViewHolder holder, MsgSettingChildren msgSettingChildren) {
                    String name = msgSettingChildren.getName();
                    SettingItemLayout silDevice = holder.getView(R.id.sil_device);
                    if (msgSettingChildren.getStatus() == 1) {
                        silDevice.setRightText(getString(R.string.sm_enable));
                    } else {
                        silDevice.setRightText(getString(R.string.str_close));
                    }
                    String title = "";
                    if (TextUtils.equals(name, MsgConstants.NOTIFY_IPC_SETTING)) {
                        title = getString(R.string.msg_ipc);
                    } else if (TextUtils.equals(name, MsgConstants.NOTIFY_ESL_SETTING)) {
                        title = getString(R.string.msg_esl);
                    }
                    silDevice.setLeftText(title);
                    titleMap.put(name, title);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MsgSettingDetailActivity_.intent(context).child(msgSettingChildren).title(titleMap.get(name)).start();
                        }
                    });
                }
            };
            rvDevice.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void initSystem(boolean status, Switch sw) {
        sw.setChecked(status);
        sw.setOnCheckedChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        BaseNotification.newInstance().postNotificationName(NotificationConstant.msgReadedOrChange);
        finish();
    }

    @Override
    public void getSettingListFail(int code, String msg) {
        nsvSetting.setVisibility(View.GONE);
        networkError.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateSettingStatusSuccess(int msgId, int status) {

    }

    @Override
    public void updateSettingStatusFail(int msgId, int status) {
        if (msgId == taskChild.getId()) {
            initSystem(status == 0, sTask);    //由于更改状态失败，现在的状态和需要更改的状态相反
        } else if (msgId == serviceChild.getId()) {
            initSystem(status == 0, sService);
        } else {
            initSystem(status == 0, sPromotion);
        }
    }

    @Click(R.id.btn_refresh)
    void refeshClick() {
        mPresenter.getSettingList();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_task:
                changeStatus(isChecked, taskChild.getId());
                break;
            case R.id.switch_service:
                changeStatus(isChecked, serviceChild.getId());
                break;
            case R.id.switch_promotion:
                changeStatus(isChecked, promotionChild.getId());
                break;
            default:
                break;
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationConstant.msgDeviceChange) {
            mPresenter.getSettingList();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.msgDeviceChange};
    }

    private void changeStatus(boolean isChecked, int settingId) {
        if (isChecked) {
            mPresenter.updateSettingStatus(settingId, 1);
        } else {
            mPresenter.updateSettingStatus(settingId, 0);
        }
    }
}