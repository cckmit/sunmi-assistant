package com.sunmi.assistant.mine.shop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.ShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemLayout;
import sunmi.common.view.TitleBarView;

import static com.sunmi.assistant.mine.shop.ShopDetailGroupActivity.INTENT_EXTRA_SHOP_NAME;
import static sunmi.common.utils.CommonHelper.floatTrans;
import static sunmi.common.utils.CommonHelper.isGooglePlay;

/**
 * 我的店铺详情
 *
 * @author yinhui
 */
@EActivity(R.layout.activity_mine_store_detatils)
public class ShopDetailActivity extends BaseActivity {

    private static final String BUNDLE_STATE_SHOP = "shop";
    static final String INTENT_EXTRA_NAME = "name";
    static final String INTENT_EXTRA_ADDRESS = "address";
    static final String INTENT_EXTRA_CONTACT = "shop_contact";
    static final String INTENT_EXTRA_CONTACT_TEL = "shop_contact_tel";
    static final String INTENT_EXTRA_AREA = "business_area";
    //修改门店信息
    public static final int SHOP_CHANGE_NAME = 0;
    public static final int SHOP_CHANGE_CONTACT = 1;
    public static final int SHOP_CHANGE_CONTACT_TEL = 2;
    public static final int SHOP_CHANGE_AREA = 3;

    private static final int REQUEST_CODE_NAME = 100;
    private static final int REQUEST_CODE_CATEGORY = 101;
    private static final int REQUEST_CODE_REGION = 102;
    private static final int REQUEST_CODE_ADDRESS = 103;
    private static final int REQUEST_CODE_CONTACT = 104;
    private static final int REQUEST_CODE_CONTACT_TEL = 105;
    private static final int REQUEST_CODE_AREA = 106;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.sil_shop_name)
    SettingItemLayout silShopName;
    @ViewById(R.id.sil_shop_category)
    SettingItemLayout silShopCategory;
    @ViewById(R.id.sil_shop_region)
    SettingItemLayout silShopRegion;
    @ViewById(R.id.sil_shop_address)
    SettingItemLayout silShopAddress;
    @ViewById(R.id.sil_shop_contact)
    SettingItemLayout silShopContact;
    @ViewById(R.id.sil_shop_mobile)
    SettingItemLayout silShopMobile;
    @ViewById(R.id.sil_shop_area)
    SettingItemLayout silShopArea;

    @Extra
    ShopInfo info;

    /**
     * 是否更新了店铺信息
     */
    private boolean isUpdateShopInfo;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        if (!isGooglePlay()) {
            silShopCategory.setVisibility(View.VISIBLE);
            silShopRegion.setVisibility(View.VISIBLE);
            silShopArea.setVisibility(View.VISIBLE);
            silShopMobile.setVisibility(View.VISIBLE);
        }
        setupItems();
        getShopInfo(info.getShopId());
    }

    private void setupItems() {
        silShopName.setEndContent(info.getShopName());
        silShopCategory.setEndContent(info.getTypeName());
        silShopRegion.setEndContent(info.getRegion());
        silShopAddress.setEndContent(info.getAddress());
        silShopContact.setEndContent(info.getContactPerson());
        silShopMobile.setEndContent(info.getContactTel());
        if (info.getBusinessArea() > 0) {
            silShopArea.setEndContent(floatTrans(info.getBusinessArea()) + "㎡");
        }
    }

    @Override
    public void onBackPressed() {
        if (isUpdateShopInfo) {
            Intent intent = getIntent();
            intent.putExtra(INTENT_EXTRA_SHOP_NAME, info.getShopName());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    private void getShopInfo(int shopId) {
        showLoadingDialog();
        SunmiStoreApi.getInstance().getShopInfo(shopId, new RetrofitCallback<ShopInfo>() {
            @Override
            public void onSuccess(int code, String msg, ShopInfo data) {
                hideLoadingDialog();
                info = data;
                setupItems();
            }

            @Override
            public void onFail(int code, String msg, ShopInfo data) {
                hideLoadingDialog();
                shortTip(R.string.toast_network_error);
            }
        });
    }

    @Click(R.id.sil_shop_name)
    public void toModifyName() {
        ShopContactsAreaActivity_.intent(this).info(info)
                .type(SHOP_CHANGE_NAME).startForResult(REQUEST_CODE_NAME);
    }

    @Click(R.id.sil_shop_category)
    public void toModifyCategory() {
        ShopCategoryActivity_.intent(context).info(info).startForResult(REQUEST_CODE_CATEGORY);
    }

    @Click(R.id.sil_shop_region)
    public void toModifyRegion() {
        ShopEditAddressActivity_.intent(context).info(info).startForResult(REQUEST_CODE_REGION);
    }

    @Click(R.id.sil_shop_address)
    public void toModifyAddress() {
        ShopAddressActivity_.intent(context).info(info).startForResult(REQUEST_CODE_ADDRESS);
    }

    @Click(R.id.sil_shop_contact)
    public void toModifyContact() {
        ShopContactsAreaActivity_.intent(context).info(info)
                .type(SHOP_CHANGE_CONTACT).startForResult(REQUEST_CODE_CONTACT);
    }

    @Click(R.id.sil_shop_mobile)
    public void toModifyContactTel() {
        ShopContactsAreaActivity_.intent(context).info(info)
                .type(SHOP_CHANGE_CONTACT_TEL).startForResult(REQUEST_CODE_CONTACT_TEL);
    }

    @Click(R.id.sil_shop_area)
    public void toModifyArea() {
        ShopContactsAreaActivity_.intent(context).info(info)
                .type(SHOP_CHANGE_AREA).startForResult(REQUEST_CODE_AREA);
    }

    @OnActivityResult(REQUEST_CODE_NAME)
    public void onNameResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            info.setShopName(data.getStringExtra(INTENT_EXTRA_NAME));
            silShopName.setEndContent(info.getShopName());
        }
    }

    @OnActivityResult(REQUEST_CODE_CATEGORY)
    public void onCategoryResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            getShopInfo(info.getShopId());
        }
    }

    @OnActivityResult(REQUEST_CODE_REGION)
    public void onRegionResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            getShopInfo(info.getShopId());
        }
    }

    @OnActivityResult(REQUEST_CODE_ADDRESS)
    public void onAddressResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            info.setAddress(data.getStringExtra(INTENT_EXTRA_ADDRESS));
            silShopAddress.setEndContent(info.getAddress());
        }
    }

    @OnActivityResult(REQUEST_CODE_CONTACT)
    public void onContactResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            info.setContactPerson(data.getStringExtra(INTENT_EXTRA_CONTACT));
            silShopContact.setEndContent(info.getContactPerson());
        }
    }

    @OnActivityResult(REQUEST_CODE_CONTACT_TEL)
    public void onContactTelResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            info.setContactTel(data.getStringExtra(INTENT_EXTRA_CONTACT_TEL));
            silShopMobile.setEndContent(info.getContactTel());
        }
    }

    @OnActivityResult(REQUEST_CODE_AREA)
    public void onAreaResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            isUpdateShopInfo = true;
            info.setBusinessArea(Double.parseDouble(data.getStringExtra(INTENT_EXTRA_AREA)));
            silShopArea.setEndContent(floatTrans(info.getBusinessArea()) + "㎡");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        info = savedInstanceState.getParcelable(BUNDLE_STATE_SHOP);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_STATE_SHOP, info);
    }

}
