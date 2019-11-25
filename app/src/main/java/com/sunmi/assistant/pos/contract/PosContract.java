package com.sunmi.assistant.pos.contract;

import com.sunmi.assistant.pos.response.PosDetailsResp;
import com.sunmi.assistant.pos.response.PosWarrantyResp;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public interface PosContract {

    interface View extends BaseView {

        void getPosDetailsSuccess(PosDetailsResp resp);

        void getPosGuaranteeSuccess(PosWarrantyResp resp);

        void getPosTypeSuccess(boolean isDesktop);
    }

    interface Presenter {

        void getPosDetails();

        void getPosGuarantee();

        void getPosType(String model);
    }

}
