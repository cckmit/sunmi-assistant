package com.sunmi.assistant.ui.activity.setting;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.contract.ChangeCompanyNameContract;
import com.sunmi.apmanager.presenter.ChangeCompanyNamePresenter;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * Description:
 * Created by bruce on 2019/6/6.
 */
@EActivity(R.layout.activity_change_username)
public class ChangeCompanyNameActivity extends BaseMvpActivity<ChangeCompanyNamePresenter>
        implements ChangeCompanyNameContract.View, View.OnClickListener, TextWatcher {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.cet_username)
    ClearableEditText cetUserName;

    private String name;

    @AfterViews
    void init() {
        mPresenter = new ChangeCompanyNamePresenter();
        mPresenter.attachView(this);
        mPresenter.getCompanyInfo(SpUtils.getCompanyId());
        titleBar.setAppTitle(R.string.str_change_company_name);
        titleBar.setRightTextViewText(R.string.str_save);
        titleBar.setRightTextViewColor(R.color.colorText);
        titleBar.getRightTextView().setOnClickListener(this);
        cetUserName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        cetUserName.addTextChangedListener(this);
        cetUserName.requestFocus();
    }

    @Override
    public void onClick(View v) {
        name = cetUserName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            shortTip(R.string.tip_input_company_name);
            return;
        }
        mPresenter.updateCompanyName(SpUtils.getCompanyId(), name);
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

    @Override
    public void getInfoSuccess(String bean) {
        try {
            JSONObject jsonObject = new JSONObject(bean);
            if (jsonObject.has("full_name")) {
                cetUserName.setText(jsonObject.getString("full_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getInfoFail(int code, String msg) {
        finish();
    }

    @Override
    public void updateSuccess(String bean) {
        SpUtils.setCompanyName(name);
        BaseNotification.newInstance().postNotificationName(NotificationConstant.companyNameChanged);
        finish();
    }

    @Override
    public void updateFail(int code, String msg) {
        shortTip("");
    }

}
