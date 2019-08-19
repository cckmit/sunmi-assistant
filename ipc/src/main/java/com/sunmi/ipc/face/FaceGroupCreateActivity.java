package com.sunmi.ipc.face;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.model.FaceGroupCreateReq;
import com.sunmi.ipc.model.FaceGroupCreateResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-08-16
 */
@EActivity(resName = "face_activity_group_create")
public class FaceGroupCreateActivity extends BaseActivity {

    private static final int MAX_LENGTH_NAME = 20;
    private static final int MAX_LENGTH_MARK = 100;

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "cet_face_group_name")
    ClearableEditText mEtName;
    @ViewById(resName = "cet_face_group_capacity")
    ClearableEditText mEtCapacity;
    @ViewById(resName = "cet_face_group_mark")
    ClearableEditText mEtMark;
    @ViewById(resName = "btn_face_group_save")
    Button mBtnSave;

    @Extra
    int mShopId;
    @Extra
    int mCurrentCapacity;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });
    }

    private void createGroup() {
        String name = mEtName.getText() == null ? null : mEtName.getText().toString().trim();
        int capacity = -1;
        if (mEtCapacity.getText() != null) {
            String str = mEtCapacity.getText().toString().trim();
            if (!TextUtils.isEmpty(str)) {
                capacity = Integer.parseInt(str);
            }
        }
        String mark = mEtMark.getText() == null ? null : mEtMark.getText().toString().trim();

        if (name == null || name.length() == 0 || name.length() > MAX_LENGTH_NAME) {
            shortTip(R.string.ipc_face_group_name_error);
            return;
        }

        if (capacity < 0 || capacity > FaceGroup.MAX_CAPACITY_ALL_GROUP - mCurrentCapacity) {
            shortTip(R.string.ipc_face_group_capacity_error);
            return;
        }

        if (mark == null || mark.length() == 0 || mark.length() > MAX_LENGTH_NAME) {
            shortTip(R.string.ipc_face_group_mark_error);
            return;
        }

        showLoadingDialog();
        IpcCloudApi.createFaceGroup(new FaceGroupCreateReq(SpUtils.getCompanyId(), mShopId, name, mark, capacity),
                new RetrofitCallback<FaceGroupCreateResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceGroupCreateResp data) {
                        hideLoadingDialog();
                        List<FaceGroupCreateResp.CreateResult> list = data.getGroupList();
                        if (list != null && list.size() > 0 && list.get(0).getCode() == 1) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            if (list != null && list.size() > 0) {
                                LogCat.e(TAG, "Create face group Failed. code=" + list.get(0).getCode());
                            } else {
                                LogCat.e(TAG, "Create face group Failed. Result is Empty.");
                            }
                            shortTip(R.string.toast_network_error);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceGroupCreateResp data) {
                        hideLoadingDialog();
                        LogCat.e(TAG, "Create face group Failed. " + msg);
                        shortTip(R.string.toast_network_error);
                    }
                });
    }
}
