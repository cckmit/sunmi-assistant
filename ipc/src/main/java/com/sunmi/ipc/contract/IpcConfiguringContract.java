package com.sunmi.ipc.contract;

import sunmi.common.router.model.IpcListResp;

import java.util.List;

import sunmi.common.base.BaseView;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public interface IpcConfiguringContract {

    interface View extends BaseView {

        void ipcBindSuccess(String sn);

        void ipcBindFail(String sn, int code, String msg);

        void getIpcListSuccess(List<IpcListResp.SsListBean> ipcList);

        void getIpcListFail(int code, String msg);
    }

    interface Presenter {
        void ipcBind(String shopId, String sn, String token, float longitude, float latitude);

        void getIpcList(int companyId, String shopId);
    }

}
