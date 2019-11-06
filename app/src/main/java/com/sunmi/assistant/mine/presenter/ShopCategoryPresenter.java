package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.mine.contract.ShopCategoryContract;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.log.LogCat;

/**
 * @author yinhui
 * @date 2019-08-08
 */
public class ShopCategoryPresenter extends BasePresenter<ShopCategoryContract.View>
        implements ShopCategoryContract.Presenter {

    private static final String TAG = ShopCategoryPresenter.class.getSimpleName();

    private final ShopInfo mInfo;

    public ShopCategoryPresenter(ShopInfo info) {
        this.mInfo = info;
    }

    @Override
    public void getCategory() {
        SunmiStoreApi.getInstance().getShopCategory(new RetrofitCallback<ShopCategoryResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopCategoryResp data) {
                if (isViewAttached()) {
                    mView.showCategoryList(data.getShopTypeList());
                }
            }

            @Override
            public void onFail(int code, String msg, ShopCategoryResp data) {
                LogCat.e(TAG, "Get category Failed. " + msg);
                if (isViewAttached()) {
                    mView.getCategoryFailed();
                }
            }
        });
    }

    @Override
    public void updateCategory(int type1, int type2) {
        mInfo.setTypeOne(type1);
        mInfo.setTypeTwo(type2);
        SunmiStoreApi.getInstance().updateShopInfo(mInfo, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.complete();
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.updateCategoryFailed();
                }
            }
        });
    }

}
