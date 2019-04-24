package com.sunmi.sunmiservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseFragment;
import sunmi.common.constant.CommonConstants;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.Utils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.webview.SMWebChromeClient;
import sunmi.common.view.webview.SMWebView;
import sunmi.common.view.webview.SMWebViewClient;

@EFragment(resName = "fragment_support")
public class SupportFragment extends BaseFragment
        implements SMWebChromeClient.Callback, SMWebView.OnScrollChangeListener,
        BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(resName = "wv_support")
    SMWebView webView;
    @ViewById(resName = "relativeTitleBg")
    RelativeLayout relativeTitleBg;
    @ViewById(resName = "tvTitle")
    TextView tvTitle;
    @ViewById(resName = "btnTryAgain")
    Button btnTryAgain;
    @ViewById(resName = "rlNetException")
    RelativeLayout rlNetException;
    @ViewById(resName = "bga_refresh")
    BGARefreshLayout mRefreshLayout;

    private IWXAPI api;// 第三方app和微信通信的openApi接口

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regToWx();
    }

    @AfterViews
    void init() {
        relativeTitleBg.setAlpha(0);
        initWebView();
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this); // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), false); // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder); // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项
        mRefreshLayout.setIsShowLoadingMoreView(false); // 设置正在加载更多时的文本
    }

    private void initWebView() {
        JSCall jsCall = new JSCall(mActivity, webView);
        jsCall.setApi(api);
        webView.addJavascriptInterface(jsCall, SsConstants.JS_INTERFACE_NAME);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        SMWebChromeClient webViewClient = new SMWebChromeClient(mActivity);
        webViewClient.setCallback(this);
        webView.setWebChromeClient(webViewClient);
        webView.setWebViewClient(new SMWebViewClient(mActivity) {
            @Override
            protected void receiverError(final WebView view, WebResourceRequest request, WebResourceError error) {
                LogCat.e("TAG", "onReceivedError，error = " + error);
                webView.setVisibility(View.GONE);
                rlNetException.setVisibility(View.VISIBLE);
                btnTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!NetworkUtils.isNetworkAvailable(mActivity)) {
                            shortTip(getString(R.string.str_check_net));
                            return;
                        }
                        rlNetException.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        view.clearCache(true);
                        view.clearHistory();
                        webView.loadUrl(getBaseUrl());//services
                    }
                });
            }
        });
        webView.setOnScrollChangeListener(this);
        webView.loadUrl(getBaseUrl());//services
    }

    private String getBaseUrl() {
        return SunmiServiceConfig.VALUE_ADDED_SERVICES + "?screenTop=" +
                CommonHelper.px2dp(mActivity, Utils.getStatusBarHeight(mActivity));
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(mActivity, SunmiServiceConfig.WECHAT_APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(SunmiServiceConfig.WECHAT_APP_ID);
    }

    /**
     * app主动发送消息给微信，发送完成之后会切回到第三方app界面
     *
     * @param text
     * @param imgUrl
     */
    private void sendWXReq(String text, String imgUrl) {
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());  //transaction字段用与唯一标示一个请求
        req.message = msg;
        if (api.getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        //调用api接口，发送数据到微信
        api.sendReq(req);
    }

    /**
     * 微信向第三方app请求数据，第三方app回应数据之后会切回到微信界面
     *
     * @param text
     */
    private void sendWXResp(String text, Bundle bundle) {
        // 初始化一个 WXTextObject 对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(textObj);
        msg.description = text;

        // 构造一个Resp
        GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
        // 将req的transaction设置到resp对象中，其中bundle为微信传递过来的Intent所带的内容，通过getExtras()方法获取
        resp.transaction = new GetMessageFromWX.Req(bundle).transaction;
        resp.message = msg;

        //调用api接口，发送数据到微信
        api.sendResp(resp);
    }

    @Override
    public void onProgressChanged(int progress) {
        if (progress < 100)
            mActivity.showLoadingDialog();
        else
            mActivity.hideLoadingDialog();
    }

    @Override
    public void onProgressComplete() {
        endRefresh();
    }

    @Override
    public void onReceivedTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void onPageEnd(int l, int t, int oldl, int oldt) {
        int deltaY = t - oldt;
        //上滑 并且 正在显示底部栏
        if (deltaY > 0 && (Math.abs(deltaY) > 1)) {
            StatusBarUtils.StatusBarLightMode(mActivity);
            //向上滑动
            relativeTitleBg.setAlpha(1f);
        } else if (deltaY < 0 && (Math.abs(deltaY) > 0)) {
        }
        if (t == 0) { //滑动到top了
            relativeTitleBg.setAlpha(0);
            StatusBarUtils.setStatusBarColor(getActivity(), StatusBarUtils.TYPE_DARK);//状态栏
        }
    }

    @Override
    public void onPageTop(int l, int t, int oldl, int oldt) {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }

    private void endRefresh() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.endRefreshing();
            }
        });
    }

    private void refreshService() {
        if (!NetworkUtils.isNetworkAvailable(mActivity)) {
            shortTip(getString(R.string.str_check_net));
            endRefresh();
            return;
        }
        rlNetException.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearHistory();
        webView.loadUrl(getBaseUrl());//services
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        refreshService();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        super.didReceivedNotification(id, args);
        if (id == CommonConstants.tabSupport) {
            refreshService();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonConstants.tabSupport};
    }
}
