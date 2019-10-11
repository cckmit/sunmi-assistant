package com.sunmi.ipc.face.presenter;

import com.sunmi.ipc.face.contract.FaceUploadContract;
import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.face.model.UploadImage;
import com.sunmi.ipc.model.FaceCheckResp;
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
 * @author yinhui
 * @date 2019-08-27
 */
public class FaceUploadPresenter extends BasePresenter<FaceUploadContract.View>
        implements FaceUploadContract.Presenter {

    private static final String TAG = FaceUploadPresenter.class.getSimpleName();

    private int mShopId;
    private FaceGroup mGroup;

    public FaceUploadPresenter(int shopId, FaceGroup group) {
        this.mShopId = shopId;
        this.mGroup = group;
    }

    @Override
    public void upload(final UploadImage image) {
        IpcCloudApi.getInstance().uploadFaceAndCheck(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(), new File(image.getCompressed()),
                new RetrofitCallback<FaceCheckResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceCheckResp data) {
                        image.setCloudName(data.getFileName());
                        if (isViewAttached()) {
                            mView.uploadSuccess(image);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceCheckResp data) {
                        LogCat.e(TAG, "Check face file Failed. " + code + ":" + msg);
                        if (code == 5526) {
                            image.setState(UploadImage.STATE_FAILED);
                        } else {
                            image.setState(UploadImage.STATE_FAILED_NET);
                        }
                        if (isViewAttached()) {
                            mView.uploadFailed(image);
                        }
                    }
                });
    }

    @Override
    public void save(List<UploadImage> list) {
        List<String> name = new ArrayList<>(list.size());
        for (UploadImage image : list) {
            name.add(image.getCloudName());
        }
        IpcCloudApi.getInstance().saveFace(SpUtils.getCompanyId(), mShopId, mGroup.getGroupId(), 0, name,
                new RetrofitCallback<FaceSaveResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceSaveResp data) {
                        if (isViewAttached()) {
                            if (!data.getSuccessList().isEmpty()) {
                                mView.saveComplete(data.getSuccessList().size());
                            } else {
                                mView.saveFailed();
                            }
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceSaveResp data) {
                        LogCat.e(TAG, "Save face file Failed. " + msg);
                        if (isViewAttached()) {
                            mView.saveFailed();
                        }
                    }
                });
    }
}
