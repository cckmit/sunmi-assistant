package com.sunmi.ipc.face.contract;

import com.sunmi.ipc.face.model.UploadImage;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @date 2019-08-26
 */
public interface FaceUploadContract {

    long FILE_SIZE_1M = 1_000_000L;

    interface View extends BaseView {

        void saveComplete();

        void saveFailed();

        void uploadSuccess(UploadImage image);

        void uploadFailed(UploadImage image);
    }

    interface Presenter {

        void upload(UploadImage image);

        void save(List<UploadImage> list);
    }
}