package com.sunmi.assistant.ui.activity.setting;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.sunmi.apmanager.contract.ChangeUsernameContract;
import com.sunmi.apmanager.presenter.ChangeUsernamePresenter;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * 设置昵称页面
 *
 * @author bruce
 * @date 2019/1/29
 */
@EActivity(R.layout.activity_change_username)
public class ChangeUsernameActivity extends BaseMvpActivity<ChangeUsernamePresenter>
        implements ChangeUsernameContract.View, View.OnClickListener, TextWatcher {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.cet_username)
    ClearableEditText cetUserName;

    String userName;

    @AfterViews
    void init() {
        mPresenter = new ChangeUsernamePresenter();
        mPresenter.attachView(this);
        titleBar.setRightTextViewText(R.string.str_complete);
        titleBar.setRightTextViewColor(R.color.colorText);
        titleBar.getRightTextView().setOnClickListener(this);
        cetUserName.addTextChangedListener(this);
        if (!TextUtils.isEmpty(SpUtils.getUsername())) {
            cetUserName.setText(SpUtils.getUsername());
        }
    }

    @Override
    public void onClick(View v) {
        Editable text = cetUserName.getText();
        if (text == null || TextUtils.isEmpty(userName = text.toString().trim())) {
            shortTip(R.string.tip_input_username);
            return;
        }
        mPresenter.updateUsername(userName);
    }

    @Override
    public void updateSuccess() {
        shortTip(R.string.tip_set_complete);
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
            titleBar.setRightTextViewColor(R.color.colorText_40);
            titleBar.getRightTextView().setClickable(false);
        } else {
            titleBar.setRightTextViewColor(R.color.colorText);
            titleBar.getRightTextView().setClickable(true);
        }
    }

}
