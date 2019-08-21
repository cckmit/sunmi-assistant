package com.sunmi.assistant.mine.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ShopContactAreaContract;
import com.sunmi.assistant.mine.presenter.ShopContactAreaPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.ShopInfo;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TextLengthWatcher;
import sunmi.common.view.TitleBarView;


/**
 * 联系人和面积
 *
 * @author yangshijie
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_mine_shop_modify_message)
public class ShopContactsAreaActivity extends BaseMvpActivity<ShopContactAreaPresenter>
        implements ShopContactAreaContract.View {

    private static final int SHOP_NAME_MAX_LENGTH = 20;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.et_shop_message)
    ClearableEditText etShopMessage;
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
        etShopMessage.requestFocus();
        initShopMessage();
        etShopMessage.addTextChangedListener(new TextLengthWatcher(etShopMessage, SHOP_NAME_MAX_LENGTH) {
            @Override
            public void onLengthExceed(EditText view, String content) {
                shortTip(R.string.company_create_shop_max_length);
            }
        });
    }

    private void initShopMessage() {
        if (type == ShopDetailActivity.TYPE_CONTACT) {
            titleBar.setAppTitle(R.string.company_shop_contact);
            etShopMessage.setHint(R.string.company_shop_contact_tip);
            if (!TextUtils.isEmpty(mInfo.getContactPerson())) {
                etShopMessage.setText(mInfo.getContactPerson());
                etShopMessage.setSelection(mInfo.getContactPerson().length());
            }
        } else if (type == ShopDetailActivity.TYPE_CONTACT_TEL) {
            titleBar.setAppTitle(R.string.company_shop_mobile);
            etShopMessage.setHint(R.string.company_shop_contact_tel_tip);
            etShopMessage.setInputType(InputType.TYPE_CLASS_PHONE);
            if (!TextUtils.isEmpty(mInfo.getContactTel())) {
                etShopMessage.setText(mInfo.getContactTel());
                etShopMessage.setSelection(mInfo.getContactTel().length());
            }
        } else if (type == ShopDetailActivity.TYPE_AREA) {
            titleBar.setAppTitle(R.string.company_shop_area);
            ConstraintLayout.LayoutParams viewGroup = (ConstraintLayout.LayoutParams) etShopMessage.getLayoutParams();
            viewGroup.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
            viewGroup.rightMargin = 150;
            etShopMessage.setLayoutParams(viewGroup);
            etShopMessage.setHint(R.string.company_shop_area_tip);
            rlSquare.setVisibility(View.VISIBLE);
            etShopMessage.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (mInfo.getBusinessArea() > 0) {
                etShopMessage.setText(String.valueOf(mInfo.getBusinessArea()));
                etShopMessage.setSelection(String.valueOf(mInfo.getBusinessArea()).length());
            }
        }
    }

    private String shopMessageText() {
        return etShopMessage.getText() == null ? null : etShopMessage.getText().toString().trim();
    }

    private void save() {
        if (isFastClick(1500)) {
            return;
        }
        String shopMessage = shopMessageText();
        if (type == ShopDetailActivity.TYPE_CONTACT) {
            if (TextUtils.isEmpty(shopMessage)) {
                shortTip(getString(R.string.company_shop_contact_tip));
                return;
            }
            if (TextUtils.equals(shopMessage, mInfo.getContactPerson())) {
                finish();
                return;
            }
            mInfo.setContactPerson(shopMessage);
        } else if (type == ShopDetailActivity.TYPE_CONTACT_TEL) {
            if (TextUtils.isEmpty(shopMessage)) {
                shortTip(getString(R.string.company_shop_contact_tel_tip));
                return;
            }
            if (TextUtils.equals(shopMessage, mInfo.getContactTel())) {
                finish();
                return;
            }
            if (!RegexUtils.isChinaPhone(shopMessage)) {
                shortTip(getString(R.string.company_shop_check_mobile));
                return;
            }
            mInfo.setContactTel(shopMessage);
        } else if (type == ShopDetailActivity.TYPE_AREA) {
            if (TextUtils.isEmpty(shopMessage)) {
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
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_CONTACT, shopMessageText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void contactTelView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_CONTACT_TEL, shopMessageText());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void areaView() {
        Intent intent = getIntent();
        intent.putExtra(ShopDetailActivity.INTENT_EXTRA_AREA, shopMessageText());
        setResult(RESULT_OK, intent);
        finish();
    }
}
