package com.sunmi.assistant.presenter;

import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.ShopListResp;

import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;

/**
 * Description: ChooseShopPresenter
 * Created by Bruce on 2019/7/1.
 */
public class ChooseShopPresenter extends BasePresenter<ChooseShopContract.View>
        implements ChooseShopContract.Presenter {
    @Override
    public void getShopList() {
        SunmiStoreRemote.get().getShopList(SpUtils.getCompanyId(), new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                List<ShopListResp.ShopInfo> shopList = data.getShop_list();
                if (shopList != null && shopList.size() > 0) {
                    if (isViewAttached()) {
                        mView.getShopListSuccess(shopList);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
            }
        });
    }
}
