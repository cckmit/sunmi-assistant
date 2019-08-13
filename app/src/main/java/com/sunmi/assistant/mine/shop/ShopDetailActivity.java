package com.sunmi.assistant.mine.shop;

import android.app.Activity;
import android.content.Intent;

import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.mine.model.ShopInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.ShopInfoResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.SettingItemLayout;

/**
 * 我的店铺详情
 *
 * @author yinhui
 */
@EActivity(R.layout.activity_mine_store_detatils)
public class ShopDetailActivity extends BaseActivity {

    static final String INTENT_EXTRA_NAME = "name";
    static final String INTENT_EXTRA_ADDRESS = "address";

    private static final int REQUEST_CODE_NAME = 100;
    private static final int REQUEST_CODE_CATEGORY = 101;
    private static final int REQUEST_CODE_REGION = 102;
    private static final int REQUEST_CODE_ADDRESS = 103;

    @ViewById(R.id.sil_shop_name)
    SettingItemLayout silShopName;
    @ViewById(R.id.sil_shop_category)
    SettingItemLayout silShopCategory;
    @ViewById(R.id.sil_shop_region)
    SettingItemLayout silShopRegion;
    @ViewById(R.id.sil_shop_address)
    SettingItemLayout silShopAddress;

    @Extra
    int shopId;
    private ShopInfo mInfo;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        silShopName.getRightText().setSingleLine();
        getShopInfo(shopId);
    }

    private void getShopInfo(int shopId) {
        showLoadingDialog();
        SunmiStoreRemote.get().getShopInfo(shopId, new RetrofitCallback<ShopInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, ShopInfoResp data) {
                hideLoadingDialog();
                mInfo = new ShopInfo(data);
                silShopName.setRightText(mInfo.getShopName());
                silShopCategory.setRightText(mInfo.getTypeName());
                silShopRegion.setRightText(mInfo.getRegionName());
                silShopAddress.setRightText(mInfo.getAddress());
                LogCat.d(TAG, "Shop info:" + mInfo);
            }

            @Override
            public void onFail(int code, String msg, ShopInfoResp data) {
                hideLoadingDialog();
                LogCat.e(TAG, "Get shop info Failed. " + msg);
                shortTip(R.string.toast_network_Exception);
            }
        });
    }

    @Click(R.id.sil_shop_name)
    public void toModifyName() {
        CommonUtils.trackCommonEvent(context, "defaultStoreName",
                "主页_我的_我的店铺_默认店铺_门店名称", Constants.EVENT_MY_INFO);
        ShopNameActivity_.intent(this).mInfo(mInfo).startForResult(REQUEST_CODE_NAME);
    }

    @Click(R.id.sil_shop_category)
    public void toModifyCategory() {
        CommonUtils.trackCommonEvent(context, "defaultStoreType",
                "主页_我的_我的店铺_默认店铺_经营品类", Constants.EVENT_MY_INFO);
        ShopCategoryActivity_.intent(this).mInfo(mInfo).startForResult(REQUEST_CODE_CATEGORY);
    }

    @Click(R.id.sil_shop_region)
    public void toModifyRegion() {
        CommonUtils.trackCommonEvent(context, "defaultStoreAddress",
                "主页_我的_我的店铺_默认店铺_门店地址", Constants.EVENT_MY_INFO);
        ShopRegionActivity_.intent(this).mInfo(mInfo).startForResult(REQUEST_CODE_REGION);
    }

    @Click(R.id.sil_shop_address)
    public void toModifyAddress() {
        CommonUtils.trackCommonEvent(context, "defaultStoreAddressDetail",
                "主页_我的_我的店铺_默认店铺_详细地址", Constants.EVENT_MY_INFO);
        ShopAddressActivity_.intent(this).mInfo(mInfo).startForResult(REQUEST_CODE_ADDRESS);
    }

    @OnActivityResult(REQUEST_CODE_NAME)
    public void onNameResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mInfo.setShopName(data.getStringExtra(INTENT_EXTRA_NAME));
            silShopName.setRightText(mInfo.getShopName());
        }
    }

    @OnActivityResult(REQUEST_CODE_CATEGORY)
    public void onCategoryResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getShopInfo(shopId);
        }
    }

    @OnActivityResult(REQUEST_CODE_REGION)
    public void onRegionResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            getShopInfo(shopId);
        }
    }

    @OnActivityResult(REQUEST_CODE_ADDRESS)
    public void onAddressResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mInfo.setAddress(data.getStringExtra(INTENT_EXTRA_ADDRESS));
            silShopAddress.setRightText(mInfo.getAddress());
        }
    }

}
