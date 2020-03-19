package com.sunmi.assistant.presenter;

import android.util.Pair;
import android.util.SparseArray;

import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.data.AppModel;
import com.sunmi.assistant.data.AppModelImpl;
import com.sunmi.assistant.data.Callback;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;

/**
 * Description: ChooseShopPresenter
 * Created by Bruce on 2019/7/1.
 */
public class ChooseShopPresenter extends BasePresenter<ChooseShopContract.View>
        implements ChooseShopContract.Presenter {

    private AppModel model = AppModelImpl.get();

    @Override
    public void getShopList(int companyId) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        model.getShopListWithAuth(companyId, true, new Callback<Pair<Integer, SparseArray<ShopInfo>>>() {
            @Override
            public void onLoaded(Pair<Integer, SparseArray<ShopInfo>> result) {
                if (isViewAttached()) {
                    SparseArray<ShopInfo> shopMap = result.second;
                    int size = shopMap.size();
                    List<ShopInfo> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(shopMap.valueAt(i));
                    }
                    mView.hideLoadingDialog();
                    mView.getShopListSuccess(result.first, list);
                }
            }

            @Override
            public void onFail() {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getShopListFail();
                }
            }
        });
    }

    @Override
    public void getCompanyList() {
        mView.showLoadingDialog();
        SunmiStoreApi.getInstance().getCompanyList(new RetrofitCallback<CompanyListResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getCompanyListSuccess(data.getCompany_list());
                }
            }

            @Override
            public void onFail(int code, String msg, CompanyListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getCompanyListFail(code, msg, data);
                }
            }
        });
    }

//    @Override
//    public void getUserInfo() {
//        mView.showLoadingDialog();
//        SunmiStoreApi.getUserInfo(new RetrofitCallback<UserInfoBean>() {
//            @Override
//            public void onSuccess(int code, String msg, UserInfoBean data) {
//                CommonUtils.saveLoginInfo(data);
//                getSsoToken();
//            }
//
//            @Override
//            public void onFail(int code, String msg, UserInfoBean data) {
//                if (isViewAttached()) {
//                    mView.hideLoadingDialog();
//                }
//            }
//        });
//    }
//
//    @Override
//    public void getSsoToken() {
//        SunmiStoreApi.getSsoToken(new RetrofitCallback<Object>() {
//            @Override
//            public void onSuccess(int code, String msg, Object data) {
//                if (isViewAttached()) {
//                    try {
//                        JSONObject jsonObject = new JSONObject(data.toString());
//                        String ssoToken = jsonObject.getString("sso_token");
//                        LogCat.e(TAG, "sso_token:" + ssoToken);
//                        SpUtils.setSsoToken(ssoToken);
//                        mView.getUserInfoSuccessView();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFail(int code, String msg, Object data) {
//
//            }
//        });
//    }

}
