package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.mine.contract.ShopCreateContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yangShiJie
 * @date 2019-10-10
 */
public class ShopCreatePresenter extends BasePresenter<ShopCreateContract.View>
        implements ShopCreateContract.Presenter {
    private static final String TAG = ShopCategoryPresenter.class.getSimpleName();

    @Override
    public void getCategory() {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().getShopCategory(new RetrofitCallback<ShopCategoryResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopCategoryResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.showCategoryList(data.getShopTypeList());
                }
            }

            @Override
            public void onFail(int code, String msg, ShopCategoryResp data) {
                LogCat.e(TAG, "Get category Failed. " + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getCategoryFailed();
                }
            }
        });
    }

    @Override
    public void createShop(int companyId, String shopName, int province, int city, int area,
                           String address, int typeOne, int typeTwo,
                           float businessArea, String person, String tel) {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().createShop(companyId, shopName, province, city, area,
                address, typeOne, typeTwo, businessArea, person, tel, new RetrofitCallback<CreateShopInfo>() {
                    @Override
                    public void onSuccess(int code, String msg, CreateShopInfo data) {
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.createShopSuccess(data);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CreateShopInfo data) {
                        LogCat.e(TAG, "Create shop Failed. " + msg);
                        if (isViewAttached()) {
                            mView.hideLoadingDialog();
                            mView.createShopFail(code, msg);
                        }
                    }
                });
    }
}
