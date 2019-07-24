package com.sunmi.assistant.presenter;

import android.support.annotation.NonNull;

import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.utils.DBUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.DeviceContract;
import com.sunmi.assistant.data.response.AdListBean;
import com.sunmi.assistant.data.response.AdListResp;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.cloudprinter.rpc.IOTCloudApi;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.rpc.IPCCloudApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConfig;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.rpc.http.RpcCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public class DevicePresenter extends BasePresenter<DeviceContract.View>
        implements DeviceContract.Presenter {

    @Override
    public void getBannerList() {
        CloudCall.getAdList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<AdListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, AdListResp data) {
                        if (isViewAttached()) {
                            mView.endRefresh();
                            mView.getAdListSuccess(data);
                        }
                        DataSupport.deleteAll(AdListBean.class);
                        DataSupport.saveAll(data.getAd_list());
                    }

                    @Override
                    public void onFail(int code, String msg, AdListResp data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.shortTip(R.string.toast_network_Exception);
                            mView.endRefresh();
                        }
                    }
                });
    }

    @Override
    public void getRouterList() {
        CloudApi.getBindDeviceList(SpUtils.getShopId(), new RpcCallback(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                DBUtils.deleteSunmiDeviceByType("ROUTER");
                List<SunmiDevice> list = new ArrayList<>();
                try {
                    if (code == 1) {
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = (JSONObject) jsonArray.opt(i);
                            SunmiDevice device = new SunmiDevice();
                            device.setType("ROUTER");
                            if (object.has("active_status")) {
                                device.setStatus(object.getInt("active_status"));
                            }
                            if (object.has("sn")) {
                                device.setDeviceid(object.getString("sn"));
                            }
                            if (object.has("model")) {
                                device.setModel(object.getString("model"));
                                device.setName(object.getString("model"));
                            }
                            if (object.has("shop_id")) {
                                device.setShopId(object.getInt("shop_id"));
                            } else {
                                device.setShopId(SpUtils.getShopId());
                            }
                            device.saveOrUpdate();
                            list.add(device);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isViewAttached()) {
                    mView.endRefresh();
                    mView.getRouterListSuccess(list);
                }
            }

            @Override
            public void onError(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.endRefresh();
                }
            }
        });
    }

    @Override
    public void unbindRouter(String sn) {
        CloudApi.unbind(sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.str_delete_success);
                    DBUtils.deleteUnBindDevLocal(sn);
                    getRouterList();
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.str_delete_fail);
                }
            }
        });
    }

    @Override
    public void getIpcList() {
        IPCCloudApi.getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        DBUtils.deleteSunmiDeviceByType("IPC");
                        List<SunmiDevice> list = new ArrayList<>();
                        if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                SunmiDevice device = getSunmiDevice(bean);
                                device.saveOrUpdate();
                                list.add(device);
                            }
                        }
                        if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getSs_list()) {
                                SunmiDevice device = getSunmiDevice(bean);
                                device.saveOrUpdate();
                                list.add(device);
                            }
                        }
                        if (isViewAttached()) {
                            mView.endRefresh();
                            mView.getIpcListSuccess(list);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {
                        mView.endRefresh();
                    }
                });
    }

    @Override
    public void unbindIPC(int deviceId) {
        IPCCloudApi.unbindIPC(SpUtils.getCompanyId(), SpUtils.getShopId(), deviceId,
                new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.tip_unbind_success);
                            mView.unbindIpcSuccess(code, msg, data);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.tip_unbind_fail);
                        }
                    }
                });
    }

    @Override
    public void getPrinterList() {
        if (!CommonConfig.SUPPORT_PRINTER) return;
        IOTCloudApi.getPrinterList(SpUtils.getShopId(), new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.endRefresh();
                    DBUtils.deleteSunmiDeviceByType("PRINTER");
                    mView.getPrinterListSuccess(getList(data));
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.endRefresh();
                }
            }
        });
    }

    @Override
    public void getPrinterStatus(final String sn) {
        IOTCloudApi.getPrinterStatus(sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                try {
                    SunmiDevice device = getStoreBean(new JSONObject(data));
                    if (isViewAttached()) {
                        mView.getPrinterStatusSuccess(device);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void unBindPrinter(final String sn) {
        IOTCloudApi.unbindPrinter(SpUtils.getShopId(), sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) mView.unbindPrinterSuccess(sn);
            }
        });
    }

    private List<SunmiDevice> getList(String data) {
        List<SunmiDevice> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("device")) {
                JSONArray array = jsonObject.getJSONArray("device");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    list.add(getStoreBean(object));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @NonNull
    private SunmiDevice getSunmiDevice(IpcListResp.SsListBean bean) {
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
        return device;
    }

    @NonNull
    private SunmiDevice getStoreBean(JSONObject object) throws JSONException {
        SunmiDevice bean = new SunmiDevice();
        bean.setType("PRINTER");
        bean.setName(BaseApplication.getContext().getString(R.string.str_cloud_printer));
        bean.setModel("NT211");
        if (object.has("msn")) {
            bean.setDeviceid(object.getString("msn"));
        }
        if (object.has("isOnline")) {
            bean.setStatus(object.getInt("isOnline"));
        }
        if (object.has("merchantId")) {
            bean.setShopId(object.getInt("merchantId"));
        }
        if (object.has("channelId")) {
            bean.setChannelId(object.getInt("channelId"));
        } else {
            bean.setChannelId(1);
        }
        bean.saveOrUpdate();
        return bean;
    }

}
