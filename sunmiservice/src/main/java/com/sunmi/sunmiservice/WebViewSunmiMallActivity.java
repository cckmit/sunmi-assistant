package com.sunmi.sunmiservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * Description:商米商城
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
    String mUrl;

    @AfterViews
    protected void init() {
        setStatusHeight();
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initWebView();
        webView.loadUrl(mUrl);
    }

    private void setStatusHeight() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvStatusHeight.getLayoutParams();
        params.height = Utils.getStatusBarHeight(context);
        tvStatusHeight.setLayoutParams(params);
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

        JSCall jsCall = new JSCall(this, webView);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
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
                        webView.loadUrl(mUrl);
                    }
                });
            }

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
                final PayTask payTask = new PayTask(WebViewSunmiMallActivity.this);
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
        });
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress < 100)
            showLoadingDialog();
        else
            hideLoadingDialog();
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
