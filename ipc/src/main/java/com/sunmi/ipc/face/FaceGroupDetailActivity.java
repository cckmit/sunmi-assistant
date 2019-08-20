package com.sunmi.ipc.face;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceGroupDetailContract;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FaceGroupDetailPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-08-20
 */
@EActivity(resName = "face_activity_group_detail")
public class FaceGroupDetailActivity extends BaseMvpActivity<FaceGroupDetailPresenter>
        implements FaceGroupDetailContract.View {

    private static final int SECONDS_PER_DAY = 86400;

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "tv_face_group_tip")
    TextView mTvTip;
    @ViewById(resName = "sil_face_group_name")
    SettingItemLayout mSilName;
    @ViewById(resName = "sil_face_group_capacity")
    SettingItemLayout mSilCapacity;
    @ViewById(resName = "sil_face_group_threshold")
    SettingItemLayout mSilThreshold;
    @ViewById(resName = "sil_face_group_mark")
    SettingItemLayout mSilMark;
    @ViewById(resName = "layout_face_group_notification")
    ConstraintLayout mLayoutNotification;
    @ViewById(resName = "switch_face_group_notification")
    Switch mSwitchNotification;
    @ViewById(resName = "sil_face_group_manage")
    SettingItemLayout mSilManage;

    @Extra
    int mShopId;
    @Extra
    FaceGroup mFaceGroup;
    @Extra
    int mOccupiedCapacity;

    @AfterViews
    void init() {
        mTitleBar.setAppTitle(Utils.getGroupName(this, mFaceGroup, false));
        if (mFaceGroup.isSystemType()) {
            mTitleBar.setRightTextViewEnable(false);
            mSilName.setRightImage(null);
        } else {
            mTitleBar.setRightTextViewEnable(true);
            mTitleBar.setRightTextViewText(R.string.ipc_setting_delete);
            mTitleBar.getRightText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickDelete();
                }
            });
        }

        if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_NEW) {
            mTvTip.setVisibility(View.VISIBLE);
            mTvTip.setText(R.string.ipc_face_group_new_desc);
        } else if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_STAFF) {
            mTvTip.setVisibility(View.VISIBLE);
            mTvTip.setText(R.string.ipc_face_group_staff_desc);
        } else {
            mTvTip.setVisibility(View.GONE);
        }


        mSilName.setRightText(Utils.getGroupName(this, mFaceGroup, false));
        mSilCapacity.setRightText(String.valueOf(mFaceGroup.getCapacity()));

        if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_NEW) {
            mSilThreshold.setVisibility(View.VISIBLE);
            mSilThreshold.setRightText(getString(R.string.ipc_face_group_threshold_content,
                    mFaceGroup.getPeriodDays(), mFaceGroup.getThreshold()));
        } else {
            mSilThreshold.setVisibility(View.GONE);
        }

        if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_BLACK) {
            mLayoutNotification.setVisibility(View.VISIBLE);
            mSwitchNotification.setChecked(mFaceGroup.getAlarmNotified() != 0);
        } else {
            mLayoutNotification.setVisibility(View.GONE);
        }

        mSilManage.setRightText(getString(R.string.ipc_face_group_count, mFaceGroup.getCount()));
    }

    @Click(resName = "sil_face_group_name")
    void clickName() {

    }

    @Click(resName = "sil_face_group_capacity")
    void clickCapacity() {

    }

    @Click(resName = "sil_face_group_threshold")
    void clickThreshold() {

    }

    @Click(resName = "sil_face_group_mark")
    void clickMark() {

    }

    @Click(resName = "sil_face_group_manage")
    void clickManage() {

    }

    private void clickDelete() {

    }

    @Override
    public void updateNameView(String name) {
        mFaceGroup.setGroupName(name);
        mSilName.setRightText(name);
    }

    @Override
    public void updateCapacityView(int capacity) {
        mFaceGroup.setCapacity(capacity);
        mSilCapacity.setRightText(String.valueOf(capacity));
    }

    @Override
    public void updateThresholdView(int times, int days) {
        mFaceGroup.setThreshold(times, days);
        mSilThreshold.setRightText(getString(R.string.ipc_face_group_threshold_content, days, times));
    }

    @Override
    public void updateMarkView(String mark) {
        mFaceGroup.setMark(mark);
    }

    @Override
    public void deleteSuccess() {
        setResult(RESULT_OK);
        finish();
    }
}
