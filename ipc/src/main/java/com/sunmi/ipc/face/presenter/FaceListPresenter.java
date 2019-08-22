package com.sunmi.ipc.face.presenter;

import android.text.TextUtils;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceListContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.model.FaceListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public class FaceListPresenter extends BasePresenter<FaceListContract.View>
        implements FaceListContract.Presenter {

    private static final String TAG = FaceListPresenter.class.getSimpleName();

    private static final int PAGE_INIT = 1;
    private static final int PAGE_SIZE = 40;

    private int mShopId;
    private FaceGroup mGroup;

    private int mCurrentPage = PAGE_INIT;
    private int mTotalCount = 0;
    private int mCurrentCount = 0;
    private String mFilterName = null;
    private int mFilterGender = -1;
    private int mFilterAge = -1;

    public FaceListPresenter(int shopId, FaceGroup group) {
        this.mShopId = shopId;
        this.mGroup = group;
    }

    @Override
    public void init() {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        loadFilter();
        loadGroup();
    }

    @Override
    public void getMore() {
        loadFace(false, false);
    }

    @Override
    public void filterName(String name) {
        if (TextUtils.equals(mFilterName, name)) {
            return;
        }
        mFilterName = name;
        loadFace(true, false);
    }

    @Override
    public void filterGender(int gender) {
        if (mFilterGender == gender) {
            return;
        }
        mFilterGender = gender;
        loadFace(true, false);
    }

    @Override
    public void filterAge(int age) {
        if (mFilterAge == age) {
            return;
        }
        mFilterAge = age;
        loadFace(true, false);
    }

    @Override
    public void move(List<Face> list, FaceGroup group) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        List<Integer> ids = new ArrayList<>(list.size());
        for (Face face : list) {
            ids.add(face.getFaceId());
        }
        IpcCloudApi.moveFace(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(), group.getGroupId(),
                ids, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.ipc_face_tip_move_success);
                        }
                        loadFace(true, true);
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Move face to another group Failed. " + msg);
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.toast_network_error);
                        }
                    }
                });
    }

    @Override
    public void delete(List<Face> list) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        List<Integer> ids = new ArrayList<>(list.size());
        for (Face face : list) {
            ids.add(face.getFaceId());
        }
        IpcCloudApi.deleteFace(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(), ids,
                new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.ipc_face_tip_delete_success);
                        }
                        loadFace(true, true);
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Move face to another group Failed. " + msg);
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.toast_network_error);
                        }
                    }
                });
    }

    private void loadFilter() {
        IpcCloudApi.getFaceAgeRange(SpUtils.getCompanyId(), mShopId, new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                loadFace(true, true);
                if (isViewAttached()) {
                    mView.updateFilter(data.getAgeRangeList());
                }
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                LogCat.e(TAG, "Load filter age range Failed. " + msg);
                if (isViewAttached()) {
                    mView.getDataFailed();
                }
            }
        });
    }

    @Override
    public void loadGroup() {
        IpcCloudApi.getFaceGroup(SpUtils.getCompanyId(), mShopId, new RetrofitCallback<FaceGroupListResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceGroupListResp data) {
                List<FaceGroup> list = data.getGroupList();
                for (FaceGroup group : list) {
                    if (group.getGroupId() == mGroup.getGroupId()) {
                        list.remove(group);
                    }
                }
                if (isViewAttached()) {
                    mView.updateGroupList(list);
                }
            }

            @Override
            public void onFail(int code, String msg, FaceGroupListResp data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.toast_network_error);
                    mView.updateGroupList(null);
                }
            }
        });
    }

    private void loadFace(final boolean refresh, boolean clearFilter) {
        if (refresh) {
            mCurrentCount = 0;
        }
        if (clearFilter) {
            mFilterName = null;
            mFilterGender = -1;
            mFilterAge = -1;
        }
        final int page = refresh ? PAGE_INIT : mCurrentPage + 1;
        IpcCloudApi.getFaceList(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(),
                mFilterGender, mFilterAge, mFilterName, page, PAGE_SIZE, new RetrofitCallback<FaceListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceListResp data) {
                        mCurrentPage = page;
                        mTotalCount = data.getTotalCount();
                        mCurrentCount += data.getFaceList().size();
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            boolean hasMore = mCurrentCount < mTotalCount;
                            if (refresh) {
                                mView.updateList(data.getFaceList(), hasMore);
                            } else {
                                mView.addList(data.getFaceList(), hasMore);
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceListResp data) {
                        LogCat.e(TAG, "Load face list Failed. " + msg);
                        if (isViewAttached()) {
                            mView.getDataFailed();
                        }
                    }
                });
    }
}
