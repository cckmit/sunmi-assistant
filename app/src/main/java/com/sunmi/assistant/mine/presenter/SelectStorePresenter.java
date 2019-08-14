package com.sunmi.assistant.mine.presenter;

import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.mine.contract.SelectStoreContract;
import com.sunmi.assistant.mine.model.SelectShopModel;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.AuthStoreInfo;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;


/**
 * 授权门店完成
 *
 * @author YangShiJie
 * @date 2019/6/26
 */
public class SelectStorePresenter extends BasePresenter<SelectStoreContract.View>
        implements SelectStoreContract.Presenter {

    private static final String TAG = SelectStorePresenter.class.getSimpleName();

    private List<SelectShopModel> mList;
    private int mSelectCount = 0;
    private int mCompleteCount = 0;
    private boolean hasSuccess = false;

    public SelectStorePresenter(ArrayList<AuthStoreInfo.SaasUserInfoListBean> list) {
        mList = new ArrayList<>(list.size());
        for (AuthStoreInfo.SaasUserInfoListBean bean : list) {
            mList.add(new SelectShopModel(bean));
        }
        if (list.size() == 1) {
            SelectShopModel item = mList.get(0);
            item.setChecked(true);
        }
    }

    @Override
    public List<SelectShopModel> getList() {
        return mList;
    }

    @Override
    public void createShops(List<SelectShopModel> list) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        mSelectCount = 0;
        mCompleteCount = 0;
        List<SelectShopModel> selected = new ArrayList<>();
        for (SelectShopModel item : list) {
            if (item.isChecked()) {
                selected.add(item);
            }
        }
        mSelectCount = selected.size();
        for (int i = 0; i < mSelectCount; i++) {
            SelectShopModel item = selected.get(i);
            item.updateName(i + 1);
            createShop(item);
        }
    }

    private void createShop(SelectShopModel item) {
        SunmiStoreApi.createShop(SpUtils.getCompanyId(), item.getShopName(), "", "", new RetrofitCallback<CreateShopInfo>() {
            @Override
            public void onSuccess(int code, String msg, CreateShopInfo data) {
                item.setShopId(data.getShop_id());
                if (SpUtils.getShopId() < 0) {
                    CommonUtils.saveSelectShop(item.getShopId(), item.getShopName());
                }
                hasSuccess = true;
                authorizeSaas(item);
            }

            @Override
            public void onFail(int code, String msg, CreateShopInfo data) {
                LogCat.e(TAG, "Create shop Failed. " + msg);
                if (isViewAttached()) {
                    mView.onFailedShopCreate(code);
                }
                createShopAdd();
            }
        });
    }

    private void authorizeSaas(SelectShopModel item) {
        SunmiStoreApi.authorizeSaas(SpUtils.getCompanyId(), item.getShopId(),
                item.getSaasSource(), item.getShopNo(), item.getSaasName(), new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        SpUtils.setSaasExist(1);
                        createShopAdd();
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Authorize shop Failed. " + msg);
                        createShopAdd();
                    }
                });
    }

    private void createShopAdd() {
        mCompleteCount++;
        if (mCompleteCount >= mSelectCount && isViewAttached()) {
            mView.hideLoadingDialog();
            if (hasSuccess) {
                mView.complete();
            }
        }
    }

}
