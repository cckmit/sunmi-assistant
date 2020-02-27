package com.sunmi.sunmiservice.cloud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
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
public class WebViewCloudServiceActivity extends BaseActivity implements SMWebChromeClient.Callback {

    private static final String URL_PARAM_JOINER = "?";

    private final int timeout = 15_000;
    @ViewById(resName = "webView")
    SMWebView webView;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;

    @Extra
    String mUrl;
    @Extra
    String params;

    private boolean hasSendDeviceInfo = false;
    private CountDownTimer countDownTimer;
    private int progress;
    private SMWebChromeClient webChrome;

    /**
     * 路由启动Activity
     *
     * @param request
     * @return
     */
    @RouterAnno(
            path = RouterConfig.SunmiService.WEB_VIEW_CLOUD
    )
    public static Intent start(@NonNull RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), WebViewCloudServiceActivity_.class);
        return intent;
    }

    @AfterViews
    protected void init() {
        AndroidBug5497Workaround.assistActivity(this, true);
        StatusBarUtils.setStatusBarFullTransparent(this);//状态栏
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
        webChrome = new SMWebChromeClient(this);
        webChrome.setCallback(this);
        webView.setWebChromeClient(webChrome);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                //微信支付
                if (url.startsWith("weixin://wap/pay?")) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        view.goBack();
                        shortTip(R.string.tip_wechat_not_installed);
                    }
                    return true;
                } else if (url.startsWith("https://wx.tenpay.com")) {
                    //H5微信支付要用，不然说"商家参数格式有误"
                    Map<String, String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("Referer", mUrl);//商户申请H5时提交的授权域名
                    view.loadUrl(url, extraHeaders);
                    return true;
                }
                //支付宝
                final PayTask payTask = new PayTask(WebViewCloudServiceActivity.this);
                boolean isIntercepted = payTask.payInterceptorWithUrl(url, true, new H5PayCallback() {
                    @Override
                    public void onPayResult(H5PayResultModel result) {
                        final String url = result.getReturnUrl();
                        if (!TextUtils.isEmpty(url)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl(url);
                                }
                            });
                        }
                        // 5000支付失败 6001重复请求 6002中途取消
                        if ("5000".equals(result.getResultCode()) || "6001".equals(result.getResultCode())
                                || "6002".equals(result.getResultCode())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl(CommonConstants.H5_ORDER_MANAGE);
                                }
                            });
                        }
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
                loadError();
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
        titleBar.setVisibility(View.GONE);
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
        titleBar.setVisibility(View.VISIBLE);
        hasSendDeviceInfo = false;
        hideLoadingDialog();
        closeTimer();
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

}

