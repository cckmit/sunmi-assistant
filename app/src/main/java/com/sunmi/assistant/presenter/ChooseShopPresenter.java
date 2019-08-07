package com.sunmi.assistant.presenter;

import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.contract.ChooseShopContract;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CompanyListResp;
import sunmi.common.model.ShopListResp;
import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;

/**
 * Description: ChooseShopPresenter
 * Created by Bruce on 2019/7/1.
 */
public class ChooseShopPresenter extends BasePresenter<ChooseShopContract.View>
        implements ChooseShopContract.Presenter {
    private static final String TAG = ChooseShopPresenter.class.getSimpleName();

    @Override
    public void getShopList(int companyId) {
        if (isViewAttached()) {
            mView.showLoadingDialog();
        }
        SunmiStoreRemote.get().getShopList(companyId, new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    List<ShopListResp.ShopInfo> shopList = data.getShop_list();
                    if (shopList != null) {
                        mView.getShopListSuccess(shopList);
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getShopListFail(code, msg, data);
                }
            }
        });
    }

    @Override
    public void getCompanyList() {
        CloudCall.getCompanyList(new RetrofitCallback<CompanyListResp>() {
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

    @Override
    public void getUserInfo() {
        mView.showLoadingDialog();
        SunmiStoreApi.getUserInfo(new RetrofitCallback<UserInfoBean>() {
            @Override
            public void onSuccess(int code, String msg, UserInfoBean data) {
                CommonUtils.saveLoginInfo(data);
                getCompanyList();
            }

            @Override
            public void onFail(int code, String msg, UserInfoBean data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                }
            }
        });
    }

    @Override
    public void getSaas(String mobile) {
        mView.showLoadingDialog();
        CloudCall.getSaasUserInfo(mobile, new RetrofitCallback<AuthStoreInfo>() {
            @Override
            public void onSuccess(int code, String msg, AuthStoreInfo bean) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSaasSuccessView(bean);
                }
            }

            @Override
            public void onFail(int code, String msg, AuthStoreInfo data) {
                LogCat.e(TAG, "getSaas  Failed code=" + code + "; msg=" + msg);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.getSaasFailView(code, msg);
                }
            }
        });
    }

    @Override
    public void getSsoToken() {
        SunmiStoreApi.getSsoToken(new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                if (isViewAttached()) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        String ssoToken = jsonObject.getString("sso_token");
                        LogCat.e(TAG, "sso_token:" + ssoToken);
                        SpUtils.setSsoToken(ssoToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {

            }
        });
    }
}
