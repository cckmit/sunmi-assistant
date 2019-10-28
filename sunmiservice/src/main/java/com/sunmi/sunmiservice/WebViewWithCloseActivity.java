package com.sunmi.sunmiservice;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * Description:
 * Created by bruce on 2019/1/23.
 */
@EActivity(resName = "activity_webview")
public class WebViewWithCloseActivity extends BaseActivity
        implements H5FaceWebChromeClient.Callback {

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "main_web_view")
    SMWebView webView;
    @ViewById(resName = "pb_web_view")
    ProgressBar pbWebView;

    @Extra
    String url;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initTitleBar();
        initWebView();
        webView.loadUrl(url);
    }

    /**
     * 标题栏初始化，是否带关闭
     */
    private void initTitleBar() {
        titleBar.setLeftImg2OnClickListener(R.mipmap.ic_pro_quit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.getRightImg().setVisibility(View.GONE);
    }

    private void initWebView() {
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {

            }
        });
        webView.setWebChromeClient(new H5FaceWebChromeClient(this, this));
        WBH5FaceVerifySDK.getInstance().setWebViewSettings(webView, getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (WBH5FaceVerifySDK.getInstance().receiveH5FaceVerifyResult(requestCode, resultCode, data))
            return;
    }

    @Override
    public void onProgressChanged(int progress) {
        pbWebView.setProgress(progress);
        if (progress == 100) {
            pbWebView.setVisibility(View.GONE);
        } else {
            pbWebView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProgressComplete() {

    }

    @Override
    public void onReceivedTitle(String title) {
        titleBar.setAppTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (webView == null) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        shortTip(grantResults[0] == PackageManager.PERMISSION_GRANTED
                ? R.string.tip_granted : R.string.tip_permission_ungranted);
    }

}
