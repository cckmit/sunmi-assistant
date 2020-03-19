package com.sunmi.assistant.dashboard.subpage;

import android.annotation.SuppressLint;
import android.util.SparseArray;

import com.sunmi.assistant.dashboard.data.AgeCustomer;
import com.sunmi.assistant.dashboard.data.GenderCustomer;
import com.sunmi.assistant.dashboard.data.NewOldCustomer;
import com.sunmi.assistant.dashboard.util.Utils;
import com.sunmi.assistant.data.AppModel;
import com.sunmi.assistant.data.AppModelImpl;
import com.sunmi.assistant.data.Callback;
import com.sunmi.ipc.face.model.FaceAge;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BasePresenter;
import sunmi.common.model.CountListBean;
import sunmi.common.model.CustomerShopDistributionResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CacheManager;
import sunmi.common.utils.SpUtils;

public class CustomerDistributionPresenter extends BasePresenter<CustomerDistributionContract.View>
        implements CustomerDistributionContract.Presenter {

    private String startTime;
    private int period;
    private AppModel model;

    public CustomerDistributionPresenter(long startTime, int period) {
        this.startTime = Utils.formatTime(Utils.FORMAT_API_DATE, startTime);
        this.period = period;
        model = AppModelImpl.get();
    }

    @Override
    public void ageRange() {
        SparseArray<FaceAge> ageName = CacheManager.get().get(CacheManager.CACHE_AGE_NAME);
        if (ageName != null) {
            if (isViewAttached()) {
                mView.hideLoadingDialog();
                mView.ageRangeSuccess(ageName);
            }
            return;
        }
        IpcCloudApi.getInstance().getFaceAgeRange(new RetrofitCallback<FaceAgeRangeResp>() {
            @Override
            public void onSuccess(int code, String msg, FaceAgeRangeResp data) {
                if (data == null || data.getAgeRangeList() == null) {
                    onFail(code, msg, data);
                    return;
                }
                List<FaceAge> list = data.getAgeRangeList();
                SparseArray<FaceAge> ageMap = new SparseArray<>(list.size());
                int size = 4;
                for (FaceAge age : list) {
                    ageMap.put(age.getCode(), age);
                    size += age.getName().length() * 2 + 8;
                }
                CacheManager.get().put(CacheManager.CACHE_AGE_NAME, ageMap, size);
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.ageRangeSuccess(ageMap);
                }
            }

            @Override
            public void onFail(int code, String msg, FaceAgeRangeResp data) {
                if (isViewAttached()) {
                    mView.ageRangeFail(code, msg);
                }
            }
        });
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void getCustomerShopAgeDistribution() {
        SunmiStoreApi.getInstance().getTotalCustomerNewOldDistributionByShop(SpUtils.getCompanyId(), startTime,
                period, new RetrofitCallback<CustomerShopDistributionResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerShopDistributionResp data) {
                        List<NewOldCustomer> newOldCustomers = new ArrayList<>();
                        List<AgeCustomer> ageCustomers = new ArrayList<>();
                        if (data != null && data.getShopList().size() > 0) {
                            List<CustomerShopDistributionResp.ShopListBean> shopListBeans = data.getShopList();
                            for (CustomerShopDistributionResp.ShopListBean bean : shopListBeans) {
                                int oldCount = 0, newCount = 0;
                                List<CountListBean> countList = bean.getCountList();
                                SparseArray<Integer> ageCount = new SparseArray<>(countList.size());
                                for (CountListBean listBean : countList) {
                                    int regular = listBean.getRegularUniqCount();
                                    int stranger = listBean.getStrangerUniqCount();
                                    oldCount += regular;
                                    newCount += stranger;
                                    ageCount.put(listBean.getAgeRangeCode(), regular + stranger);
                                }
                                newOldCustomers.add(new NewOldCustomer(bean.getShopId(), bean.getShopName(), newCount, oldCount));
                                ageCustomers.add(new AgeCustomer(bean.getShopId(), bean.getShopName(), ageCount));
                            }
                        }
                        if (isViewAttached()) {
                            mView.getCustomerShopAgeSuccess(newOldCustomers, ageCustomers);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerShopDistributionResp data) {
                        if (isViewAttached()) {
                            mView.getCustomerShopAgeFail(code, msg);
                        }
                    }
                });
    }

    @Override
    public void getCustomerShopAgeGenderDistribution() {
        SunmiStoreApi.getInstance().getTotalCustomerGenderDistributionByShop(SpUtils.getCompanyId(), startTime,
                period, new RetrofitCallback<CustomerShopDistributionResp>() {
                    @Override
                    public void onSuccess(int code, String msg, CustomerShopDistributionResp data) {
                        List<GenderCustomer> genderCustomers = new ArrayList<>();
                        if (data != null && data.getShopList().size() > 0) {
                            List<CustomerShopDistributionResp.ShopListBean> shopListBeans = data.getShopList();
                            for (CustomerShopDistributionResp.ShopListBean bean : shopListBeans) {
                                int maleCount = 0, femaleCount = 0;
                                List<CountListBean> countList = bean.getCountList();
                                for (CountListBean listBean : countList) {
                                    if (listBean.getGender() == 1) {
                                        maleCount += listBean.getUniqCount();
                                    } else {
                                        femaleCount += listBean.getUniqCount();
                                    }
                                }
                                genderCustomers.add(new GenderCustomer(bean.getShopId(), bean.getShopName(), maleCount, femaleCount));
                            }
                        }
                        if (isViewAttached()) {
                            mView.getCustomerShopAgeGenderSuccess(genderCustomers);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, CustomerShopDistributionResp data) {
                        if (isViewAttached()) {
                            mView.getCustomerShopAgeGenderFail(code, msg);
                        }
                    }
                });
    }

    @Override
    public void getShopList() {
        model.getShopList(SpUtils.getCompanyId(), false, new Callback<SparseArray<ShopInfo>>() {
            @Override
            public void onLoaded(SparseArray<ShopInfo> result) {
                if (isViewAttached()) {
                    mView.getShopListSuccess(result);
                }
            }

            @Override
            public void onFail() {
                if (isViewAttached()) {
                    mView.getShopListFail();
                }
            }
        });
    }
}
