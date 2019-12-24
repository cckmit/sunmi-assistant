package sunmi.common.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.commonlibrary.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

/**
 * 用户协议，隐私
 */
@EActivity(resName = "activity_protocol")
public class ProtocolActivity extends BaseActivity {

    //用户协议 https://wifi.cdn.sunmi.com/Privacy/user_sunmi.html
    public final static String PROTOCOL_USER = "https://wifi.cdn.sunmi.com/Privacy/user_sunmi.html";
    //用户协议英文
    public final static String PROTOCOL_USER_ENGLISH = "https://wifi.cdn.sunmi.com/Privacy/user_sunmi_english.html";
    //隐私协议
    public final static String PROTOCOL_PRIVATE = "https://wifi.cdn.sunmi.com/Privacy/private_sunmi.html";
    //隐私协议英文
    public final static String PROTOCOL_PRIVATE_ENGLISH = "https://wifi.cdn.sunmi.com/Privacy/private_sunmi_english.html";
    //协议管理
    public final static String PROTOCOL_ARGEEMENT = "";

    //本地用户协议
    public final static String LOCAL_PROTOCOL_USER = "file:///android_asset/user_sunmi.html";
    //本地隐私协议
    public final static String LOCAL_PROTOCOL_PRIVATE = "file:///android_asset/private_sunmi.html";
    //本地用户协议英文
    public final static String LOCAL_PROTOCOL_USER_ENGLISH = "file:///android_asset/user_sunmi_english.html";
    //本地隐私协议英文
    public final static String LOCAL_PROTOCOL_PRIVATE_ENGLISH = "file:///android_asset/private_sunmi_english.html";

    //微信
    public final static String WX_AUTH_HELP = "https://webapi.wap.sunmi.com/webapi/wap/app/static/wechat/index.html";
    //平台数据协议
    public final static String AUTH_PLATFORM = "file:///android_asset/auth_merchant.html";
    public static final int USER_PROTOCOL = 0;
    public static final int USER_PRIVATE = 1;
    public static final int USER_AP_PROTOCOL = 2;
    public static final int USER_AP_PRIVATE = 3;
    public static final int USER_WX_HELP = 4;
    public static final int USER_AUTH_PLATFORM = 5;
    public static final int USER_AGREEMENT = 6;

    @ViewById(resName = "wv_protocol")
    SMWebView webView;
    @ViewById(resName = "rl_exception")
    RelativeLayout rlException;
    @Extra
    int protocolType;

    private CountDownTimer countDownTimer;
    private long timeout = 5000;//超时时间
    private boolean loadFail;

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
        } else if (protocolType == USER_WX_HELP) {
            loadWebView(WX_AUTH_HELP);
        } else if (protocolType == USER_AUTH_PLATFORM) {//获取平台授权协议
            loadWebView(AUTH_PLATFORM);
        } else if (protocolType == USER_AGREEMENT) {//协议管理
            loadWebView(PROTOCOL_ARGEEMENT);
        }
    }

    /**
     * 本地协议
     */
    private void localHtml() {
        if (protocolType == USER_PROTOCOL) { //app注册协议
            webView.loadUrl(CommonHelper.isChinese() ? LOCAL_PROTOCOL_USER : LOCAL_PROTOCOL_USER_ENGLISH);
        } else if (protocolType == USER_PRIVATE) {
            webView.loadUrl(CommonHelper.isChinese() ? LOCAL_PROTOCOL_PRIVATE : LOCAL_PROTOCOL_PRIVATE_ENGLISH);
        } else if (protocolType == USER_AP_PROTOCOL) { //快速配置路由器协议
            webView.loadUrl(CommonHelper.isChinese() ? LOCAL_PROTOCOL_USER : LOCAL_PROTOCOL_USER_ENGLISH);
        } else if (protocolType == USER_AP_PRIVATE) {
            webView.loadUrl(CommonHelper.isChinese() ? LOCAL_PROTOCOL_PRIVATE : LOCAL_PROTOCOL_PRIVATE_ENGLISH);
        } else if (protocolType == USER_WX_HELP) {
            webView.loadUrl(WX_AUTH_HELP);
        } else if (protocolType == USER_AUTH_PLATFORM) {//获取平台授权协议
            webView.loadUrl(AUTH_PLATFORM);
        }
    }

    private void setView(boolean isShow) {
        if (isShow) {
            webView.setVisibility(View.VISIBLE);
            rlException.setVisibility(View.GONE);
        } else {
            webView.setVisibility(View.GONE);
            rlException.setVisibility(View.VISIBLE);
        }
    }

    @Click(resName = "btnImage")
    public void onClick(View v) {
        closeTimer();
        finish();
        this.overridePendingTransition(0, R.anim.activity_close_up_down);
    }

    @Click(resName = "btn_try")
    public void tryClick(View v) {
        setView(true);
        initNormal();
    }

    private void startTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(timeout, timeout) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    localHtml();
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
        webView.setWebViewClient(new SMWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                startTimer();
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadingDialog();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadingDialog();
                closeTimer();
            }

            @Override
            protected void receiverError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (!loadFail) {
                    loadFail = true;
                    hideLoadingDialog();
                    localHtml();
                }
                if (request.isForMainFrame()) {
                    if (protocolType == USER_AGREEMENT) {//协议管理
                        view.loadUrl("about:blank");// 避免出现默认的错误界面
                        setView(false);
                    }
                }
            }
        });
    }

}
