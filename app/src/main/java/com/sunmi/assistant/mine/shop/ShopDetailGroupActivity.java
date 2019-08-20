package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.sunmi.assistant.R;
import com.sunmi.ipc.face.FaceGroupListActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;

/**
 * @author yangShiJie
 * @date 2019/8/19
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_store_detatils_group)
public class ShopDetailGroupActivity extends BaseActivity {
    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @Extra
    int shopId;
    @Extra
    String shopName;
    private boolean isUpdateShopInfo;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.setAppTitle(shopName);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (isUpdateShopInfo) {
            Intent intent = getIntent();
            intent.putExtra(INTENT_EXTRA_SUCCESS, true);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }

    @Click(R.id.sil_shop_detail)
    public void toShopDetail() {
        ShopDetailActivity_.intent(context).shopId(shopId).startForResult(ShopListActivity.REQUEST_CODE_SHOP);
    }

    @Click(R.id.sil_shop_face)
    public void toShopFace() {
        FaceGroupListActivity_.intent(this).mShopId(shopId).start();
    }

    @OnActivityResult(ShopListActivity.REQUEST_CODE_SHOP)
    void onResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
        }
    }
}
