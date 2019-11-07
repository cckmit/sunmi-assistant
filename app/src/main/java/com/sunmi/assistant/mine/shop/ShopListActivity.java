package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.platform.SelectPlatformActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.ViewHolder;

/**
 * 我的店铺
 *
 * @author yangshijie
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_my_store)
public class ShopListActivity extends BaseActivity {

    public static final int REQUEST_CODE_SHOP = 100;

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewById(R.id.tv_empty)
    TextView tvEmpty;
    @ViewById(R.id.include_network_error)
    View includeNetworkError;
    private ShopListAdapter mAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ShopListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        getShopList();
    }

    @Click(R.id.btn_refresh)
    void onRefreshClick() {
        getShopList();
    }

    @Click(R.id.btn_shop_create)
    void onAddClick() {
        CommonUtils.trackCommonEvent(context, "addStore",
                "主页_我的_我的店铺_添加店铺", Constants.EVENT_MY_INFO);
        createShop();
    }

    private void createShop() {
        if (CommonHelper.isGooglePlay()) {
            CreateShopActivity_.intent(context)
                    .companyId(SpUtils.getCompanyId())
                    .companyName(SpUtils.getCompanyName())
                    .saasExist(SpUtils.getSaasExist())
                    .isLoginSuccessSwitchCompany(false)
                    .startForResult(REQUEST_CODE_SHOP);
        } else {
            CreateShopNewActivity_.intent(context)
                    .companyId(SpUtils.getCompanyId())
                    .companyName(SpUtils.getCompanyName())
                    .saasExist(SpUtils.getSaasExist())
                    .isLoginSuccessSwitchCompany(false)
                    .startForResult(REQUEST_CODE_SHOP);
        }
    }

    private void importShop() {
        SelectPlatformActivity_.intent(context)
                .isCanBack(true)
                .companyId(SpUtils.getCompanyId())
                .companyName(SpUtils.getCompanyName())
                .saasExist(SpUtils.getSaasExist())
                .isLoginSuccessSwitchCompany(false)
                .startForResult(REQUEST_CODE_SHOP);
    }

    @OnActivityResult(REQUEST_CODE_SHOP)
    void onResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getShopList();
        }
    }

    private void getShopList() {
        showLoadingDialog();
        SunmiStoreApi.getInstance().getShopList(SpUtils.getCompanyId(), new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                hideLoadingDialog();
                includeNetworkError.setVisibility(View.GONE);
                if (data.getShop_list().size() == 0) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    mAdapter.setData(data.getShop_list());
                }
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                LogCat.e(TAG, "Get shop list Failed. " + msg);
                hideLoadingDialog();
                includeNetworkError.setVisibility(View.VISIBLE);
            }
        });
    }

    private static class ShopListAdapter extends CommonListAdapter<ShopInfo> {

        private ShopListAdapter(Context context) {
            super(context, R.layout.item_mine_store, null);
        }

        @Override
        public void convert(ViewHolder holder, ShopInfo info) {
            SettingItemLayout silCompanyDetail = holder.getView(R.id.tvName);
            silCompanyDetail.setLeftText(info.getShopName());
            holder.itemView.setOnClickListener(v -> {
                CommonUtils.trackCommonEvent(mContext, "defaultStore",
                        "主页_我的_我的店铺_默认店铺", Constants.EVENT_MY_INFO);
                ShopDetailGroupActivity_.intent(mContext)
                        .info(info)
                        .startForResult(REQUEST_CODE_SHOP);
            });
        }
    }

}
