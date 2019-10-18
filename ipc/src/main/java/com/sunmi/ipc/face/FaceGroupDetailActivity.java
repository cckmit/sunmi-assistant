package com.sunmi.ipc.face;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceGroupDetailContract;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.presenter.FaceGroupDetailPresenter;
import com.sunmi.ipc.face.util.Constants;
import com.sunmi.ipc.face.util.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.BottomDialog;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.dialog.InputDialog;
import sunmi.common.view.loopview.LoopView;
import sunmi.common.view.loopview.OnItemSelectedListener;

/**
 * @author yinhui
 * @date 2019-08-20
 */
@EActivity(resName = "face_activity_group_detail")
public class FaceGroupDetailActivity extends BaseMvpActivity<FaceGroupDetailPresenter>
        implements FaceGroupDetailContract.View {

    private static final int REQUEST_CODE = 100;
    private static final int IPC_NAME_MAX_LENGTH = 36;
    private static final int IPC_MARK_MAX_LENGTH = 100;

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

    private int times, days;

    private Dialog mDeleteForbiddenDialog;
    private Dialog mDeleteDialog;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mTitleBar.setAppTitle(Utils.getGroupName(this, mFaceGroup));
        if (mFaceGroup.isSystemType()) {
            mTitleBar.setRightTextViewEnable(false);
            mTitleBar.setRightTextViewText("");
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

        mTvTip.setVisibility(mFaceGroup.isSystemType() ? View.VISIBLE : View.GONE);
        if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_NEW) {
            mTvTip.setText(R.string.ipc_face_group_new_desc);
        } else if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_OLD) {
            mTvTip.setText(R.string.ipc_face_group_old_desc);
        } else if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_STAFF) {
            mTvTip.setText(R.string.ipc_face_group_staff_desc);
        } else if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_BLACK) {
            mTvTip.setText(R.string.ipc_face_group_black_desc);
        }

        mSilName.setRightText(Utils.getGroupName(this, mFaceGroup));
        mSilCapacity.setRightText(String.valueOf(mFaceGroup.getCapacity()));

        if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_NEW) {
            mSilThreshold.setVisibility(View.VISIBLE);
            times = mFaceGroup.getThreshold();
            days = mFaceGroup.getPeriodDays();
            mSilThreshold.setRightText(getString(R.string.ipc_face_group_threshold_content,
                    mFaceGroup.getPeriodDays(), mFaceGroup.getThreshold()));
        } else {
            mSilThreshold.setVisibility(View.GONE);
        }
/*
        if (mFaceGroup.getType() == FaceGroup.FACE_GROUP_TYPE_BLACK) {
            mLayoutNotification.setVisibility(View.VISIBLE);
            mSwitchNotification.setChecked(mFaceGroup.getAlarmNotified() != 0);
        } else {
            mLayoutNotification.setVisibility(View.GONE);
        }
*/
        mLayoutNotification.setVisibility(View.GONE);
        mSilMark.getRightText().setSingleLine();
        mSilMark.getRightText().setEllipsize(TextUtils.TruncateAt.END);
        mSilMark.setRightText(mFaceGroup.getMark());
        mSilManage.setRightText(getString(R.string.ipc_face_group_count, mFaceGroup.getCount()));

        mPresenter = new FaceGroupDetailPresenter(mShopId, mFaceGroup, mOccupiedCapacity);
        mPresenter.attachView(this);
    }

    @Click(resName = "sil_face_group_name")
    void clickName() {
        if (mFaceGroup.isSystemType()) {
            return;
        }
        modifyGroupName();
    }

    @Click(resName = "sil_face_group_capacity")
    void clickCapacity() {
        modifyPhotosMax();
    }

    @Click(resName = "sil_face_group_threshold")
    void clickThreshold() {
        moveGroupRule();
    }

    @Click(resName = "sil_face_group_mark")
    void clickMark() {
        modifyMarks();
    }

    @Click(resName = "sil_face_group_manage")
    void clickManage() {
        FaceListActivity_.intent(this)
                .mShopId(mShopId)
                .mFaceGroup(mFaceGroup)
                .startForResult(REQUEST_CODE);
    }

    private void clickDelete() {
        if (mFaceGroup.getCount() > 0) {
            if (mDeleteForbiddenDialog == null) {
                mDeleteForbiddenDialog = new CommonDialog.Builder(this)
                        .setTitle(getString(R.string.ipc_face_group_delete_title,
                                Utils.getGroupName(this, mFaceGroup)))
                        .setMessage(R.string.ipc_face_group_delete_error)
                        .setCancelButton(R.string.sm_cancel)
                        .create();
            }
            mDeleteForbiddenDialog.show();
        } else {
            if (mDeleteDialog == null) {
                mDeleteDialog = new CommonDialog.Builder(this)
                        .setTitle(getString(R.string.ipc_face_group_delete_title,
                                Utils.getGroupName(this, mFaceGroup)))
                        .setConfirmButton(R.string.ipc_setting_delete, R.color.common_orange,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mPresenter.delete();
                                    }
                                })
                        .setCancelButton(R.string.sm_cancel)
                        .create();
            }
            mDeleteDialog.show();
        }
    }

    @Override
    public void updateNameView(String name) {
        mFaceGroup.setGroupName(name);
        mSilName.setRightText(name);
        setResult(RESULT_OK);
    }

    @Override
    public void updateCapacityView(int capacity) {
        mFaceGroup.setCapacity(capacity);
        mSilCapacity.setRightText(String.valueOf(capacity));
        setResult(RESULT_OK);
    }

    @Override
    public void updateThresholdView(int times, int days) {
        mFaceGroup.setThreshold(times, days);
        mSilThreshold.setRightText(getString(R.string.ipc_face_group_threshold_content, days, times));
        setResult(RESULT_OK);
    }

    @Override
    public void updateMarkView(String mark) {
        mFaceGroup.setMark(mark);
        mSilMark.setRightText(mark);
        setResult(RESULT_OK);
    }

    @Override
    public void deleteSuccess() {
        shortTip(R.string.ipc_face_tip_delete_success);
        setResult(RESULT_OK);
        finish();
    }

    @OnActivityResult(REQUEST_CODE)
    void onActivityResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            int count = data.getIntExtra(Constants.EXTRA_UPDATE_COUNT, mFaceGroup.getCount());
            mFaceGroup.setCount(count);
            mSilManage.setRightText(getString(R.string.ipc_face_group_count, mFaceGroup.getCount()));
            setResult(RESULT_OK);
        }
    }

    private void modifyGroupName() {
        new InputDialog.Builder(this)
                .setTitle(R.string.ipc_face_group_name)
                .setHint(getString(R.string.ipc_face_input_name_tip))
                .setInitInputContent(mFaceGroup.getGroupName())
                .setInputWatcher(new InputDialog.TextChangeListener() {
                    @Override
                    public void onTextChange(EditText view, Editable s) {
                        if (TextUtils.isEmpty(s.toString())) {
                            return;
                        }
                        String name = s.toString().trim();
                        if (name.length() > IPC_NAME_MAX_LENGTH) {
                            shortTip(R.string.ipc_setting_tip_name_length);
                            do {
                                name = name.substring(0, name.length() - 1);
                            }
                            while (name.length() > IPC_NAME_MAX_LENGTH);
                            view.setText(name);
                            view.setSelection(name.length());
                        }
                    }
                }).setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.ipc_setting_save, new InputDialog.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(InputDialog dialog, String input) {
                        if (input.length() > IPC_NAME_MAX_LENGTH) {
                            shortTip(getString(R.string.ipc_face_name_length_tip));
                            return;
                        }
                        if (input.trim().length() == 0) {
                            shortTip(getString(R.string.ipc_face_input_name_tip));
                            return;
                        }
                        mPresenter.updateName(input);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void modifyMarks() {
        new InputDialog.Builder(this)
                .setTitle(R.string.ipc_face_group_mark)
                .setHint(getString(R.string.ipc_face_input_marks_tip))
                .setInitInputContent(mFaceGroup.getMark())
                .setEditTextHeight(true, 400, 40, 0, 40, 40)
                .setInputWatcher(new InputDialog.TextChangeListener() {
                    @Override
                    public void onTextChange(EditText view, Editable s) {
                        if (TextUtils.isEmpty(s.toString())) {
                            return;
                        }
                        String name = s.toString().trim();
                        if (name.length() > IPC_MARK_MAX_LENGTH) {
                            shortTip(getString(R.string.ipc_face_name_length100_tip));
                            do {
                                name = name.substring(0, name.length() - 1);
                            }
                            while (name.length() > IPC_MARK_MAX_LENGTH);
                            view.setText(name);
                            view.setSelection(name.length());
                        }
                    }
                })
                .setCancelButton(R.string.sm_cancel)
                .setConfirmButton(R.string.ipc_setting_save, new InputDialog.ConfirmClickListener() {
                    @Override
                    public void onConfirmClick(InputDialog dialog, String input) {
                        if (input.trim().length() == 0) {
                            shortTip(getString(R.string.ipc_face_input_marks_tip));
                            return;
                        }
                        if (input.trim().length() > IPC_MARK_MAX_LENGTH) {
                            shortTip(getString(R.string.ipc_face_name_length100_tip));
                            return;
                        }
                        mPresenter.updateMark(input);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void modifyPhotosMax() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Dialog dialog = new Dialog(context, com.commonlibrary.R.style.Son_dialog);
        assert inflater != null;
        View view = inflater.inflate(R.layout.dialog_photo_input, null);
        final EditText etInput = view.findViewById(R.id.et_input);
        final TextView errorTip = view.findViewById(R.id.tv_error_tip);
        final int nowCapacity = 10000 - mOccupiedCapacity + mFaceGroup.getCapacity();
        etInput.setHint(getString(R.string.ipc_face_photo_num_max));
        errorTip.setText(getString(R.string.ipc_face_photo_num_remainder, nowCapacity));
        view.findViewById(com.commonlibrary.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString().trim()) &&
                        Integer.valueOf(s.toString().trim()) > nowCapacity) {
                    etInput.setBackgroundResource(R.drawable.edittext_edge_red);
                    errorTip.setText(getString(R.string.ipc_face_photo_num_max_more));
                    errorTip.setTextColor(ContextCompat.getColor(context, R.color.caution_primary));
                } else {
                    etInput.setBackgroundResource(R.drawable.edittext_edge_grey2);
                    errorTip.setText(getString(R.string.ipc_face_photo_num_remainder, nowCapacity));
                    errorTip.setTextColor(ContextCompat.getColor(context, R.color.colorText_40));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        view.findViewById(com.commonlibrary.R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etInput.getText() == null || TextUtils.isEmpty(etInput.getText().toString().trim())) {
                    shortTip(getString(R.string.ipc_face_input_photo_num_tip));
                    return;
                }
                int num = Integer.valueOf(etInput.getText().toString().trim());
                if (num > nowCapacity) {
                    errorTip.setText(getString(R.string.ipc_face_photo_num_max_more));
                    errorTip.setTextColor(ContextCompat.getColor(context, R.color.caution_primary));
                    return;
                }
                mPresenter.updateCapacity(num);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void moveGroupRule() {
        int margin = (int) getResources().getDimension(R.dimen.dp_20);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, margin, 0, margin);
        new BottomDialog.Builder(this)
                .setTitle(R.string.ipc_face_group_threshold)
                .setCancelButton(R.string.sm_cancel)
                .setOkButton(R.string.str_complete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.updateThreshold(times, days);
                    }
                })
                .setContent(wheelView(), layoutParams)
                .create()
                .show();
    }

    private View wheelView() {
        int listLength = 101;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.item_wheelview, null);
        LoopView lvWheelLeft = view.findViewById(R.id.lv_wheel_left);
        LoopView lvWheelRight = view.findViewById(R.id.lv_wheel_right);
        ArrayList<String> listLeft = new ArrayList<>();
        ArrayList<String> listRight = new ArrayList<>();
        for (int i = 1; i < listLength; i++) {
            listLeft.add(i + getString(R.string.ipc_face_day));
            listRight.add(i + getString(R.string.ipc_face_order));
        }
        //设置是否循环播放
//        lvWheelLeft.setNotLoop();
//        lvWheelRight.setNotLoop();
        //滚动监听
        lvWheelLeft.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                days = index + 1;
            }
        });
        lvWheelRight.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                times = index + 1;
            }
        });
        //设置原始数据
        lvWheelLeft.setItems(listLeft);
        lvWheelRight.setItems(listRight);
        //设置初始位置
        lvWheelLeft.setInitPosition(days - 1);
        lvWheelRight.setInitPosition(times - 1);
        return view;
    }
}
