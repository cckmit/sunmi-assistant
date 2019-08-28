package com.sunmi.ipc.face.contract;

import com.sunmi.ipc.face.model.FaceGroup;
import com.sunmi.ipc.model.FaceAgeRangeResp;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
public interface FacePhotoContract {
    interface View extends BaseView {

        void updateNameSuccessView(String name);

        void updateIdentitySuccessView();

        void updateGenderSuccessView(int gender);

        void updateAgeSuccessView(int ageRangeCode);

        void faceAgeRangeSuccessView(FaceAgeRangeResp data);

        void loadGroupSuccessView(List<FaceGroup> list);
    }

    interface Presenter {

        void updateName(int faceId, int sourceGroupId, String name);

        void updateIdentity(int faceId, int sourceGroupId, int targetGroupId);

        void updateGender(int faceId, int sourceGroupId, int gender);

        void updateAge(int faceId, int sourceGroupId, int ageRangeCode);

        void faceAgeRange();

        void loadGroup();
    }
}
