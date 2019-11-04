package com.sunmi.sunmiservice.cloud;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.sunmi.sunmiservice.R;
import com.sunmi.sunmiservice.SsConstants;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.RouterConfig;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-25.
 */
@EActivity(resName = "activity_webview_cloud")
public class WebViewCloudServiceActivity extends BaseActivity {

    private final int timeout = 10_000;
    @ViewById(resName = "webView")
    SMWebView webView;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @Extra
    String mUrl;
    @Extra
    String deviceSn;
    private boolean hasSendDeviceInfo = false;
    private CountDownTimer countDownTimer;

    /**
     * 路由启动Activity
     *
     * @param request
     * @return
     */
    @RouterAnno(
            path = RouterConfig.SunmiService.WEB_VIEW_CLOUD
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), WebViewCloudServiceActivity_.class);
        return intent;
    }


    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);//状态栏
        ServiceJSCall jsCall = new ServiceJSCall(this);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        // 设置标题
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
        loadWebView(mUrl);
    }

    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(timeout, timeout) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    networkError.setVisibility(View.VISIBLE);
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
    private void loadWebView(String url) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
        // 可以运行JavaScript
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);// 设置允许访问文件数据
        //自适应屏幕
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小--关键点
        webView.getSettings().setLoadWithOverviewMode(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
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
                                    view.goBack();
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
                networkError.setVisibility(View.GONE);
                showLoadingDialog();
                startTimer();
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
                                .put("sn", deviceSn).toString();
                        webView.evaluateJavascript("javascript:getDeviceInfo('" + params + "')", new ValueCallback<String>() {
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
                hideLoadingDialog();
                closeTimer();
                networkError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler,
                                           SslError error) {
                hideLoadingDialog();
                super.onReceivedSslError(view, handler, error);
            }
        });
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        loadWebView(mUrl);
    }

    @TargetApi(23)
    private void showTag(WebResourceError error) {
        LogCat.e(TAG, "错误码：" + error.getErrorCode() + " 错误描述" + error.getDescription());
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

