package sunmi.common.view.activity;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.webview.BaseJSCall;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;
import sunmi.common.view.webview.SsConstants;

/**
 * 用户协议，隐私
 */
@EActivity(resName = "activity_protocol")
public class ProtocolActivity extends BaseActivity {

    //用户协议 https://wifi.cdn.sunmi.com/Privacy/user_sunmi.html
    public final static String PROTOCOL_USER = CommonConfig.SERVICE_H5_URL + "privacy/:zh-cn/user";
    //用户协议英文
    public final static String PROTOCOL_USER_ENGLISH = "https://wifi.cdn.sunmi.com/Privacy/user_sunmi_english.html";
    //隐私协议
    public final static String PROTOCOL_PRIVATE = CommonConfig.SERVICE_H5_URL + "privacy/:zh-cn/private";
    //隐私协议英文
    public final static String PROTOCOL_PRIVATE_ENGLISH = "https://wifi.cdn.sunmi.com/Privacy/private_sunmi_english.html";
    //商米智能摄像机隐私政策
    public final static String PROTOCOL_IPC = CommonConfig.SERVICE_H5_URL + "privacy/:zh-cn/ipcPrivacy";

    public static final int USER_PROTOCOL = 0;
    public static final int USER_PRIVATE = 1;
    public static final int USER_AP_PROTOCOL = 2;
    public static final int USER_AP_PRIVATE = 3;
    public static final int USER_IPC_PROTOCOL = 4;

    @ViewById(resName = "wv_protocol")
    SMWebView webView;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @Extra
    int protocolType;

    private CountDownTimer countDownTimer;
    private long timeout = 5000;//超时时间

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);//状态栏
        initNormal();
        // 设置标题
        WebChromeClient webChrome = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    showLoadingDialog();
                } else {
                    hideLoadingDialog();
                    closeTimer();
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        // 设置setWebChromeClient对象
        webView.setWebChromeClient(webChrome);
    }

    private void initNormal() {
        if (protocolType == USER_PROTOCOL) { //app注册协议
            loadWebView(CommonHelper.isChinese() ? PROTOCOL_USER : PROTOCOL_USER_ENGLISH);
        } else if (protocolType == USER_PRIVATE) {
            loadWebView(CommonHelper.isChinese() ? PROTOCOL_PRIVATE : PROTOCOL_PRIVATE_ENGLISH);
        } else if (protocolType == USER_AP_PROTOCOL) { //快速配置路由器协议
            loadWebView(CommonHelper.isChinese() ? PROTOCOL_USER : PROTOCOL_USER_ENGLISH);
        } else if (protocolType == USER_AP_PRIVATE) {
            loadWebView(CommonHelper.isChinese() ? PROTOCOL_PRIVATE : PROTOCOL_PRIVATE_ENGLISH);
        } else if (protocolType == USER_IPC_PROTOCOL) {
            loadWebView(PROTOCOL_IPC);
        }
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

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(String url) {
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
        startTimer();
        // 可以运行JavaScript
        WebSettings webSetting = webView.getSettings();
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        //自适应屏幕
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setLoadWithOverviewMode(true);
        webView.getSettings().setMixedContentMode(
                WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        // 不用启动客户端的浏览器来加载未加载出来的数据
        BaseJSCall jsCall = new BaseJSCall(this, webView);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        webView.setWebViewClient(new SMWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                loadError();
            }
        });
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        networkError.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.reload();
        startTimer();
    }

    @UiThread
    protected void loadError() {
        webView.setVisibility(View.GONE);
        networkError.setVisibility(View.VISIBLE);
        hideLoadingDialog();
        closeTimer();
    }

}
