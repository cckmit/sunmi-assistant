package com.sunmi.assistant.ui.activity.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.model.LoginDataBean;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.assistant.R;
import com.sunmi.assistant.data.SunmiStoreRemote;
import com.sunmi.assistant.data.response.CompanyInfoResp;
import com.sunmi.assistant.rpc.CloudCall;
import com.sunmi.assistant.ui.activity.merchant.AuthDialog;
import com.sunmi.assistant.ui.activity.merchant.SelectPlatformActivity_;
import com.sunmi.assistant.ui.activity.merchant.SelectStoreActivity_;
import com.sunmi.assistant.ui.activity.model.AuthStoreInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.cloud.SunmiStoreApi;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.http.RpcCallback;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.RegexUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.ClearableEditText;

/**
 * set password
 */
@EActivity(R.layout.activity_set_password)
public class SetPasswordActivity extends BaseActivity {

    @ViewById(R.id.llTitleLine)
    LinearLayout llTitleLine;
    @ViewById(R.id.et_password)
    ClearableEditText etPassword;
    @ViewById(R.id.passwordIsVisible)
    ImageButton passwordIsVisible;
    @ViewById(R.id.btnComplete)
    Button btnComplete;

    private int type;
    private String smsCode = "";//重置密码的时候传参
    private String mobile = "";
    private boolean psdIsVisible;//密码是否可见

    @AfterViews
    protected void init() {
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etPassword.setClearIcon(R.mipmap.ic_edit_delete_white);
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt(AppConfig.SET_PASSWORD);
        smsCode = bundle.getString(AppConfig.SET_PASSWORD_SMS);
        mobile = bundle.getString("mobile");

        if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
            llTitleLine.setVisibility(View.VISIBLE);
        } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
            llTitleLine.setVisibility(View.GONE);
        }
        //button是否可点击
        new SomeMonitorEditText().setMonitorEditText(btnComplete, etPassword);
        CommonUtils.trackDurationEventBegin(context, "registerPasswordDuration",
                "注册流程_设置密码_耗时", Constants.EVENT_DURATION_REGISTER_PSW);
    }

    @Click({R.id.passwordIsVisible, R.id.btnComplete})
    public void onClick(View v) {
        String password = etPassword.getText().toString();
        switch (v.getId()) {
            case R.id.passwordIsVisible: //密码是否可见
                if (psdIsVisible) {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //隐藏密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_light_hide);
                    psdIsVisible = false;
                } else {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance()); //显示密码
                    passwordIsVisible.setBackgroundResource(R.mipmap.ic_eye_light_open);
                    psdIsVisible = true;
                }
                break;
            case R.id.btnComplete:
                if (isFastClick(1500)) return;
                if (!RegexUtils.isValidPassword(password) || password.length() < 8) {
                    shortTip(R.string.tip_password_non_standard);
                    return;
                }
                trackFinish();
                if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
                    register(password);
                } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
                    reSetPassword(password);
                }
                break;
        }
    }

    //忘记密码后重置密码
    private void reSetPassword(String password) {
        CloudApi.resetPassword(mobile, password, smsCode, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    shortTip(R.string.textView_reset_password_success);
                    LoginActivity_.intent(context).start();
                } else {
                    shortTip(R.string.textView_reset_password_error);
                }
            }
        });
    }

    //注册成功后设置密码
    private void register(String password) {
        CloudApi.register(mobile, password, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    LoginDataBean bean = new Gson().fromJson(data, LoginDataBean.class);
                    try {
                        int shopId = new JSONObject(data).getJSONObject("create_shop").getJSONObject("data").getInt("shop_id");
                        LogCat.e(TAG, "shopId=" + shopId);
                        CommonUtils.saveLoginInfo(SetPasswordActivity.this, bean, shopId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    shortTip(R.string.register_success);
                    CommonUtils.trackDurationEventEnd(context, "registerPasswordDuration",
                            "注册流程_设置密码_耗时", Constants.EVENT_DURATION_REGISTER_PSW);
                    CommonUtils.trackDurationEventEnd(context, "registerDuration",
                            "注册流程开始和结束时调用", Constants.EVENT_DURATION_REGISTER);
                    //注册成功后直接登录
//                    gotoMainActivity();
                    getSsoToken();
                }
            }
        });
    }


    private void trackFinish() {
        if (type == AppConfig.SET_PASSWORD_REGISTER) {//注册
            CommonUtils.trackCommonEvent(context, "registerPassFinish",
                    "注册_设置密码_完成", Constants.EVENT_REGISTER);
        } else if (type == AppConfig.SET_PASSWORD_RESET) {//找回密码
            CommonUtils.trackCommonEvent(context, "forgetPwdPassFinish",
                    "找回密码_设置密码完成", Constants.EVENT_FORGET_PSW);
            CommonUtils.trackDurationEventEnd(context, "retrievePasswordDuration",
                    "找回密码流程开始和结束", Constants.EVENT_DURATION_FORGET_PSW);
        }
    }

    //获取ssotoken
    private void getSsoToken() {
        showLoadingDialog();
        SunmiStoreApi.getStoreToken(SpUtils.getUID(), SpUtils.getSsoToken(),
                SpUtils.getCompanyId() + "", new RetrofitCallback() {
                    @Override
                    public void onSuccess(int code, String msg, Object data) {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            SpUtils.setStoreToken(jsonObject.getString("store_token"));
                            SunmiStoreRetrofitClient.createInstance();//初始化retrofit
                            //MqttManager.getInstance().createEmqToken(true);//初始化ipc长连接
                            getCompanyInfo();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, Object data) {
                        hideLoadingDialog();
                    }
                });
    }

    //获取商户信息
    private void getCompanyInfo() {
        SunmiStoreRemote.get().getCompanyInfo(SpUtils.getCompanyId(), new RetrofitCallback<CompanyInfoResp>() {
            @Override
            public void onSuccess(int code, String msg, CompanyInfoResp data) {
                LogCat.e(TAG, "data getCompanyInfo=" + data.getCompany_name() + "," + data.getCompany_id());
                if (TextUtils.isEmpty(data.getCompany_name()))
                    SpUtils.setCompanyName(getString(R.string.str_mine_company));
                else
                    SpUtils.setCompanyName(data.getCompany_name());
                getSaasInfo();
            }

            @Override
            public void onFail(int code, String msg, CompanyInfoResp data) {
                hideLoadingDialog();
                SpUtils.setCompanyName(getString(R.string.str_mine_company));
                getSaasInfo();
            }
        });
    }

    //通过手机号获取saas信息
    private void getSaasInfo() {
        CloudCall.getSaasUserInfo(SpUtils.getMobile(), new RetrofitCallback() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                LogCat.e(TAG, "data onSuccess=" + data);
                hideLoadingDialog();
                AuthStoreInfo bean = new Gson().fromJson(data.toString(), AuthStoreInfo.class);
                getSaasData(bean.getSaas_user_info_list());
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e(TAG, "data onFail code=" + code + "," + msg);
                hideLoadingDialog();
            }
        });
    }

    private void getSaasData(List<AuthStoreInfo.SaasUserInfoListBean> list) {
        if (list.size() > 0) {  //匹配到平台数据
            StringBuilder saasName = new StringBuilder();
            for (AuthStoreInfo.SaasUserInfoListBean bean : list) {
                if (!saasName.toString().contains(bean.getSaas_name()))
                    saasName.append(bean.getSaas_name()).append(",");
            }
            new AuthDialog.Builder(SetPasswordActivity.this)
                    .setMessage(getString(R.string.str_dialog_auth_message, saasName.replace(saasName.length() - 1, saasName.length(), "")))
                    .setAllowButton((dialog, which) -> SelectStoreActivity_.intent(SetPasswordActivity.this)
                            .isBack(false)
                            .list((ArrayList) list)
                            .start())
                    .setCancelButton((dialog, which) -> {
                        GotoActivityUtils.gotoMainActivity(SetPasswordActivity.this);
                    })
                    .create().show();
        } else { //未匹配平台数据
            SelectPlatformActivity_.intent(SetPasswordActivity.this).start();
        }
    }
}
