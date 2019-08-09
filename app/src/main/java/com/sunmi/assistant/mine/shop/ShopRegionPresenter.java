package com.sunmi.assistant.mine.shop;

import com.sunmi.assistant.mine.model.ShopInfo;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.ShopRegionResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public class ShopRegionPresenter extends BasePresenter<ShopRegionContract.View>
        implements ShopRegionContract.Presenter {

    private static final String TAG = ShopRegionPresenter.class.getSimpleName();

    private ShopInfo mInfo;

    ShopRegionPresenter(ShopInfo info) {
        this.mInfo = info;
    }

    @Override
    public void getRegion() {
        SunmiStoreApi.getShopRegion(new RetrofitCallback<ShopRegionResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopRegionResp data) {
                if (isViewAttached()) {
                    mView.showRegionList(data.getRegionList());
                }
            }

            @Override
            public void onFail(int code, String msg, ShopRegionResp data) {
                LogCat.e(TAG, "Get shop region Failed. " + msg);
                if (isViewAttached()) {
                    mView.getRegionFailed();
                }
            }
        });
    }

    @Override
    public void updateRegion(int province, int city, int area) {
        SunmiStoreApi.updateShopRegion(mInfo.getShopId(), mInfo.getShopName(), province, city, area,
                new RetrofitCallback<Object>() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        if (isViewAttached()) {
                            mView.complete();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        LogCat.e(TAG, "Update shop region Failed. " + msg);
                        if (isViewAttached()) {
                            mView.updateRegionFailed();
                        }
                    }
                });
    }
}
