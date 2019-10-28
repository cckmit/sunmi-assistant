package com.sunmi.ipc.face.contract;

import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.face.model.FaceArrivalLogResp;
import com.sunmi.ipc.face.model.FaceGroup;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-11.
 */
public interface FaceHistoryArrivalContract {

    interface View extends BaseView {
        void getArrivalListSuccess(List<FaceArrivalLogResp.HistoryListBean> historyList, int totalCount);

        void getFaceGroupSuccess(List<FaceGroup> groupList);

        void getFaceAgeRangeSuccess(List<FaceAge> ageList);

        void getDateFail(int code,String msg);
    }

    interface Presenter {
        void getArrivalListByTimeRange(int companyId, int shopId, String startTime, String endTime, int groupId,
                                       int deviceId, List<Integer> ageRange, int gender, int pageNum, int pageSize);

        void getFaceGroup(int companyId, int shopId);

        void getFaceAgeRange(int companyId, int shopId);
    }
}
