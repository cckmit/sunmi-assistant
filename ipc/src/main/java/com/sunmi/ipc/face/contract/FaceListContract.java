package com.sunmi.ipc.face.contract;

import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public interface FaceListContract {

    interface View extends BaseView {

        void updateFilter(List<FaceAge> list);

        void updateGroupList(List<FaceGroup> list);

        void updateList(List<Face> list, boolean hasMore);

        void addList(List<Face> list, boolean hasMore);

        void getDataFailed();
    }

    interface Presenter {

        void init();

        void getMore();

        void filterName(String name);

        void filterGender(int gender);

        void filterAge(int age);

        void move(List<Face> list, FaceGroup group);

        void delete(List<Face> list);

        void loadGroup();
    }
}
