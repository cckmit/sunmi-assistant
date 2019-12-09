package com.sunmi.sunmiservice.cloud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sunmi.sunmiservice.H5FaceWebChromeClient;
import com.sunmi.sunmiservice.JSCall;
import com.sunmi.sunmiservice.SsConstants;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.TitleBarView;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-06.
 */
@EActivity(resName = "activity_webview_cloud")
public class WebViewCashServiceActivity extends BaseActivity implements H5FaceWebChromeClient.Callback {

    private final int timeout = 15_000;
    @ViewById(resName = "webView")
    SMWebView webView;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;

    @Extra
    String mUrl;

    private boolean hasSendDeviceInfo = false;
    private CountDownTimer countDownTimer;

    /**
     * 路由启动Activity
     *
     * @param request
     * @return
     */
    @RouterAnno(
            path = RouterConfig.SunmiService.WEB_VIEW_CASH
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), WebViewCashServiceActivity_.class);
        return intent;
    }


    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);//状态栏
        initWebView();
        webView.loadUrl(mUrl + Utils.getWebViewStatusBarHeight(context));
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
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭webview中缓存
        webSettings.setAllowFileAccess(true);//设置可以访问文件
        webSettings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);//支持自动加载图片N
        webSettings.setGeolocationEnabled(true);//启用地理定位
        webSettings.setAllowFileAccessFromFileURLs(true);//使用允许访问文件的urls
        webSettings.setAllowUniversalAccessFromFileURLs(true);//使用允许访问文件的urls
        // 可以运行JavaScript
        JSCall jsCall = new JSCall(this, webView);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        webView.setWebChromeClient(new H5FaceWebChromeClient(this, this));
        // 不用启动客户端的浏览器来加载未加载出来的数据
        webView.setWebViewClient(new SMWebViewClient(this) {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                view.loadUrl(url);
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
                    closeTimer();
                    try {
                        String params = new JSONObject()
                                .put("token", SpUtils.getStoreToken())
                                .put("company_id", SpUtils.getCompanyId())
                                .put("shop_id", SpUtils.getShopId())
                                .put("shop_name", SpUtils.getShopName()).toString();
                        webView.evaluateJavascript("javascript:getShopInfo('" + params + "')", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                            }
                        });
                        hasSendDeviceInfo = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                // loadError();
                LogCat.e(TAG, "receiverError 111111" + " networkError");
            }

        });
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        networkError.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        initWebView();
        webView.loadUrl(mUrl + Utils.getWebViewStatusBarHeight(context));
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
    public void onBackPressed() {
        if (webView == null) {
            return;
        }
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        webView.clearCache(true);
        super.onBackPressed();
    }
}
