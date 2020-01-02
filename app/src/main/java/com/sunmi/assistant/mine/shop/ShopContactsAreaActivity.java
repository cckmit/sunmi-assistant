package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

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
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.ShopInfo;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.NumberValueFilter;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.SettingItemEditTextLayout;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;

import static sunmi.common.utils.CommonHelper.floatTrans;


/**
 * 联系人和面积
 *
 * @author yangshijie
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_change_username)
public class ShopContactsAreaActivity extends BaseMvpActivity<ShopContactAreaPresenter>
        implements ShopContactAreaContract.View {

    private static final int CONTACTS_MAX_LENGTH = 32;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.cet_username)
    SettingItemEditTextLayout etShopMessage;

    @Extra
    ShopInfo info;
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
        if (type == ShopDetailActivity.SHOP_CHANGE_NAME) {
            initData(R.string.company_shop_create_name, R.string.company_shop_create_hint, info.getShopName());
        } else if (type == ShopDetailActivity.SHOP_CHANGE_CONTACT) {
            initData(R.string.company_shop_contact, R.string.company_shop_contact_tip, info.getContactPerson());
        } else if (type == ShopDetailActivity.SHOP_CHANGE_CONTACT_TEL) {
            etShopMessage.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
            initData(R.string.company_shop_mobile, R.string.company_shop_contact_tel_tip, info.getContactTel());
        } else if (type == ShopDetailActivity.SHOP_CHANGE_AREA) {
            etShopMessage.setRightText(R.string.company_square);
            etShopMessage.setRightTextColor(ContextCompat.getColor(context, R.color.text_main));
            etShopMessage.setRightTextSize(R.dimen.sp_20);
            etShopMessage.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            //默认两位小数
            etShopMessage.getEditText().setFilters(new InputFilter[]{new NumberValueFilter(info.getBusinessArea() >= 100000 ? 7 : 6)});
            initData(R.string.company_shop_area, R.string.company_shop_area_tip, info.getBusinessArea() > 0 ? floatTrans(info.getBusinessArea()) : "");
        }
    }

    private void initData(int company_shop_contact, int company_shop_contact_tip, String contactPerson) {
        titleBar.setAppTitle(company_shop_contact);
        etShopMessage.setEditTextHint(company_shop_contact_tip);
        if (!TextUtils.isEmpty(contactPerson)) {
            etShopMessage.setEditTextText(contactPerson);
            etShopMessage.setSelection(contactPerson.length());
        }
        etShopMessage.addTextChangedListener(new TextLengthWatcher(etShopMessage.getEditText(), CONTACTS_MAX_LENGTH) {
            @Override
            public void onLengthExceed(EditText view, String content) {
                shortTip(getString(R.string.editetxt_max_length));
            }
        });
    }

    private void save() {
        if (isFastClick(1500)) {
            return;
        }
        String input = etShopMessage.getEditTextText();
        if (type == ShopDetailActivity.SHOP_CHANGE_NAME) {
            if (isEmptyOrNoChanged(input, info.getShopName(), R.string.company_shop_create_hint)) {
                return;
            }
            info.setShopName(input);
        } else if (type == ShopDetailActivity.SHOP_CHANGE_CONTACT) {
            if (isEmptyOrNoChanged(input, info.getContactPerson(), R.string.company_shop_contact_tip)) {
                return;
            }
            if (HelpUtils.isContainEmoji(input)) {
                shortTip(R.string.specital_text_cannot_support);
                return;
            }
            info.setContactPerson(input);
        } else if (type == ShopDetailActivity.SHOP_CHANGE_CONTACT_TEL) {
            if (isEmptyOrNoChanged(input, info.getContactTel(), R.string.company_shop_contact_tel_tip)) {
                return;
            }
            if (!RegexUtils.isCorrectAccount(input)) {
                shortTip(R.string.str_invalid_phone);
                return;
            }
            info.setContactTel(input);
        } else if (type == ShopDetailActivity.SHOP_CHANGE_AREA) {
            if (isEmptyOrNoChanged(input, String.valueOf(info.getBusinessArea()),
                    R.string.company_shop_area_tip)) {
                return;
            }
            if (TextUtils.equals(input, "0") ||
                    TextUtils.equals(input, "0.0") ||
                    TextUtils.equals(input, "0.00")) {
                shortTip(getString(R.string.company_shop_area_tip));
                return;
            }
            info.setBusinessArea(Double.parseDouble(input));
        }
        mPresenter.editShopMessage(type, info);
    }

    private boolean isEmptyOrNoChanged(String newInfo, String originalInfo, int resIdTip) {
        if (TextUtils.isEmpty(newInfo)) {
            shortTip(resIdTip);
            return true;
        }
        if (TextUtils.equals(newInfo, originalInfo)) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void shopNameChanged() {
        if (info.getShopId() == SpUtils.getShopId()) {
            SpUtils.setShopName(etShopMessage.getEditTextText());
        }
        BaseNotification.newInstance().postNotificationName(
                CommonNotifications.shopNameChanged, info.getShopId(), etShopMessage.getEditTextText());
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_NAME, etShopMessage.getEditTextText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void contactView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_CONTACT, etShopMessage.getEditTextText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void contactTelView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_CONTACT_TEL, etShopMessage.getEditTextText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void areaView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_AREA, etShopMessage.getEditTextText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(etShopMessage.getEditTextText())
                || (type == ShopDetailActivity.SHOP_CHANGE_NAME &&
                TextUtils.equals(info.getShopName(), etShopMessage.getEditTextText()))
                || (type == ShopDetailActivity.SHOP_CHANGE_CONTACT &&
                TextUtils.equals(info.getContactPerson(), etShopMessage.getEditTextText()))
                || (type == ShopDetailActivity.SHOP_CHANGE_CONTACT_TEL &&
                TextUtils.equals(info.getContactTel(), etShopMessage.getEditTextText()))) {
            super.onBackPressed();
            return;
        }
        if (type == ShopDetailActivity.SHOP_CHANGE_AREA) {
            if (TextUtils.equals(floatTrans(info.getBusinessArea()), etShopMessage.getEditTextText())) {
                super.onBackPressed();
                return;
            } else if (TextUtils.isEmpty(etShopMessage.getEditTextText())) {
                if (TextUtils.equals(floatTrans(info.getBusinessArea()), "0") ||
                        TextUtils.equals(floatTrans(info.getBusinessArea()), "0.0") ||
                        TextUtils.equals(floatTrans(info.getBusinessArea()), "0.00")) {
                    super.onBackPressed();
                    return;
                }
            }
        }
        DialogUtils.isCancelSetting(this);
    }

}
