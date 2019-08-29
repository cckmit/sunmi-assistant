package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopContactAreaContract;
import com.sunmi.assistant.mine.presenter.ShopContactAreaPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.ShopInfo;
import sunmi.common.utils.NumberValueFilter;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;

import static sunmi.common.utils.CommonHelper.floatTrans;


/**
 * 联系人和面积
 *
 * @author yangshijie
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_shop_modify_message)
public class ShopContactsAreaActivity extends BaseMvpActivity<ShopContactAreaPresenter>
        implements ShopContactAreaContract.View {

    private static final int CONTACTS_MAX_LENGTH = 32;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.et_shop_message)
    ClearableEditText etShopMessage;
    @ViewById(R.id.et_shop_area)
    ClearableEditText etShopArea;
    @ViewById(R.id.rl_square)
    RelativeLayout rlSquare;

    @Extra
    ShopInfo mInfo;
    @Extra
    int type;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ShopContactAreaPresenter();
        mPresenter.attachView(this);
        titleBar.getLeftLayout().setOnClickListener(v -> onBackPressed());
        titleBar.getRightText().setOnClickListener(v -> save());
        initShopMessage();

    }

    private void initShopMessage() {
        if (type == ShopDetailActivity.TYPE_CONTACT) {
            etShopMessage.requestFocus();
            titleBar.setAppTitle(R.string.company_shop_contact);
            etShopMessage.setHint(R.string.company_shop_contact_tip);
            if (!TextUtils.isEmpty(mInfo.getContactPerson())) {
                etShopMessage.setText(mInfo.getContactPerson());
                etShopMessage.setSelection(mInfo.getContactPerson().length());
            }
            etShopMessage.addTextChangedListener(new TextLengthWatcher(etShopMessage, CONTACTS_MAX_LENGTH) {
                @Override
                public void onLengthExceed(EditText view, String content) {
                    shortTip(getString(R.string.editetxt_max_length));
                }
            });

        } else if (type == ShopDetailActivity.TYPE_CONTACT_TEL) {
            etShopMessage.requestFocus();
            titleBar.setAppTitle(R.string.company_shop_mobile);
            etShopMessage.setHint(R.string.company_shop_contact_tel_tip);
            etShopMessage.setInputType(InputType.TYPE_CLASS_PHONE);
            if (!TextUtils.isEmpty(mInfo.getContactTel())) {
                etShopMessage.setText(mInfo.getContactTel());
                etShopMessage.setSelection(mInfo.getContactTel().length());
            }
            etShopMessage.addTextChangedListener(new TextLengthWatcher(etShopMessage, CONTACTS_MAX_LENGTH) {
                @Override
                public void onLengthExceed(EditText view, String content) {
                    shortTip(getString(R.string.editetxt_max_length));
                }
            });
        } else if (type == ShopDetailActivity.TYPE_AREA) {
            etShopArea.requestFocus();
            titleBar.setAppTitle(R.string.company_shop_area);
            etShopArea.setHint(R.string.company_shop_area_tip);
            rlSquare.setVisibility(View.VISIBLE);
            etShopMessage.setVisibility(View.GONE);
            etShopArea.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (mInfo.getBusinessArea() > 0) {
                etShopArea.setText(floatTrans(mInfo.getBusinessArea()));
                etShopArea.setSelection(floatTrans(mInfo.getBusinessArea()).length());
            }
            //默认两位小数
            etShopArea.setFilters(new InputFilter[]{new NumberValueFilter()});
        }
    }

    private String shopMessageText(ClearableEditText text) {
        return text.getText() == null ? null : text.getText().toString().trim();
    }

    private void save() {
        if (isFastClick(1500)) {
            return;
        }

        if (type == ShopDetailActivity.TYPE_CONTACT) {
            String shopMessage = shopMessageText(etShopMessage);
            if (TextUtils.isEmpty(shopMessage)) {
                shortTip(getString(R.string.company_shop_contact_tip));
                return;
            }
            if (HelpUtils.isContainEmoji(shopMessage)) {
                shortTip(getString(R.string.specital_text_cannot_support));
                return;
            }
            if (TextUtils.equals(shopMessage, mInfo.getContactPerson())) {
                finish();
                return;
            }
            mInfo.setContactPerson(shopMessage);
        } else if (type == ShopDetailActivity.TYPE_CONTACT_TEL) {
            String shopMessage = shopMessageText(etShopMessage);
            if (TextUtils.isEmpty(shopMessage)) {
                shortTip(getString(R.string.company_shop_contact_tel_tip));
                return;
            }
            if (TextUtils.equals(shopMessage, mInfo.getContactTel())) {
                finish();
                return;
            }
            if (!RegexUtils.isChinaPhone(shopMessage)) {
                shortTip(getString(R.string.str_invalid_phone));
                return;
            }
            mInfo.setContactTel(shopMessage);
        } else if (type == ShopDetailActivity.TYPE_AREA) {
            String shopMessage = shopMessageText(etShopArea);
            if (TextUtils.isEmpty(shopMessage)) {
                shortTip(getString(R.string.company_shop_area_tip));
                return;
            }
            if (TextUtils.equals(shopMessage, "0") ||
                    TextUtils.equals(shopMessage, "0.0") ||
                    TextUtils.equals(shopMessage, "0.00")) {
                shortTip(getString(R.string.company_shop_area_tip));
                return;
            }
            if (TextUtils.equals(shopMessage, String.valueOf(mInfo.getBusinessArea()))) {
                finish();
                return;
            }
            mInfo.setBusinessArea(Float.parseFloat(shopMessage));
        }
        mPresenter.editShopMessage(type, mInfo);
    }

    @Override
    public void contactView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_CONTACT, shopMessageText(etShopMessage));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void contactTelView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_CONTACT_TEL, shopMessageText(etShopMessage));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void areaView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_AREA, shopMessageText(etShopArea));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (type == ShopDetailActivity.TYPE_CONTACT &&
                TextUtils.equals(mInfo.getContactPerson(), shopMessageText(etShopMessage))
                || type == ShopDetailActivity.TYPE_CONTACT_TEL &&
                TextUtils.equals(mInfo.getContactTel(), shopMessageText(etShopMessage))
                || type == ShopDetailActivity.TYPE_AREA &&
                TextUtils.equals(floatTrans(mInfo.getBusinessArea()), shopMessageText(etShopArea))) {
            super.onBackPressed();
            return;
        }
        DialogUtils.isCancelSetting(this);
    }
}
