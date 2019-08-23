package com.sunmi.ipc.face.contract;

import android.content.Context;

import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.face.model.FaceGroup;

import java.util.List;

import sunmi.common.base.BaseView;
import sunmi.common.model.FilterItem;

/**
 * @author yinhui
 * @date 2019-08-20
 */
public interface FaceListContract {

    interface View extends BaseView {

        void updateFilter(List<FilterItem> gender, List<FilterItem> age);

        void updateGroupList(List<FaceGroup> list);

        void updateList(List<Face> list, boolean hasMore);

        void addList(List<Face> list, boolean hasMore);

        void resetView();

        void getDataFailed();
    }

    interface Presenter {

        void init(Context context);

        boolean getMore();

        void filterName(String name);

        void filterGender(int gender);

        void filterAge(int age);

        void move(List<Face> list, FaceGroup group);

        void delete(List<Face> list);

        void loadGroup();

        void loadFace(boolean refresh, boolean clearFilter);
    }
}
