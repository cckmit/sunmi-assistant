package com.sunmi.assistant.ui.activity.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.constant.Constants;
import com.sunmi.apmanager.model.LoginDataBean;
import sunmi.common.rpc.http.RpcCallback;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.rpc.sso.SSOApi;
import com.sunmi.apmanager.ui.view.MergeDialog;
import com.sunmi.apmanager.utils.CommonUtils;
import com.sunmi.apmanager.utils.DialogUtils;
import com.sunmi.apmanager.utils.HelpUtils;
import com.sunmi.apmanager.utils.SomeMonitorEditText;
import com.sunmi.apmanager.utils.SpUtils;
import com.sunmi.apmanager.utils.UDPUtils;
import com.sunmi.assistant.R;
import com.sunmi.assistant.ui.activity.MainActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.PermissionUtils;
import sunmi.common.utils.RegexUtils;
import sunmi.common.view.ClearableEditText;
import sunmi.common.view.dialog.CommonDialog;

/**
 * 登录
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewById(R.id.etUser)
    ClearableEditText etUser;
    @ViewById(R.id.etPassword)
    ClearableEditText etPassword;
    @ViewById(R.id.tvSMSLogin)
    TextView tvSMSLogin;
    @ViewById(R.id.tvForgetPassword)
    TextView tvForgetPassword;
    @ViewById(R.id.passwordIsVisible)
    ImageButton passwordIsVisible;
    @ViewById(R.id.btnLogin)
    Button btnLogin;
    @ViewById(R.id.btnRegister)
    Button btnRegister;
    @ViewById(R.id.btnFixPassword)
    Button btnFixPassword;
    @ViewById(R.id.btnLogout)
    Button btnLogout;

    private boolean psdIsVisible;//密码是否可见
    private String mobile;

    private Handler mHandler;
    private CommonDialog kickedDialog;

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.WHAT_UDP://切换wifi显示搜索的路由器
                    try {
                        JSONObject object = new JSONObject((String) msg.obj);
                        String name = object.getString("name");
                        String deviceId = object.getString("deviceid");
                        int factory = object.getInt("factory");//是否已经设置0已初始 1未初始设置
                        if (!TextUtils.isEmpty(name) && !isFinishing() && factory == 1) {
                            DialogUtils.quickConfigDialog(LoginActivity.this, name, deviceId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    };

    static class WeakRefHandler extends Handler {
        private WeakReference<Callback> mWeakReference;

        public WeakRefHandler(Callback callback) {
            mWeakReference = new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mWeakReference != null && mWeakReference.get() != null) {
                Callback callback = mWeakReference.get();
                callback.handleMessage(msg);
            }
        }
    }

    @AfterViews
    protected void init() {
        PermissionUtils.checkPermissionActivity(this);//手机权限
        HelpUtils.setStatusBarFullTransparent(this);//透明标题栏
        etUser.setClearIcon(R.mipmap.ic_edit_delete_white);
        etPassword.setClearIcon(R.mipmap.ic_edit_delete_white);
        String mobile = SpUtils.getMobile();
        mHandler = new WeakRefHandler(mCallback);
        new SomeMonitorEditText().setMonitorEditText(btnLogin, etUser, etPassword);//button 是否可点击
        initData();
        if (!TextUtils.isEmpty(mobile)) {
            etUser.setText(mobile);
        } else if (!TextUtils.isEmpty(SpUtils.getEmail())) {
            etUser.setText(SpUtils.getEmail());
        }

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String extra = bundle.getString("reason");
            if (TextUtils.equals("1", extra)) {
                if (kickedDialog != null && kickedDialog.isShowing()) return;
                kickedDialog = new CommonDialog.Builder(context)
                        .setTitle(R.string.tip_kick_off)
                        .setConfirmButton(R.string.str_confirm).create();
                kickedDialog.show();
            } else if (TextUtils.equals("2", extra)) {
                shortTip(R.string.tip_password_changed_login);
            }
            if (!TextUtils.isEmpty(bundle.getString("mobile")))
                mobile = bundle.getString("mobile");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!TextUtils.isEmpty(bundle.getString("mobile"))) {
                mobile = bundle.getString("mobile");
                etUser.setText(mobile);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UDPUtils.UdpManual.initSearchRouter(mHandler, this);//搜索路由器
        HelpUtils.setSelectionEnd(etUser);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length <= 0) return;
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {//用户拒绝
            shortTip(R.string.tip_permission_ungranted);
        }
    }

    @Click({R.id.btnLogin, R.id.btnRegister, R.id.btnFixPassword, R.id.passwordIsVisible,
            R.id.btnLogout, R.id.tvForgetPassword, R.id.tvSMSLogin})
    public void onClick(View v) {
        mobile = etUser.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btnLogin: //密码登录
                if (!RegexUtils.isChinaPhone(mobile) && !RegexUtils.isEmail(mobile)) {
                    shortTip(R.string.tip_invalid_phone_number);
                    return;
                }
                if (!RegexUtils.isValidPassword(password)) {
                    shortTip(R.string.textView_tip_psd);
                    return;
                }
                userMerge(password);
                break;
            case R.id.btnRegister: //注册
                if (isFastClick(1500)) return;
                CommonUtils.trackCommonEvent(context, "register", "注册按钮", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "registerDuration",
                        "注册流程开始和结束时调用", Constants.EVENT_DURATION_REGISTER);
                RegisterActivity_.intent(context)
                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .start();
                break;
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
            case R.id.tvForgetPassword: //忘记密码
                if (isFastClick(1500)) return;
                CommonUtils.trackCommonEvent(context, "forgetPassword", "忘记密码按钮", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "retrievePasswordDuration",
                        "找回密码流程开始和结束", Constants.EVENT_DURATION_FORGET_PSW);
                RetrievePasswordActivity_.intent(context)
                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .start();
                break;
            case R.id.tvSMSLogin:  //短信登录
                if (isFastClick(1500)) return;
                CommonUtils.trackCommonEvent(context, "loginBySms", "短信验证码登录", Constants.EVENT_LOGIN);
                CommonUtils.trackDurationEventBegin(context, "quickLoginDuration",
                        "登录流程开始到结束", Constants.EVENT_DURATION_LOGIN_BY_SMS);
                SendSmsLoginActivity_.intent(context)
                        .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                        .start();
                break;
        }
    }

    //账号合并
    private void userMerge(final String password) {
        if (etUser.getText() == null) return;
        String user = etUser.getText().toString();  //email test: esyzim06497@chacuo.net
        if (RegexUtils.isChinaPhone(user) || RegexUtils.isEmail(user)) {
            SSOApi.checkUserName(user, new RpcCallback(context, false) {
                @Override
                public void onSuccess(int code, String msg, String data) {
                    userMergeSuccess(code, msg, data, password);
                }
            });
        }
    }

    private void userMergeSuccess(int code, String msg, String data, String
            password) {
        try {
            if (code == 1) {
                JSONObject object = new JSONObject(data);
                if (object.has("needMerge")) {
                    int needMerge = object.getInt("needMerge");//是否需要合并 0-否 1-是
                    String url = object.getString("url");
                    if (needMerge == 1) {
                        new MergeDialog(context, url).show();
                    } else {
                        login(mobile, password);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void login(String mobile, String password) {
        CommonUtils.trackCommonEvent(context, "login", "登录", Constants.EVENT_LOGIN);
        CloudApi.login(mobile, password, new RpcCallback(context) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (code == 1) {
                    LoginDataBean bean = new Gson().fromJson(data, LoginDataBean.class);
                    SpUtils.saveLoginInfo(bean);
                    gotoMainActivity();
                } else if (code == 201) {//用户名或密码错误
                    shortTip(R.string.textView_user_password_error);
                } else if (code == 3603) {
                    mobileNoRegister();//手机号未注册
                }
            }
        });
    }

    private void gotoMainActivity() {
        MainActivity_.intent(context).start();
        finish();
    }

    //手机号未注册
    private void mobileNoRegister() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CommonDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.tip_unregister)
                        .setCancelButton(R.string.str_cancel)
                        .setConfirmButton(R.string.str_register_now,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CommonUtils.trackCommonEvent(context, "loginUnRegisterDialogRegister",
                                                "登录_未注册弹框-立即注册", Constants.EVENT_LOGIN);
                                        RegisterActivity_.intent(context)
                                                .extra("mobile", RegexUtils.isChinaPhone(mobile) ? mobile : "")
                                                .start();
                                    }
                                }).create().show();
            }
        });
    }

    private Bundle getMobileBundle() {
        if (!RegexUtils.isChinaPhone(mobile)) {
            return new Bundle();
        }
        Bundle bundle = new Bundle();
        bundle.putString("mobile", mobile);
        return bundle;
    }

}
