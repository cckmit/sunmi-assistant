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

        void updateRegularView(int times, int days);

        void updateMarkView(String mark);
    }

    interface Presenter {

        void updateName(String name);

        void updateCapacity(int capacity);

        void updateRegular(int times, int days);

        void updateMark(String mark);
    }
}
