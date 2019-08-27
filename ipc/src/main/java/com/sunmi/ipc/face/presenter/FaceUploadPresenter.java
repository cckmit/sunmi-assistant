package com.sunmi.ipc.face.presenter;

import com.sunmi.ipc.face.contract.FaceUploadContract;
import com.sunmi.ipc.face.model.UploadImage;

import java.util.List;

import sunmi.common.base.BasePresenter;

/**
 * @author yinhui
 * @date 2019-08-27
 */
public class FaceUploadPresenter extends BasePresenter<FaceUploadContract.View>
        implements FaceUploadContract.Presenter {


    @Override
    public void upload(UploadImage image) {

    }

    @Override
    public void save(List<UploadImage> list) {

    }
}
