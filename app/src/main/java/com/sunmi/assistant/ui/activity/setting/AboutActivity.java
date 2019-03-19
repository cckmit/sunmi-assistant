package com.sunmi.assistant.ui.activity.setting;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.sunmi.apmanager.config.AppConfig;
import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.ui.activity.ProtocolActivity;
import com.sunmi.apmanager.update.AppUpdate;
import com.sunmi.apmanager.utils.BundleUtils;
import com.sunmi.apmanager.utils.NetConnectUtils;
import com.sunmi.assistant.R;
import com.zhy.http.okhttp.callback.StringCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;
import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * 关于
 */
@EActivity(R.layout.activity_setting_about)
public class AboutActivity extends BaseActivity {

    @ViewById(R.id.tvVersion)
    TextView tvVersion;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this,
                StatusBarUtils.TYPE_DARK);//状态栏
        tvVersion.setText(getString(R.string.str_version, CommonHelper.getAppVersionName(this)));
    }

    @Click({R.id.rlVersion, R.id.rlUserProtocol, R.id.rlUserPrivate})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlVersion:
//                Uri uri = Uri.parse("https://sj.qq.com/myapp/detail.htm?apkName=com.sunmi.assistant");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
                checkUpdate();
                break;
            case R.id.rlUserProtocol://用户协议
                openActivity(this, ProtocolActivity.class, BundleUtils.protocol(AppConfig.USER_PROTOCOL), false);
                overridePendingTransition(R.anim.activity_open_down_up, 0);
                break;
            case R.id.rlUserPrivate://隐私协议
                openActivity(this, ProtocolActivity.class, BundleUtils.protocol(AppConfig.USER_PRIVATE), false);
                overridePendingTransition(R.anim.activity_open_down_up, 0);
                break;
        }
    }

    private void checkUpdate() {
        CloudApi.checkUpgrade(new StringCallback() {
            @Override
            public void onError(Call call, Response response, Exception e, int id) {
                NetConnectUtils.isNetConnected(AboutActivity.this);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("code") && jsonObject.getInt("code") == 1) {
                            JSONObject object = (JSONObject) jsonObject.getJSONArray("data").opt(0);
                            if (object.has("has_new_version")) {
                                // 是否需要升级 0-否 1-是
                                int needUpdate = object.getInt("has_new_version");
                                if (needUpdate == 1) {
                                    String url = object.getString("url");
                                    forceUpdate(url);
                                    return;
                                } else {
                                    shortTip(getString(R.string.tip_now_new_version));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @UiThread
    void forceUpdate(String url) {
        getUpgradeDialog(url).show();
    }

    private CommonDialog getUpgradeDialog(final String url) {
        CommonDialog commonDialog = new CommonDialog.Builder(this)
                .setTitle(R.string.tip_title_upgrade)
                .setMessage(R.string.tip_message_upgrade_set)
                .setConfirmButton(R.string.go_upgrade, (dialog, which) -> AppUpdate.versionUpdate((Activity) context, url))
                .setCancelButton(R.string.str_cancel)
                .create();
        commonDialog.showWithOutTouchable(false);
        return commonDialog;
    }

}
