package com.sunmi.ipc.face.contract;

import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceGroup;

import java.io.File;
import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public interface FaceListContract {

    interface View extends BaseView {

        void updateGroup(FaceGroup group);

        void updateGroupList(List<FaceGroup> list);

        void updateFilter(List<FilterItem> gender, List<FilterItem> age);

        void updateList(List<Face> list, boolean hasMore);

        void addList(List<Face> list, boolean hasMore);

        void getDataFailed();

        void uploadSuccess();

        void uploadFailed(int code, String file);

        void resetView();
    }

    interface Presenter {

        void init();

        void loadGroup();

        void refresh();

        boolean getMore();

        void filterName(String name);

        void filterGender(int gender);

        void filterAge(int age);

        void move(List<Face> list, FaceGroup group);

        void delete(List<Face> list);

        void upload(File file);

    }
}
