package com.sunmi.assistant.mine.presenter;

import com.google.gson.Gson;
import com.sunmi.assistant.mine.contract.MainContract;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.rpc.MessageCenterApi;
import com.sunmi.assistant.utils.PushUtils;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.enums.DeviceType;
import sunmi.common.model.ServiceListResp;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DBUtils;
import sunmi.common.utils.FileHelper;
import sunmi.common.utils.FileUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
public class MainPresenter extends BasePresenter<MainContract.View>
        implements MainContract.Presenter {

    @Override
    public void getMessageCount() {
        MessageCenterApi.getInstance().getMessageCount(new RetrofitCallback<MessageCountBean>() {
            @Override
            public void onSuccess(int code, String msg, MessageCountBean data) {
                PushUtils.resetUnReadCount(data);
                FileUtils.writeFileToSD(FileHelper.FILE_PATH, "msgCount.json", new Gson().toJson(data));
                if (isViewAttached()) {
                    mView.getMessageCountSuccess(data);
                }
            }

            @Override
            public void onFail(int code, String msg, MessageCountBean data) {
                if (isViewAttached()) {
                    mView.getMessageCountFail(code, msg);
                }
            }
        });
    }

    public void syncIpcDevice() {
        IpcCloudApi.getInstance().getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        DBUtils.deleteSunmiDeviceByType(DeviceType.IPC);
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

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {

                    }
                });
    }

    @Override
    public void getServiceList() {
        SunmiStoreApi.getInstance().getServiceList(new RetrofitCallback<ServiceListResp>() {
            @Override
            public void onSuccess(int code, String msg, ServiceListResp data) {
                boolean status = false;
                List<ServiceListResp.ServiceListBean> beans = data.getServiceList();
                if (beans!=null && beans.size()>0){
                    for (ServiceListResp.ServiceListBean bean : beans) {
                        if (bean.getServiceType()== CommonConstants.SERVICE_TYPE_LOAN
                        &&bean.getActiveStatus() ==CommonConstants.SERVICE_STATUS_ABLE){
                            status = true;
                            break;
                        }
                    }
                }
                SpUtils.setLoanStatus(status);
                if (isViewAttached()){
                    mView.getLoanStatus(status);
                }
            }

            @Override
            public void onFail(int code, String msg, ServiceListResp data) {
                if (isViewAttached()){
                    mView.getLoanStatus(false);
                }
            }
        });
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
