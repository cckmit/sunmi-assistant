package com.sunmi.ipc.face.contract;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public interface FaceGroupDetailContract {

    interface View extends BaseView {

        void updateNameView(String name);

        void updateCapacityView(int capacity);

        void updateThresholdView(int times, int days);

        void updateMarkView(String mark);

        void deleteSuccess();
    }

    interface Presenter {

        void updateName(String name);

        void updateCapacity(int capacity);

        void updateThreshold(int times, int days);

        void updateMark(String mark);

        void delete();
    }
}
