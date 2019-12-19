package com.sunmi.assistant.mine.message;

import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.MsgSettingContract;
import com.sunmi.assistant.mine.model.MsgSettingChildren;
import com.sunmi.assistant.mine.model.MsgSettingListBean;
import com.sunmi.assistant.mine.presenter.MsgSettingPresenter;
import com.sunmi.assistant.utils.MessageUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

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
 * Description:
 *
 * @author linyuanpeng on 2019-08-23.
 */
@EActivity(R.layout.activity_msg_setting)
public class MsgSettingActivity extends BaseMvpActivity<MsgSettingPresenter>
        implements MsgSettingContract.View {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.nsv_setting)
    NestedScrollView nsvSetting;
    @ViewById(R.id.rv_device)
    SmRecyclerView rvDevice;
    @ViewById(R.id.layout_network_error)
    View networkError;
    @ViewById(R.id.switch_task)
    SettingItemLayout sTask;
    @ViewById(R.id.switch_service)
    SettingItemLayout sService;
    @ViewById(R.id.switch_promotion)
    SettingItemLayout sPromotion;

    private CommonListAdapter adapter;
    private MsgSettingChildren taskChild, serviceChild, promotionChild;
    private List<MsgSettingChildren> deviceMsg = new ArrayList<>();
    private boolean allowCheck = true;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
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
        sTask.setChecked(taskChild.getStatus() == 1);
        sTask.setOnCheckedChangeListener((buttonView, isChecked) -> changeStatus(isChecked, taskChild.getId(), sTask));
        sService.setChecked(serviceChild.getStatus() == 1);
        sService.setOnCheckedChangeListener((buttonView, isChecked) -> changeStatus(isChecked, serviceChild.getId(), sService));
        sPromotion.setChecked(promotionChild.getStatus() == 1);
        sPromotion.setOnCheckedChangeListener((buttonView, isChecked) -> changeStatus(isChecked, promotionChild.getId(), sPromotion));
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
            adapter = new CommonListAdapter<MsgSettingChildren>(context, R.layout.item_common_arrow, deviceMsg) {
                @Override
                public void convert(ViewHolder holder, MsgSettingChildren msgSettingChildren) {
                    SettingItemLayout silDevice = holder.getView(R.id.sil_item);
                    if (msgSettingChildren.getStatus() == 1) {
                        silDevice.setEndContent(R.string.sm_enable);
                    } else {
                        silDevice.setEndContent(R.string.str_close);
                    }
                    String title = MessageUtils.getInstance().getMsgFirst(msgSettingChildren.getName());
                    silDevice.setTitle(title);
                    holder.itemView.setOnClickListener(v ->
                            MsgSettingDetailActivity_.intent(context)
                                    .child(msgSettingChildren)
                                    .title(title)
                                    .start());
                }
            };
            rvDevice.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        BaseNotification.newInstance().postNotificationName(CommonNotifications.msgCenterBadgeUpdate);
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
        allowCheck = false;
        if (msgId == taskChild.getId()) {
            sTask.setChecked(status == 0);//由于更改状态失败，现在的状态和需要更改的状态相反
        } else if (msgId == serviceChild.getId()) {
            sService.setChecked(status == 0);
        } else {
            sPromotion.setChecked(status == 0);
        }
    }

    @Click(R.id.btn_refresh)
    void refeshClick() {
        mPresenter.getSettingList();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.msgSettingsChange) {
            mPresenter.getSettingList();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.msgSettingsChange};
    }

    private void changeStatus(boolean isChecked, int settingId, SettingItemLayout sw) {
        if (noNetCannotClick()) {
            sw.setChecked(!isChecked);
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
