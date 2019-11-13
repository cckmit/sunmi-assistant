package com.sunmi.sunmiservice.cloud;

import android.annotation.SuppressLint;
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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.sunmi.sunmiservice.H5FaceWebChromeClient;
import com.sunmi.sunmiservice.JSCall;
import com.sunmi.sunmiservice.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
 * @author linyuanpeng on 2019-10-25.
 */
@EActivity(resName = "activity_webview_cloud")
public class WebViewCloudServiceActivity extends BaseActivity implements H5FaceWebChromeClient.Callback {

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
    ArrayList<String> snList;
    private boolean hasSendDeviceInfo = false;
    private boolean beginLoading = false;
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
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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
                                .put("sn_list", snList).toString();
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
                // loadError();
                LogCat.e(TAG, "receiverError 111111" + " networkError");
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
        networkError.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        initWebView();
        webView.loadUrl(mUrl);
        startTimer();
    }

    @UiThread
    protected void loadError() {
        closeTimer();
        hideLoadingDialog();
        webView.setVisibility(View.GONE);
        networkError.setVisibility(View.VISIBLE);
        titleBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress < 100 && !beginLoading) {
            showLoadingDialog();
            closeTimer();
            beginLoading = true;
        } else {
            hideLoadingDialog();
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

