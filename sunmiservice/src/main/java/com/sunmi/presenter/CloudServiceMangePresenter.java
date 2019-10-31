package com.sunmi.presenter;

import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.bean.SubscriptionListBean;
import com.sunmi.contract.CloudServiceMangeContract;
import com.sunmi.rpc.ServiceApi;
import com.xiaojinzi.component.impl.service.ServiceManager;

import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.IpcCloudApiAnno;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DBUtils;
import sunmi.common.utils.SpUtils;
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
            public void onSuccess(int code, String msg, final SubscriptionListBean data) {
                final List<ServiceDetailBean> beans = data.getServiceList();
                ThreadPool.getCachedThreadPool().submit(new Runnable() {
                    @Override
                    public void run() {
                        List<SunmiDevice> devices = DataSupport.where("type=?", "IPC").find(SunmiDevice.class);
                        if (devices.size() <= 0) {
                            getIpcDetailList(beans);
                        } else {
                            for (int i = 0; i < beans.size(); i++) {
                                SunmiDevice device = DataSupport.where("deviceid=?",
                                        beans.get(i).getDeviceSn()).findFirst(SunmiDevice.class);
                                if (device != null) {
                                    beans.get(i).setBind(true);
                                    beans.get(i).setDeviceName(device.getName());
                                } else {
                                    beans.get(i).setBind(false);
                                }
                            }
                            if (isViewAttached()) {
                                mView.hideLoadingDialog();
                                mView.getSubscriptionListSuccess(beans, data.getTotalCount());
                            }
                        }
                    }
                });

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


    private void getIpcDetailList(List<ServiceDetailBean> beans) {
        HashMap<String, ServiceDetailBean> beanHashMap = new HashMap<>(beans.size());
        for (ServiceDetailBean bean : beans) {
            beanHashMap.put(bean.getDeviceSn(),bean);
        }
        IpcCloudApiAnno ipcCloudApi = ServiceManager.get(IpcCloudApiAnno.class);
        if (ipcCloudApi != null) {
            ipcCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                    new RetrofitCallback<IpcListResp>() {
                        @Override
                        public void onSuccess(int code, String msg, final IpcListResp data) {
                            ThreadPool.getCachedThreadPool().submit(new Runnable() {
                                @Override
                                public void run() {
                                    DBUtils.deleteSunmiDeviceByType("IPC");
                                    if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                                        for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                            getIpcDevice(bean);
                                        }
                                    }
                                    if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                                        for (IpcListResp.SsListBean bean : data.getSs_list()) {
                                            getIpcDevice(bean);
                                        }
                                    }

                                }
                            });
                        }

                        @Override
                        public void onFail(int code, String msg, IpcListResp data) {

                        }
                    });
        }
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
