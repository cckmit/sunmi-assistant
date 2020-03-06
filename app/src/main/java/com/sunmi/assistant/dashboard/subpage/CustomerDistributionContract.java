package com.sunmi.assistant.dashboard.subpage;

import android.util.SparseArray;

import com.sunmi.assistant.dashboard.data.AgeCustomer;
import com.sunmi.assistant.dashboard.data.GenderCustomer;
import com.sunmi.assistant.dashboard.data.NewOldCustomer;
import com.sunmi.ipc.face.model.FaceAge;

import java.util.List;

import sunmi.common.base.BaseView;

public interface CustomerDistributionContract {

    interface View extends BaseView {

        void ageRangeSuccess(SparseArray<FaceAge> ageMap);

        void ageRangeFail(int code, String msg);

        void getCustomerShopAgeSuccess(List<NewOldCustomer> newOldCustomers, List<AgeCustomer> ageCustomers);

        void getCustomerShopAgeFail(int code, String msg);

        void getCustomerShopAgeGenderSuccess(List<GenderCustomer> genderCustomers);

        void getCustomerShopAgeGenderFail(int code, String msg);
    }

    interface Presenter {
        void ageRange();

        void getCustomerShopAgeDistribution();

        void getCustomerShopAgeGenderDistribution();
    }
}
