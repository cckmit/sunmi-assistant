package com.sunmi.assistant.mine.shop;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.ShopInfo;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;

/**
 * @author yinhui
 * @date 2019-08-08
 */
@EActivity(R.layout.activity_mine_address_details)
public class ShopAddressActivity extends BaseActivity {

    private static final int MAX_LENGTH = 100;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.etAddress)
    EditText etAddress;
    @ViewById(R.id.tvCounts)
    TextView tvCounts;

    @Extra
    ShopInfo mInfo;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        titleBar.getRightText().setOnClickListener(this::save);
        etAddress.addTextChangedListener(new TextChecker());
        etAddress.setText(mInfo.getAddress());
        etAddress.requestFocus();
        if (!TextUtils.isEmpty(mInfo.getAddress())) {
            etAddress.setSelection(mInfo.getAddress().length());
        }
    }

    private void save(View v) {
        if (isFastClick(1500)) {
            return;
        }
        String address = etAddress.getText() == null ? null : etAddress.getText().toString().trim();
        if (TextUtils.isEmpty(address)) {
            shortTip(R.string.tip_addr_empty);
            return;
        }
        if (TextUtils.equals(address, mInfo.getAddress())) {
            finish();
            return;
        }
        updateShopAddress(address);
    }

    private void updateShopAddress(String address) {
        showLoadingDialog();
        mInfo.setAddress(address);
        SunmiStoreApi.getInstance().updateShopInfo(mInfo, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                hideLoadingDialog();
                Intent intent = getIntent();
                intent.putExtra(ShopDetailActivity.INTENT_EXTRA_ADDRESS, address);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                hideLoadingDialog();
                LogCat.e(TAG, "Update shop address Failed. " + msg);
                shortTip(R.string.tip_save_fail);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(etAddress.getText()) ||
                TextUtils.equals(mInfo.getAddress(), etAddress.getText() == null ? null : etAddress.getText().toString().trim())) {
            super.onBackPressed();
        } else {
            DialogUtils.isCancelSetting(this);
        }
    }

    private class TextChecker implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            tvCounts.setText(String.format(Locale.getDefault(), "%d/%d", s.length(), MAX_LENGTH));
            if (s.length() > MAX_LENGTH) {
                shortTip(R.string.tip_over_limit_words);
                s.delete(MAX_LENGTH, s.length());
            }
        }
    }
}
