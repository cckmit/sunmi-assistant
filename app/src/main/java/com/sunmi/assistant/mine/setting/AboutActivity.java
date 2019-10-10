package com.sunmi.assistant.mine.setting;

import android.app.Activity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.apmanager.rpc.cloud.CloudApi;
import com.sunmi.apmanager.update.AppUpdate;
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
import sunmi.common.utils.ViewUtils;
import sunmi.common.view.dialog.CommonDialog;

/**
 * 关于
 * @author yangshijie
 */
@EActivity(R.layout.activity_setting_about)
public class AboutActivity extends BaseActivity {

    @ViewById(R.id.tvVersion)
    TextView tvVersion;
    @ViewById(R.id.ctv_privacy)
    CheckedTextView ctvPrivacy;
    @ViewById(R.id.rlVersion)
    RelativeLayout rlVersion;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        ViewUtils.setPrivacy(this, ctvPrivacy, R.color.colorOrange, false);
        tvVersion.setText(getString(R.string.str_version, CommonHelper.getAppVersionName(this)));
        if (!CommonHelper.isGooglePlay()){
            rlVersion.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.rlVersion)
    public void onClick(View v) {
        checkUpdate();
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
                .setCancelButton(R.string.sm_cancel)
                .create();
        commonDialog.showWithOutTouchable(false);
        return commonDialog;
    }

}
