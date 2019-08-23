package com.sunmi.assistant.mine.message;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.model.MsgSettingListBean;
import com.sunmi.assistant.rpc.MessageCenterApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.tablayout.CommonTabLayout;
import sunmi.common.view.tablayout.listener.CustomTabEntity;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
@EActivity(R.layout.activity_msg_center)
public class MsgCenterActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.tab_message)
    CommonTabLayout commonTabLayout;
    @ViewById(R.id.frame_message)
    FrameLayout frameLayout;

    private ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();


    @AfterViews
    void init() {
        titleBar.getRightTextView().setOnClickListener(this);
        tabEntities.add(new TabEntity(getString(R.string.str_device_msg)));
        tabEntities.add(new TabEntity(getString(R.string.str_system_msg)));
        fragments.add(DeviceMessageFragment_.builder().build());
        fragments.add(SystemMessageFragment_.builder().build());
        commonTabLayout.setTabData(tabEntities, this, R.id.frame_message, fragments);
        initDot();
    }

    @Override
    public void onClick(View v) {
        MessageCenterApi.getInstance().getSettingList(new RetrofitCallback<MsgSettingListBean>() {
            @Override
            public void onSuccess(int code, String msg, MsgSettingListBean data) {

            }

            @Override
            public void onFail(int code, String msg, MsgSettingListBean data) {

            }
        });
    }

    private void initDot() {
        if (SpUtils.getUnreadDeviceMsg() > 0) {
            commonTabLayout.showDot(0);
        } else {
            commonTabLayout.hideMsg(0);
        }
        if (SpUtils.getUnreadSystemMsg() > 0) {
            commonTabLayout.showDot(1);
        } else {
            commonTabLayout.hideMsg(1);
        }
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationConstant.msgUpdated) {
            initDot();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{NotificationConstant.msgUpdated};
    }
}
