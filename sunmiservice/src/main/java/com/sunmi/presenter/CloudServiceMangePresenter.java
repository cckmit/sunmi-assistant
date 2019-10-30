package com.sunmi.presenter;

import com.sunmi.bean.IpcListResp;
import com.sunmi.bean.SubscriptionListBean;
import com.sunmi.contract.CloudServiceMangeContract;
import com.sunmi.rpc.ServiceApi;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DBUtils;
import sunmi.common.utils.ThreadPool;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class CloudServiceMangePresenter extends BasePresenter<CloudServiceMangeContract.View>
        implements CloudServiceMangeContract.Presenter {
    @Override
    public void getSubscriptionList(int pageNum, int pageSize) {
        ServiceApi.getInstance().getSubscriptionList(pageNum, pageSize, new RetrofitCallback<SubscriptionListBean>() {
            @Override
            public void onSuccess(int code, String msg, SubscriptionListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSubscriptionListSuccess(data.getServiceList(), data.getTotalCount());
                }
            }

            @Override
            public void onFail(int code, String msg, SubscriptionListBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSubscriptionListFail(code, msg);
                }
            }
        });
    }

    @Override
    public void getIpcDetailList() {
        ServiceApi.getInstance().getDetailList(new RetrofitCallback<IpcListResp>() {
            @Override
            public void onSuccess(int code, String msg, final IpcListResp data) {
                ThreadPool.getCachedThreadPool().submit(new Runnable() {
                    @Override
                    public void run() {
                        DBUtils.deleteSunmiDeviceByType("IPC");

                        if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getSs_list()) {
                                getIpcDevice(bean);
                            }
                        }
                        if (isViewAttached()) {
                            mView.getIpcDetailListSuccess();
                        }
                    }
                });
            }

            @Override
            public void onFail(int code, String msg, IpcListResp data) {

            }
        });
    }


    private void getIpcDevice(IpcListResp.SsListBean bean) {
        SunmiDevice device = new SunmiDevice();
        device.setType("IPC");
        device.setStatus(bean.getActive_status());
        device.setDeviceid(bean.getSn());
        device.setModel(bean.getModel());
        device.setName(bean.getDevice_name());
        device.setIp(bean.getCdn_address());
        device.setUid(bean.getUid());
        device.setShopId(bean.getShop_id());
        device.setId(bean.getId());
        device.setFirmware(bean.getBin_version());
        saveDevice(device);
    }

    private void saveDevice(SunmiDevice device) {
        device.saveOrUpdate("deviceid=?", device.getDeviceid());
    }
}
