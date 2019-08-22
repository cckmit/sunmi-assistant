package com.sunmi.assistant.mine.shop;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.ShopInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-08-08
 */
@EActivity(R.layout.activity_mine_add_store)
public class ShopNameActivity extends BaseActivity {

    private static final int SHOP_NAME_MAX_LENGTH = 20;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.etName)
    ClearableEditText etName;

    @Extra
    ShopInfo mInfo;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        titleBar.getRightText().setOnClickListener(v -> save());
        etName.requestFocus();
        if (!TextUtils.isEmpty(mInfo.getShopName())) {
            etName.setText(mInfo.getShopName());
            etName.setSelection(mInfo.getShopName().length());
        }
        etName.addTextChangedListener(new TextLengthWatcher(etName, SHOP_NAME_MAX_LENGTH) {
            @Override
            public void onLengthExceed(EditText view, String content) {
                shortTip(R.string.company_create_shop_max_length);
            }
        });
    }

    private void save() {
        if (isFastClick(1500)) {
            return;
        }
        String name = etName.getText() == null ? null : etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            shortTip(R.string.tip_input_name);
            return;
        }
        if (TextUtils.equals(name, mInfo.getShopName())) {
            finish();
            return;
        }
        showLoadingDialog();
        mInfo.setShopName(name);
        SunmiStoreApi.getInstance().updateShopInfo(mInfo, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                hideLoadingDialog();
                if (mInfo.getShopId() == SpUtils.getShopId()) {
                    SpUtils.setShopName(name);
                }
                BaseNotification.newInstance().postNotificationName(
                        NotificationConstant.shopNameChanged, mInfo.getShopId(), name);
                Intent intent = getIntent();
                intent.putExtra(ShopDetailActivity.INTENT_EXTRA_NAME, name);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                hideLoadingDialog();
                LogCat.e(TAG, "Update shop name Failed. " + msg);
                shortTip(R.string.tip_save_fail);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.equals(mInfo.getShopName(),
                etName.getText() == null ? null : etName.getText().toString().trim())) {
            super.onBackPressed();
        } else {
            DialogUtils.isCancelSetting(this);
        }
    }

}
