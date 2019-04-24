package com.sunmi.sunmiservice;

import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * Description:
 * Created by bruce on 2019/1/23.
 */
@EActivity(resName = "activity_webview_mall")
public class WebViewSunmiMallActivity extends BaseActivity
        implements H5FaceWebChromeClient.Callback {

    @ViewById(resName = "tv_status_height")
    TextView tvStatusHeight;//状态栏高度
    @ViewById(resName = "main_web_view")
    SMWebView webView;
    @ViewById(resName = "btnTryAgain")
    Button btnTryAgain;
    @ViewById(resName = "rlNetException")
    RelativeLayout rlNetException;

    @Extra
    String url;

    @AfterViews
    protected void init() {
        setStatusHeight();
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initWebView();
        webView.loadUrl(url);
    }

    private void setStatusHeight() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvStatusHeight.getLayoutParams();
        params.height = getStatusBarHeight();
        tvStatusHeight.setLayoutParams(params);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        LogCat.e(TAG, "getStatusBarHeight=" + result);
        return result;
    }

    private void initWebView() {
        final WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setDefaultTextEncodingName("utf-8");
        webSetting.setSupportZoom(false);
        webSetting.setDomStorageEnabled(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSetting.setBlockNetworkImage(true);//同步请求图片
        webSetting.setAllowFileAccess(true);// 设置允许访问文件数据
        webSetting.setLoadWithOverviewMode(true);

        JSCall jsCall = new JSCall(this, webView);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        //webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new H5FaceWebChromeClient(this, this));
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            protected void receiverError(final WebView view, WebResourceRequest request, WebResourceError error) {
                LogCat.e("TAG", "onReceivedError，error = " + error);
                webView.setVisibility(View.GONE);
                rlNetException.setVisibility(View.VISIBLE);
                btnTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetworkUtils.isNetworkAvailable(WebViewSunmiMallActivity.this)) {
                            shortTip(getString(R.string.str_check_net));
                            return;
                        }
                        rlNetException.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        view.clearCache(true);
                        view.clearHistory();
                        webView.loadUrl(url);
                    }
                });
            }
        });
    }


    @Override
    public void onProgressChanged(int progress) {

    }

    @Override
    public void onProgressComplete() {

    }

    @Override
    public void onReceivedTitle(String title) {
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
}
