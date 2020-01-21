package com.sunmi.ipc.face.contract;

import android.util.SparseArray;

import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceGroup;

import java.io.File;
import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
public interface FaceDetailContract {
    interface View extends BaseView {

        void updateImageSuccessView(String url);

        void updateImageFailed(int code, String file);

        void updateNameSuccessView(String name);

        void updateIdentitySuccessView(int targetGroupId);

        void updateGenderSuccessView(int gender);

        void updateAgeSuccessView(int ageRangeCode);

        void faceAgeRangeSuccessView(SparseArray<FaceAge> ageMap);

        void loadGroupSuccessView(List<FaceGroup> list);

        void deleteSuccess();
    }

    interface Presenter {

        void upload(File file);

        void updateName(String name);

        void updateIdentity(int groupId, int targetGroupId);

        void updateGender(int gender);

        void updateAge(int ageRangeCode);

        void faceAgeRange();

        void loadGroup();

        void delete();
    }
}
