package com.sunmi.ipc.face;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.util.Utils;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.base.recycle.BaseArrayAdapter;
import sunmi.common.base.recycle.BaseViewHolder;
import sunmi.common.base.recycle.ItemType;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-08-15
 */
@EActivity(resName = "face_activity_group_list")
public class FaceGroupListActivity extends BaseActivity {

    private static final int REQUEST_CODE_UPDATE = 100;
    private static final int GROUP_LIST_MAX_COUNT = 12;

    @ViewById(resName = "title_bar")
    TitleBarView mTitleBar;
    @ViewById(resName = "rv_face_group")
    RecyclerView mRvGroupList;

    @Extra
    int mShopId;
    int mOccupiedCapacity;

    private BaseArrayAdapter<Object> mAdapter;
    private List<Object> mFaceGroup = new ArrayList<>();

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mTitleBar.getRightText().setOnClickListener(v -> createGroup());
        initRecyclerView();
        getGroup();
    }

    private void initRecyclerView() {
        mRvGroupList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseArrayAdapter<>();
        FaceTitleType titleType = new FaceTitleType();
        FaceGroupType groupType = new FaceGroupType();
        groupType.setOnItemClickListener((holder, model, position) -> {
            LogCat.d(TAG, "FaceGroup: " + model);
            openGroupDetail(model);
        });
        mAdapter.register(String.class, titleType);
        mAdapter.register(FaceGroup.class, groupType);
        mRvGroupList.setAdapter(mAdapter);
    }

    private void getGroup() {
        showLoadingDialog();
        IpcCloudApi.getInstance().getFaceGroup(SpUtils.getCompanyId(), mShopId,
                new RetrofitCallback<FaceGroupListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceGroupListResp data) {
                        hideLoadingDialog();
                        List<FaceGroup> list = data.getGroupList();
                        Collections.sort(list, (o1, o2) -> o1.getType() - o2.getType());
                        mFaceGroup.clear();
                        mOccupiedCapacity = 0;
                        mFaceGroup.addAll(list);
                        for (FaceGroup group : list) {
                            mOccupiedCapacity += group.getCapacity();
                        }
                        mFaceGroup.add(0, context.getString(R.string.ipc_face_group_default));
                        if (mFaceGroup.size() > FaceGroup.FACE_GROUP_TYPE_CUSTOM) {
                            mFaceGroup.add(FaceGroup.FACE_GROUP_TYPE_CUSTOM, context.getString(R.string.ipc_face_group_custom));
                        }
                        mAdapter.setData(mFaceGroup);
                    }

                    @Override
                    public void onFail(int code, String msg, FaceGroupListResp data) {
                        hideLoadingDialog();
                        LogCat.e(TAG, "Get face group Failed. " + msg);
                    }
                });
    }

    @Click(resName = "tv_face_group_create")
    void createGroup() {
        if (mFaceGroup.size() >= GROUP_LIST_MAX_COUNT) {
            shortTip(R.string.ipc_face_group_create_full);
            return;
        }
        FaceGroupCreateActivity_.intent(this)
                .mShopId(mShopId)
                .mOccupiedCapacity(mOccupiedCapacity)
                .startForResult(REQUEST_CODE_UPDATE);
    }

    private void openGroupDetail(FaceGroup model) {
        FaceGroupDetailActivity_.intent(this)
                .mShopId(mShopId)
                .mFaceGroup(model)
                .mOccupiedCapacity(mOccupiedCapacity)
                .startForResult(REQUEST_CODE_UPDATE);
    }

    @OnActivityResult(REQUEST_CODE_UPDATE)
    void onCreateResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            getGroup();
        }
    }

    private static class FaceTitleType extends ItemType<String, BaseViewHolder<String>> {

        @Override
        public int getLayoutId(int type) {
            return R.layout.face_item_group_title;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<String> holder, String model, int position) {
            ((TextView) holder.itemView).setText(model);
        }
    }

    private static class FaceGroupType extends ItemType<FaceGroup, BaseViewHolder<FaceGroup>> {

        @Override
        public int getLayoutId(int type) {
            return R.layout.face_item_group;
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder<FaceGroup> holder, FaceGroup model, int position) {
            SettingItemLayout itemView = (SettingItemLayout) holder.itemView;
            itemView.setTitle(Utils.getGroupName(holder.getContext(), model));
            itemView.setEndContent(holder.getContext().getString(R.string.ipc_face_group_count, model.getCount()));
        }
    }
}
