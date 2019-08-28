package com.sunmi.ipc.face.presenter;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FaceDetailContract;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceCheckResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.model.FaceSaveResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
public class FaceDetailPresenter extends BasePresenter<FaceDetailContract.View>
        implements FaceDetailContract.Presenter {

    private static final String TAG = FaceDetailPresenter.class.getSimpleName();

    private int mShopId;
    private Face mFace;

    public FaceDetailPresenter(int mShopId, Face face) {
        this.mShopId = mShopId;
        this.mFace = face;
    }

    @Override
    public void upload(File file) {
        IpcCloudApi.uploadFaceAndCheck(SpUtils.getCompanyId(), mShopId, mFace.getGroupId(), file,
                new RetrofitCallback<FaceCheckResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceCheckResp data) {
                        save(data);
                    }

                    @Override
                    public void onFail(int code, String msg, FaceCheckResp data) {
                        LogCat.e(TAG, "Check face file Failed. " + msg);
                        if (isViewAttached()) {
                            mView.updateImageFailed();
                        }
                    }
                });
    }

    private void save(FaceCheckResp data) {
        List<String> name = new ArrayList<>(1);
        name.add(data.getFileName());
        IpcCloudApi.saveFace(SpUtils.getCompanyId(), mShopId, mFace.getGroupId(), mFace.getFaceId(), name,
                new RetrofitCallback<FaceSaveResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceSaveResp data) {
                        if (!data.getSuccessList().isEmpty()) {
                            if (isViewAttached()) {
                                mView.updateImageSuccessView(data.getSuccessList().get(0).getImgUrl());
                            }
                        } else if (isViewAttached()) {
                            mView.updateImageFailed();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceSaveResp data) {
                        LogCat.e(TAG, "Save face file Failed. " + msg);
                        if (isViewAttached()) {
                            mView.updateImageFailed();
                        }
                    }
                });
    }

    @Override
    public void updateName(final String name) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceName(SpUtils.getCompanyId(), mShopId,
                mFace.getFaceId(), mFace.getGroupId(), name, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_success);
                            mView.updateNameSuccessView(name);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_fail);
                        }
                    }
                });
    }

    @Override
    public void updateIdentity(int targetGroupId) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceTargetGroupId(SpUtils.getCompanyId(), mShopId,
                mFace.getFaceId(), mFace.getGroupId(), targetGroupId, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_success);
                            mView.updateIdentitySuccessView();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_fail);
                        }
                    }
                });
    }

    @Override
    public void updateGender(final int gender) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceGender(SpUtils.getCompanyId(), mShopId,
                mFace.getFaceId(), mFace.getGroupId(), gender, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_success);
                            mView.updateGenderSuccessView(gender);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_fail);
                        }
                    }
                });
    }

    @Override
    public void updateAge(final int ageRangeCode) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceAgeRangeCode(SpUtils.getCompanyId(), mShopId,
                mFace.getFaceId(), mFace.getGroupId(), ageRangeCode, new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_success);
                            mView.updateAgeSuccessView(ageRangeCode);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.ipc_setting_fail);
                        }
                    }
                });
    }

    @Override
    public void faceAgeRange() {
        mView.showLoadingDialog();
        IpcCloudApi.getFaceAgeRange(SpUtils.getCompanyId(), mShopId, new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.faceAgeRangeSuccessView(data);
                }
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
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
                if (isViewAttached()) {
                    mView.loadGroupSuccessView(list);
                }
            }

            @Override
            public void onFail(int code, String msg, FaceGroupListResp data) {
            }
        });
    }
}
