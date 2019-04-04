package com.sunmi.assistant.ui.activity.setting;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.sunmi.apmanager.constant.NotificationConstant;
import com.sunmi.apmanager.contract.ChangeUsernameContract;
import com.sunmi.apmanager.presenter.ChangeUsernamePresenter;
import sunmi.common.utils.SpUtils;
import com.sunmi.assistant.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.notification.BaseNotification;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.TitleBarView;

/**
 * Description:
 * Created by bruce on 2019/1/29.
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
        if (!TextUtils.isEmpty(SpUtils.getUsername()))
            cetUserName.setText(SpUtils.getUsername());
    }

    @Override
    @UiThread
    public void updateSuccess(String bean) {
        shortTip(R.string.tip_set_complete);
        SpUtils.setUsername(userName);
        BaseNotification.newInstance().postNotificationName(
                NotificationConstant.updateUsernameSuccess, userName);
        finish();
    }

    @Override
    public void updateFail(int code, String msg) {
        shortTip(R.string.tip_set_fail);
    }

    @Override
    public void onClick(View v) {
        userName = cetUserName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            shortTip(R.string.tip_input_username);
            return;
        }
        mPresenter.updateUserName(userName);
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
