package com.sunmi.presenter;

import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.bean.SubscriptionListBean;
import com.sunmi.contract.CloudServiceMangeContract;
import com.sunmi.rpc.ServiceApi;
import com.xiaojinzi.component.impl.service.ServiceManager;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.enums.DeviceType;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.IpcCloudApiAnno;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.ThreadPool;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class CloudServiceMangePresenter extends BasePresenter<CloudServiceMangeContract.View>
        implements CloudServiceMangeContract.Presenter {


    private List<SunmiDevice> devices;
    private HashMap<String, String> nameMap;

    public CloudServiceMangePresenter() {
        devices = DataSupport.where("type=?", DeviceType.IPC).find(SunmiDevice.class);
    }

    @Override
    public void getSubscriptionList(int pageNum, int pageSize, int category) {
        ServiceApi.getInstance().getSubscriptionList(pageNum, pageSize, category, new RetrofitCallback<SubscriptionListBean>() {
            @Override
            public void onSuccess(int code, String msg, final SubscriptionListBean data) {
                final List<ServiceDetailBean> beans = data.getServiceList();
                final int total = data.getTotalCount();
                if (total <= 0) {
                    if (isViewAttached()) {
                        mView.hideLoadingDialog();
                        mView.getSubscriptionListSuccess(beans, total);
                    }
                } else {
                    ThreadPool.getCachedThreadPool().submit(new Runnable() {
                        @Override
                        public void run() {
                            if (devices.size() <= 0) {
                                getIpcDetailList(beans, data.getTotalCount());
                            } else {
                                nameMap = new HashMap<>(devices.size());
                                for (SunmiDevice device : devices) {
                                    nameMap.put(device.getDeviceid(), device.getName());
                                }
                                setName(beans, nameMap, total);
                            }
                        }
                    });
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

    private void getIpcDetailList(final List<ServiceDetailBean> beans, final int total) {
        final IpcCloudApiAnno ipcCloudApi = ServiceManager.get(IpcCloudApiAnno.class);
        if (ipcCloudApi != null) {
            ipcCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                    new RetrofitCallback<IpcListResp>() {
                        @Override
                        public void onSuccess(int code, String msg, final IpcListResp data) {
                            final List<IpcListResp.SsListBean> list = new ArrayList<>();
                            if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                                list.addAll(data.getSs_list());
                            }
                            if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                                list.addAll(data.getFs_list());
                            }
                            if (list.size() > 0) {
                                nameMap = new HashMap<>(list.size());
                                for (IpcListResp.SsListBean bean : list) {
                                    nameMap.put(bean.getSn(), bean.getDevice_name());
                                }
                                setName(beans, nameMap, total);
                            } else {
                                setName(beans, null, total);
                            }

                            ThreadPool.getCachedThreadPool().submit(new Runnable() {
                                @Override
                                public void run() {
                                    if (list.size() > 0) {
                                        for (IpcListResp.SsListBean bean : list) {
                                            getIpcDevice(bean);
                                        }
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFail(int code, String msg, IpcListResp data) {
                            if (isViewAttached()) {
                                mView.hideLoadingDialog();
                                mView.getSubscriptionListFail(code, msg);
                            }
                        }
                    });
        }
    }

    private void setName(List<ServiceDetailBean> beans, HashMap<String, String> map, int total) {
        if (map != null) {
            for (int i = 0; i < beans.size(); i++) {
                String name = map.get(beans.get(i).getDeviceSn());
                if (name != null) {
                    beans.get(i).setBind(true);
                    beans.get(i).setDeviceName(name);
                } else {
                    beans.get(i).setBind(false);
                }
            }
        } else {
            for (int i = 0; i < beans.size(); i++) {
                beans.get(i).setBind(false);
            }
        }
        if (isViewAttached()) {
            mView.hideLoadingDialog();
            mView.getSubscriptionListSuccess(beans, total);
        }
    }

    private void getIpcDevice(IpcListResp.SsListBean bean) {
        SunmiDevice device = new SunmiDevice();
        device.setType(DeviceType.IPC);
        device.setStatus(bean.getActive_status());
        device.setDeviceid(bean.getSn());
        device.setModel(bean.getModel());
        device.setName(bean.getDevice_name());
        device.setImgPath(bean.getCdn_address());
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
