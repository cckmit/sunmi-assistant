package com.sunmi.assistant.mine.presenter;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopContactAreaContract;
import com.sunmi.assistant.mine.shop.ShopDetailActivity;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.ShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * @author yangShiJie
 * @date 2019/8/16
 */
public class ShopContactAreaPresenter extends BasePresenter<ShopContactAreaContract.View>
        implements ShopContactAreaContract.Presenter {
    private static final String TAG = ShopContactAreaPresenter.class.getSimpleName();

    @Override
    public void editShopMessage(final int type, ShopInfo shopInfo) {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().updateShopInfo(shopInfo, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    if (type == ShopDetailActivity.SHOP_CHANGE_NAME) {
                        mView.shopNameChanged();
                    } else if (type == ShopDetailActivity.SHOP_CHANGE_CONTACT) {
                        mView.contactView();
                    } else if (type == ShopDetailActivity.SHOP_CHANGE_CONTACT_TEL) {
                        mView.contactTelView();
                    } else if (type == ShopDetailActivity.SHOP_CHANGE_AREA) {
                        mView.areaView();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.shortTip(R.string.tip_save_fail);
                }
            }
        });
    }

}
