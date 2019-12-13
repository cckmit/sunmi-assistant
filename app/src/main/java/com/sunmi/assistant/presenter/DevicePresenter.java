package com.sunmi.assistant.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.sunmi.apmanager.receiver.MyNetworkCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.assistant.R;
import com.sunmi.assistant.contract.DeviceContract;
import com.sunmi.assistant.pos.data.PosApi;
import com.sunmi.assistant.pos.response.PosListResp;
import com.sunmi.cloudprinter.rpc.IOTCloudApi;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseApplication;
import sunmi.common.base.BasePresenter;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.enums.DeviceStatus;
import sunmi.common.model.AdListBean;
import sunmi.common.model.AdListResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.model.SunmiDevice;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.rpc.http.RpcCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DBUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.ThreadPool;

/**
 * Description:
 * Created by bruce on 2019/6/27.
 */
public class DevicePresenter extends BasePresenter<DeviceContract.View>
        implements DeviceContract.Presenter {

    @Override
    public void getBannerList() {
        SunmiStoreApi.getInstance().getAdList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<AdListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, AdListResp data) {
                        if (isViewAttached()) {
                            mView.endRefresh();
                            mView.getAdListSuccess(data);
                        }
                        try {
                            DataSupport.deleteAll(AdListBean.class);
                            DataSupport.saveAll(data.getAd_list());
                        } catch (Exception e) {
                            DataSupport.saveAll(data.getAd_list());
                            e.printStackTrace();
                        }
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
                ThreadPool.getCachedThreadPool().submit(() -> {
                    DBUtils.deleteSunmiDeviceByType("ROUTER");
                    List<SunmiDevice> list = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(getRouterDevice((JSONObject) jsonArray.opt(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (isViewAttached()) {
                        mView.endRefresh();
                        mView.getRouterListSuccess(list);
                    }
                });
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
    public void unbind(SunmiDevice device) {
        switch (device.getType()) {
            case "ROUTER":
                unbindRouter(device.getDeviceid());
                break;
            case "IPC":
                unbindIPC(device.getId());
                break;
            case "PRINTER":
                unbindPrinter(device.getDeviceid());
                break;
        }
    }

    private void unbindRouter(String sn) {
        CloudApi.unbind(sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.shortTip(R.string.str_delete_success);
                    DBUtils.deleteSunmiDevice(sn);
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
        IpcCloudApi.getInstance().getDetailList(SpUtils.getCompanyId(), SpUtils.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        ThreadPool.getCachedThreadPool().submit(() -> {
                            DBUtils.deleteSunmiDeviceByType("IPC");
                            List<SunmiDevice> list = new ArrayList<>();
                            if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                                for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                    SunmiDevice device = getIpcDevice(bean);
                                    list.add(device);
                                }
                            }
                            if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                                for (IpcListResp.SsListBean bean : data.getSs_list()) {
                                    SunmiDevice device = getIpcDevice(bean);
                                    list.add(device);
                                }
                            }
                            if (isViewAttached()) {
                                mView.endRefresh();
                                mView.getIpcListSuccess(list);
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {
                        if (isViewAttached()) {
                            mView.endRefresh();
                        }
                    }
                });
    }

    private void unbindIPC(int deviceId) {
        IpcCloudApi.getInstance().unbindIpc(SpUtils.getCompanyId(), SpUtils.getShopId(), deviceId,
                new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.str_delete_success);
                            mView.unbindIpcSuccess(code, msg, data);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.shortTip(R.string.str_delete_fail);
                        }
                    }
                });
    }

    @Override
    public void getPrinterList() {
        if (!CommonConfig.SUPPORT_PRINTER) {
            return;
        }
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
    public void getPosList() {
        PosApi.getInstance().getPosList(new RetrofitCallback<PosListResp>() {
            @Override
            public void onSuccess(int code, String msg, PosListResp data) {
                ThreadPool.getCachedThreadPool().submit(() -> {
                    DBUtils.deleteSunmiDeviceByType("POS");
                    List<SunmiDevice> posList = new ArrayList<>();
                    if (data.getDeviceList().size() > 0) {
                        for (PosListResp.DeviceListBean bean : data.getDeviceList()) {
                            posList.add(getPosDevice(bean));
                        }
                    }
                    if (isViewAttached()) {
                        mView.endRefresh();
                        mView.getPosListSuccess(posList);
                    }
                });
            }

            @Override
            public void onFail(int code, String msg, PosListResp data) {
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
                    getPrinterDevice(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void unbindPrinter(final String sn) {
        IOTCloudApi.unbindPrinter(SpUtils.getShopId(), sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.unbindPrinterSuccess(sn);
                }
            }
        });
    }

    @Override
    public void getShopList() {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().getShopList(SpUtils.getCompanyId(), new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    List<ShopInfo> shopList = data.getShop_list();
                    if (shopList == null) {
                        return;
                    }
                    List<ShopInfo> newShopList = new ArrayList<>();
                    for (ShopInfo shop : shopList) {
                        if (shop.getShopId() == SpUtils.getShopId()) {
                            newShopList.add(0, shop);
                        } else {
                            newShopList.add(shop);
                        }
                    }
                    mView.getShopListSuccess(newShopList);
                }
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.str_server_exception);
                }
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
                    list.add(getPrinterDevice(object));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @NonNull
    private SunmiDevice getRouterDevice(JSONObject object) throws JSONException {
        SunmiDevice device = new SunmiDevice();
        device.setType("ROUTER");
        if (object.has("sn")) {
            device.setDeviceid(object.getString("sn"));
        }
        if (object.has("active_status")) {
            device.setStatus(object.getInt("active_status"));
        }
        if (TextUtils.equals(device.getDeviceid(), MyNetworkCallback.CURRENT_ROUTER)) {
            if (device.getStatus() == DeviceStatus.OFFLINE.ordinal()) {
                device.setStatus(DeviceStatus.EXCEPTION.ordinal());
            }
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
        saveDevice(device);
        return device;
    }

    @NonNull
    private SunmiDevice getIpcDevice(IpcListResp.SsListBean bean) {
        SunmiDevice device = new SunmiDevice();
        device.setType("IPC");
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
        return device;
    }

    @NonNull
    private SunmiDevice getPrinterDevice(JSONObject object) throws JSONException {
        SunmiDevice device = new SunmiDevice();
        device.setType("PRINTER");
        device.setName(BaseApplication.getContext().getString(R.string.str_cloud_printer));
        device.setModel("NT211");
        if (object.has("msn")) {
            device.setDeviceid(object.getString("msn"));
        }
        if (object.has("isOnline")) {
            device.setStatus(object.getInt("isOnline"));
        }
        if (object.has("merchantId")) {
            device.setShopId(object.getInt("merchantId"));
        }
        if (object.has("channelId")) {
            device.setChannelId(object.getInt("channelId"));
        } else {
            device.setChannelId(1);
        }
        saveDevice(device);
        return device;
    }

    @NonNull
    private SunmiDevice getPosDevice(PosListResp.DeviceListBean bean) {
        SunmiDevice device = new SunmiDevice();
        device.setType("POS");
        device.setDeviceid(bean.getSn());
        device.setModel(bean.getModel());
        device.setDisplayModel(bean.getDisplayModel());
        device.setPosModelDetails(bean.getModelDetail());
        device.setChannelId(bean.getChannelId());
        device.setStatus(bean.getActiveStatus());
        device.setImgPath(bean.getImgPath());
        device.setShopId(SpUtils.getShopId());
        saveDevice(device);
        return device;
    }

    private void saveDevice(SunmiDevice device) {
        device.saveOrUpdate("deviceid=?", device.getDeviceid());
    }
}
