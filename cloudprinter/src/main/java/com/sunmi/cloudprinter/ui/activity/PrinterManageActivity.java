package com.sunmi.cloudprinter.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sunmi.cloudprinter.bean.PrinterJSCall;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.webview.AndroidBug5497Workaround;
import sunmi.common.view.webview.SMWebChromeClient;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;
import sunmi.common.view.webview.SsConstants;

@EActivity(resName = "activity_printer_manage")
public class PrinterManageActivity extends BaseActivity implements SMWebChromeClient.Callback {

    private static final int timeout = 15_000;

    @ViewById(resName = "webView")
    SMWebView webView;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;

    @Extra
    String sn;
    @Extra
    String userId;
    @Extra
    String shopId;
    @Extra
    int channelId;

    private boolean hasSendInfo = false;
    private CountDownTimer countDownTimer;
    private SMWebChromeClient webChrome;

    @AfterViews
    protected void init() {
        AndroidBug5497Workaround.assistActivity(this, true);
        StatusBarUtils.setStatusBarFullTransparent(this);//状态栏
        initWebView();
        webView.loadUrl(CommonConfig.SERVICE_H5_URL +
                "cloudPrinter/index?topPadding=" + Utils.getWebViewStatusBarHeight(context));
//        webView.loadUrl(PrinterConfig.IOT_H5_URL);
        startTimer();
    }

    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(timeout, timeout) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    loadError();
                }
            };
        }
        countDownTimer.start();
    }

    private void closeTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Click(resName = "img_back")
    public void backClick() {
        onBackPressed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        final WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);//设置DOM Storage缓存
        webSettings.setDatabaseEnabled(true);//设置可使用数据库
        webSettings.setJavaScriptEnabled(true);//支持js脚本
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setSupportZoom(false);//支持缩放
        webSettings.setBuiltInZoomControls(false);//支持缩放
        webSettings.setSupportMultipleWindows(false);//多窗口
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);//支持自动加载图片N
        webSettings.setGeolocationEnabled(true);//启用地理定位
        webSettings.setAllowFileAccessFromFileURLs(true);//使用允许访问文件的urls
        webSettings.setAllowUniversalAccessFromFileURLs(true);//使用允许访问文件的urls
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 可以运行JavaScript
        PrinterJSCall jsCall = new PrinterJSCall(this, webView, userId, shopId, sn, channelId);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
//        webView.addJavascriptInterface(baseJsCall, SsConstants.JS_INTERFACE_NAME);
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });
        webChrome = new SMWebChromeClient(this);
        webChrome.setCallback(this);
        //允许第三方访问Cookie
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.setWebChromeClient(webChrome);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
                if (!hasSendInfo) {
                    try {
                        JSONObject userInfo = new JSONObject()
                                .put("userId", userId)
                                .put("merchantId", shopId)
                                .put("msn", sn)
                                .put("channelId", channelId)
                                .put("token", SpUtils.getStoreToken());
                        String params = new JSONObject()
                                .put("userInfo", userInfo)
                                .toString();
                        webView.evaluateJavascript("javascript:getDataFromApp('" + params + "')", value -> {
                        });
                        hasSendInfo = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
//                loadError();
                LogCat.e(TAG, "receiverError 111111" + " networkError");
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    //销毁Webview 防止内存溢出
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();

            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        networkError.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.reload();
        startTimer();
    }

    @UiThread
    protected void loadError() {
        closeTimer();
        webView.setVisibility(View.GONE);
        networkError.setVisibility(View.VISIBLE);
        titleBar.setVisibility(View.VISIBLE);
        hideLoadingDialog();
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress < 100) {
            showLoadingDialog();
        } else {
            hideLoadingDialog();
            closeTimer();
        }
    }

    @Override
    public void onProgressComplete() {

    }

    @Override
    public void onReceivedTitle(String title) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (webChrome != null) {
            webChrome.uploadImage(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (webChrome != null) {
            webChrome.onPermissionResult(requestCode, grantResults);
        }
    }
}
