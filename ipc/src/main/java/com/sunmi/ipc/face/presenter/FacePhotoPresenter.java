package com.sunmi.ipc.face.presenter;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.contract.FacePhotoContract;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
public class FacePhotoPresenter extends BasePresenter<FacePhotoContract.View>
        implements FacePhotoContract.Presenter {
    private int mShopId;

    public FacePhotoPresenter(int mShopId) {
        this.mShopId = mShopId;
    }

    @Override
    public void updateName(int faceId, int sourceGroupId, final String name) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceName(SpUtils.getCompanyId(), mShopId,
                faceId, sourceGroupId, name, new RetrofitCallback<Object>() {
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
    public void updateIdentity(int faceId, int sourceGroupId, int targetGroupId) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceTargetGroupId(SpUtils.getCompanyId(), mShopId,
                faceId, sourceGroupId, targetGroupId, new RetrofitCallback<Object>() {
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
    public void updateGender(int faceId, int sourceGroupId, final int gender) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceGender(SpUtils.getCompanyId(), mShopId,
                faceId, sourceGroupId, gender, new RetrofitCallback<Object>() {
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
    public void updateAge(int faceId, int sourceGroupId, final int ageRangeCode) {
        mView.showLoadingDialog();
        IpcCloudApi.updateFaceAgeRangeCode(SpUtils.getCompanyId(), mShopId,
                faceId, sourceGroupId, ageRangeCode, new RetrofitCallback<Object>() {
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
