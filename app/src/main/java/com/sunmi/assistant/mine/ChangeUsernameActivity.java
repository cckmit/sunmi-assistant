package com.sunmi.assistant.mine;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.contract.ChangeUsernameContract;
import com.sunmi.assistant.mine.presenter.ChangeUsernamePresenter;

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
        mPresenter.getUsername();
        titleBar.setRightTextViewText(R.string.str_complete);
        titleBar.setRightTextViewColor(R.color.colorText);
        titleBar.getRightTextView().setOnClickListener(this);
        cetUserName.addTextChangedListener(this);
        cetUserName.requestFocus();
        String username = SpUtils.getUsername();
        if (!TextUtils.isEmpty(username)) {
            cetUserName.setText(username);
            cetUserName.setSelection(username.length());
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
    public void updateUsernameView(String name) {
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
