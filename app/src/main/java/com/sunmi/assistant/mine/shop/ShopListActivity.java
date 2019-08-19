package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.mine.platform.SelectPlatformActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.ShopListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.bottompopmenu.BottomPopMenu;
import sunmi.common.view.bottompopmenu.PopItemAction;

/**
 * 我的店铺
 *
 * @author yangshijie
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_my_store)
public class ShopListActivity extends BaseActivity {

    public static final String INTENT_EXTRA_SUCCESS = "success";
    public static final int REQUEST_CODE_SHOP = 100;

    @ViewById(R.id.recyclerView)
    RecyclerView recyclerView;
    private ShopListAdapter mAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ShopListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        getShopList();
    }

    @Click(R.id.btnAdd)
    void onAddClick() {
        CommonUtils.trackCommonEvent(context, "addStore",
                "主页_我的_我的店铺_添加店铺", Constants.EVENT_MY_INFO);
        BottomPopMenu choosePhotoMenu = new BottomPopMenu.Builder(this)
                .setTitle(R.string.company_shop_new_create_or_import)
                .setIsShowCircleBackground(true)
                .addItemAction(new PopItemAction(R.string.company_shop_new_create,
                        PopItemAction.PopItemStyle.Normal, this::createShop))
                .addItemAction(new PopItemAction(R.string.company_shop_import,
                        PopItemAction.PopItemStyle.Normal, this::importShop))
                .addItemAction(new PopItemAction(R.string.sm_cancel,
                        PopItemAction.PopItemStyle.Cancel))
                .create();
        choosePhotoMenu.show();

    }

    private void createShop() {
        CreateShopActivity_.intent(context)
                .companyId(SpUtils.getCompanyId())
                .startForResult(REQUEST_CODE_SHOP);
    }

    private void importShop() {
        SelectPlatformActivity_.intent(context)
                .isCanBack(true)
                .startForResult(REQUEST_CODE_SHOP);
    }

    @OnActivityResult(REQUEST_CODE_SHOP)
    void onResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null
                && data.getBooleanExtra(INTENT_EXTRA_SUCCESS, false)) {
            getShopList();
        }
    }

    private void getShopList() {
        showLoadingDialog();
        SunmiStoreRemote.get().getShopList(SpUtils.getCompanyId(), new RetrofitCallback<ShopListResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopListResp data) {
                mAdapter.setData(data.getShop_list());
            }

            @Override
            public void onFail(int code, String msg, ShopListResp data) {
                LogCat.e(TAG, "Get shop list Failed. " + msg);
                shortTip(getString(R.string.str_store_load_error));
            }
        });
    }

    private static class ShopListAdapter extends CommonListAdapter<ShopListResp.ShopInfo> {

        private ShopListAdapter(Context context) {
            super(context, R.layout.item_mine_store, null);
        }

        @Override
        public void convert(ViewHolder holder, ShopListResp.ShopInfo info) {
            holder.setText(R.id.tvName, info.getShop_name());
            SettingItemLayout silCompanyDetail = holder.getView(R.id.sil_shop_detail);
            SettingItemLayout silCompanyFace = holder.getView(R.id.sil_shop_face);
            silCompanyDetail.setOnClickListener(v -> {
                CommonUtils.trackCommonEvent(mContext, "defaultStore",
                        "主页_我的_我的店铺_默认店铺", Constants.EVENT_MY_INFO);
                ShopDetailActivity_.intent(mContext).shopId(info.getShop_id()).startForResult(REQUEST_CODE_SHOP);
            });
            silCompanyFace.setOnClickListener(v -> {
                //TODO
            });
        }
    }

}
