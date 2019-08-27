package com.sunmi.ipc.face.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceListContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceCheckResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.model.FaceListResp;
import com.sunmi.ipc.model.FaceSaveResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import sunmi.common.base.BasePresenter;
import sunmi.common.model.FilterItem;
import sunmi.common.rpc.retrofit.BaseResponse;
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

    private List<FilterItem> mFilterGenderList = new ArrayList<>(3);
    private List<FilterItem> mFilterAgeList = new ArrayList<>();

    private Call<BaseResponse<FaceListResp>> mCall;

    public FaceListPresenter(int shopId, FaceGroup group) {
        this.mShopId = shopId;
        this.mGroup = group;
    }

    @Override
    public void init(Context context) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        mFilterGenderList.clear();
        mFilterAgeList.clear();
        FilterItem allGender = new FilterItem(-1, context.getString(R.string.ipc_face_gender),
                context.getString(R.string.ipc_face_gender_all));
        allGender.setChecked(true);
        mFilterGenderList.add(allGender);
        mFilterGenderList.add(new FilterItem(1, context.getString(R.string.ipc_face_gender_male)));
        mFilterGenderList.add(new FilterItem(2, context.getString(R.string.ipc_face_gender_female)));
        FilterItem allAge = new FilterItem(-1, context.getString(R.string.ipc_face_age),
                context.getString(R.string.ipc_face_age_all));
        allAge.setChecked(true);
        mFilterAgeList.add(allAge);
        loadFilter();
        loadGroup();
    }

    @Override
    public boolean getMore() {
        if (mCurrentCount >= mTotalCount) {
            return false;
        } else {
            loadFace(false, false);
            return true;
        }
    }

    @Override
    public void filterName(String name) {
        if (TextUtils.equals(mFilterName, name)) {
            return;
        }
        mFilterName = name;
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
        }
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
    public void move(final List<Face> list, FaceGroup group) {
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
                        loadFace(true, true);
                        if (isViewAttached()) {
                            mView.updateCount(-list.size());
                            mView.shortTip(R.string.ipc_face_tip_move_success);
                            mView.resetView();
                        }
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
    public void delete(final List<Face> list) {
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
                        loadFace(true, true);
                        if (isViewAttached()) {
                            mView.updateCount(-list.size());
                            mView.shortTip(R.string.ipc_face_tip_delete_success);
                            mView.resetView();
                        }
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
    public void upload(File file) {
        IpcCloudApi.uploadFaceAndCheck(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(), file,
                new RetrofitCallback<FaceCheckResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceCheckResp data) {
                        save(data);
                    }

                    @Override
                    public void onFail(int code, String msg, FaceCheckResp data) {
                        LogCat.e(TAG, "Check face file Failed. " + msg);
                        if (isViewAttached()) {
                            mView.uploadFailed();
                        }
                    }
                });
    }

    public void save(FaceCheckResp data) {
        List<String> name = new ArrayList<>(1);
        name.add(data.getFileName());
        IpcCloudApi.saveFace(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(), name,
                new RetrofitCallback<FaceSaveResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceSaveResp data) {
                        if (!data.getSuccessList().isEmpty()) {
                            loadFace(true, true);
                            if (isViewAttached()) {
                                mView.updateCount(1);
                                mView.uploadSuccess();
                            }
                        } else if (isViewAttached()) {
                            mView.uploadFailed();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceSaveResp data) {
                        LogCat.e(TAG, "Save face file Failed. " + msg);
                        if (isViewAttached()) {
                            mView.uploadFailed();
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
                    List<FaceAge> faceAges = data.getAgeRangeList();
                    for (FaceAge age : faceAges) {
                        mFilterAgeList.add(new FilterItem(age.getCode(), age.getName()));
                    }
                    mView.updateFilter(mFilterGenderList, mFilterAgeList);
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
                ListIterator<FaceGroup> iterator = list.listIterator();
                while (iterator.hasNext()) {
                    FaceGroup next = iterator.next();
                    if (next.getGroupId() == mGroup.getGroupId()) {
                        iterator.remove();
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

    @Override
    public void loadFace(final boolean refresh, boolean clearFilter) {
        if (refresh) {
            mCurrentCount = 0;
        }
        if (clearFilter) {
            mFilterName = null;
            mFilterGender = -1;
            mFilterAge = -1;
        }
        final int page = refresh ? PAGE_INIT : mCurrentPage + 1;
        mCall = IpcCloudApi.getFaceList(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(),
                mFilterGender, mFilterAge, mFilterName, page, PAGE_SIZE, new RetrofitCallback<FaceListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceListResp data) {
                        mCall = null;
                        mCurrentPage = page;
                        mTotalCount = data.getTotalCount();
                        mCurrentCount += data.getFaceList().size();
                        if (isViewAttached()) {
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
                        mCall = null;
                        LogCat.e(TAG, "Load face list Failed. " + msg);
                        if (isViewAttached()) {
                            mView.getDataFailed();
                        }
                    }
                });
    }
}
