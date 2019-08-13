package com.sunmi.assistant.mine;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ChangeCompanyNameContract;
import com.sunmi.assistant.mine.presenter.ChangeCompanyNamePresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * 更改商户名称页面
 *
 * @author bruce
 * @date 2019/6/6
 */
@EActivity(R.layout.activity_change_username)
public class ChangeCompanyNameActivity extends BaseMvpActivity<ChangeCompanyNamePresenter>
        implements ChangeCompanyNameContract.View, View.OnClickListener, TextWatcher {

    private static final int COMPANY_NAME_MAX_LENGTH = 40;

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.cet_username)
    ClearableEditText cetUserName;

    @AfterViews
    void init() {
        mPresenter = new ChangeCompanyNamePresenter();
        mPresenter.attachView(this);
        mPresenter.getCompanyInfo();
        titleBar.setAppTitle(R.string.str_change_company_name);
        titleBar.setRightTextViewText(R.string.str_save);
        titleBar.setRightTextViewColor(R.color.colorText);
        titleBar.getRightTextView().setOnClickListener(this);
        cetUserName.setHint(R.string.tip_input_company_name);
        cetUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        cetUserName.addTextChangedListener(this);
        cetUserName.requestFocus();
        String companyName = SpUtils.getCompanyName();
        if (!TextUtils.isEmpty(companyName)) {
            cetUserName.setText(companyName);
            cetUserName.setSelection(companyName.length());
        }
    }

    @Override
    public void onClick(View v) {
        String name;
        if (cetUserName.getText() == null
                || TextUtils.isEmpty(name = cetUserName.getText().toString().trim())) {
            shortTip(R.string.tip_input_company_name);
            return;
        }
        mPresenter.updateCompanyName(name);
    }

    @Override
    public void updateNameView(String name) {
        if (!TextUtils.isEmpty(name)) {
            cetUserName.setText(name);
            cetUserName.setSelection(name.length());
        }
    }

    @Override
    public void getNameFailed() {
        finish();
    }

    @Override
    public void updateSuccess() {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            titleBar.setRightTextViewColor(com.sunmi.assistant.R.color.colorText_40);
            titleBar.getRightTextView().setClickable(false);
        } else {
            titleBar.setRightTextViewColor(com.sunmi.assistant.R.color.colorText);
            titleBar.getRightTextView().setClickable(true);
        }
    }

}
