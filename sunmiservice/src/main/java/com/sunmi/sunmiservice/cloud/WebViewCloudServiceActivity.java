package com.sunmi.sunmiservice.cloud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.alipay.sdk.app.PayTask;
import com.sunmi.sunmiservice.JSCall;
import com.sunmi.sunmiservice.R;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.dialog.CommonDialog;
import sunmi.common.view.webview.AndroidBug5497Workaround;
import sunmi.common.view.webview.SMWebChromeClient;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;
import sunmi.common.view.webview.SsConstants;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-25.
 */
@EActivity(resName = "activity_webview_cloud")
public class WebViewCloudServiceActivity extends BaseActivity
        implements SMWebChromeClient.Callback, SMWebChromeClient.CustomViewListener {

    private static final String URL_PARAM_JOINER = "?";
    private final static int timeout = 15_000;

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "webView")
    SMWebView webView;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @ViewById(resName = "flVideoContainer")
    FrameLayout flVideoContainer;

    @Extra
    String mUrl;
    @Extra
    String params;
    @Extra
    boolean showTitleBar;

    private boolean hasSendDeviceInfo = false;
    private CountDownTimer countDownTimer;
    private int progress;

    /**
     * 路由启动Activity
     */
    @RouterAnno(
            path = RouterConfig.SunmiService.WEB_VIEW_CLOUD
    )
    public static Intent start(@NonNull RouterRequest request) {
        return new Intent(request.getRawContext(), WebViewCloudServiceActivity_.class);
    }

    @AfterViews
    protected void init() {
        AndroidBug5497Workaround.assistActivity(this, true);
        StatusBarUtils.setStatusBarFullTransparent(this);//状态栏
        if (showTitleBar) {
            titleBar.setVisibility(View.VISIBLE);
        }
        initWebView();
        StringBuilder sb = new StringBuilder(mUrl);
        if (mUrl.contains(URL_PARAM_JOINER)) {
            sb.append("&topPadding=");
        } else {
            sb.append("?topPadding=");
        }
        sb.append(Utils.getWebViewStatusBarHeight(context));
        webView.loadUrl(sb.toString());
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
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭webview中缓存
        // 可以运行JavaScript
        JSCall jsCall = new JSCall(this, webView);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        SMWebChromeClient webChrome = new SMWebChromeClient(this);
        webChrome.setCallback(this);
        webChrome.setCustomViewListener(this);
        webView.setWebChromeClient(webChrome);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //微信支付
                if (url.startsWith("weixin://wap/pay?")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        view.goBack();
                        shortTip(R.string.tip_wechat_not_installed);
                    }
                    return true;
                } else if (url.startsWith("https://wx.tenpay.com")) {
                    //H5微信支付要用，不然说"商家参数格式有误"
                    Map<String, String> extraHeaders = new HashMap<>();
                    extraHeaders.put("Referer", mUrl);//商户申请H5时提交的授权域名
                    view.loadUrl(url, extraHeaders);
                    return true;
                } else if (url.startsWith("intent://")) {
                    try {
                        startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("dingtalk://dingtalkclient/page/link")) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        try {
                            new CommonDialog.Builder(context)
                                    .setTitle(R.string.dialog_title_install_dingding)
                                    .setConfirmButton(R.string.str_install_now, (dialog, which) -> {
                                        startActivity(new Intent("android.intent.action.VIEW",
                                                Uri.parse("https://www.dingtalk.com/android/d/lang=zh_CN")));
                                    }).setCancelButton(R.string.sm_cancel).create().show();
//                            WebViewActivity_.intent(context).url(
//                                    URLDecoder.decode(url.substring(url.indexOf("?url=") + 5), "UTF-8")).start();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                    return true;
                }
                //支付宝
                final PayTask payTask = new PayTask(WebViewCloudServiceActivity.this);
                boolean isIntercepted = payTask.payInterceptorWithUrl(url, true, result -> {
                    final String url1 = result.getReturnUrl();
                    if (!TextUtils.isEmpty(url1)) {
                        runOnUiThread(() -> view.loadUrl(url1));
                    }
                    // 5000支付失败 6001重复请求 6002中途取消
                    if ("5000".equals(result.getResultCode()) || "6001".equals(result.getResultCode())
                            || "6002".equals(result.getResultCode())) {
                        runOnUiThread(() -> view.loadUrl(CommonConstants.H5_ORDER_MANAGE));
                    }
                });
                if (!isIntercepted) {
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true;
                    }
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
                if (!hasSendDeviceInfo) {
                    webView.evaluateJavascript("javascript:getDataFromApp('" + params + "')", value -> {
                    });
                    hasSendDeviceInfo = true;
                }
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    loadError();
                }
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

    @Override
    protected void onNewIntent(Intent intent) {
        hasSendDeviceInfo = false;
        if (webView != null) {
            webView.reload();
        }
        startTimer();
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        networkError.setVisibility(View.GONE);
        setTitleBarVisibility(View.GONE);
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
            webView.reload();
        }
        startTimer();
    }

    @UiThread
    protected void loadError() {
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }
        networkError.setVisibility(View.VISIBLE);
        setTitleBarVisibility(View.VISIBLE);
        hasSendDeviceInfo = false;
        hideLoadingDialog();
        closeTimer();
    }

    private void setTitleBarVisibility(int visibility) {
        if (showTitleBar) {
            return;
        }
        titleBar.setVisibility(visibility);
    }

    @Override
    public void onProgressChanged(int progress) {
        this.progress = progress;
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
        if (showTitleBar) {
            titleBar.setAppTitle(title);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.isShown() && progress >= 100) {
            webView.evaluateJavascript("javascript:emitPageBack()", value -> {
            });
            return;
        }
        super.onBackPressed();
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

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        fullScreen();

        webView.setVisibility(View.GONE);
        flVideoContainer.setVisibility(View.VISIBLE);
        flVideoContainer.addView(view);
    }

    @Override
    public void onHideCustomView() {
        fullScreen();

        webView.setVisibility(View.VISIBLE);
        flVideoContainer.setVisibility(View.GONE);
        flVideoContainer.removeAllViews();
    }

    private void fullScreen() {
        setRequestedOrientation(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}

