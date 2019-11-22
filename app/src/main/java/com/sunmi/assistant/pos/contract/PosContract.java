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

//        void getPosDetailsFail(int code, String msg);

        void getPosGuaranteeSuccess(PosWarrantyResp resp);

//        void getPosGuaranteeFail(int code, String msg);
    }

    interface Presenter {

        void getPosDetails();

        void getPosGuarantee();

    }

}
