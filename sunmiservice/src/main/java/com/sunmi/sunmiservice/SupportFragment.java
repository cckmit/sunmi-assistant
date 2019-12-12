package com.sunmi.sunmiservice;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.sunmiservice.cloud.WebViewCloudServiceActivity_;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaojinzi.component.impl.Router;
import com.xiaojinzi.component.impl.service.ServiceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseFragment;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CashVideoServiceBean;
import sunmi.common.model.ServiceListResp;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.router.IpcApi;
import sunmi.common.router.IpcCloudApiAnno;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_support")
public class SupportFragment extends BaseFragment
        implements View.OnClickListener {


    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "tv_cloud_storage")
    TextView tvCloudStorage;
    @ViewById(resName = "iv_tip_free")
    ImageView ivTipFree;
    @ViewById(resName = "tv_cash_video")
    TextView tvCashVideo;


    private IWXAPI api;// 第三方app和微信通信的openApi接口
    private ArrayList<CashVideoServiceBean> cashVideoServiceBeans = new ArrayList<>();
    private IpcCloudApiAnno ipcCloudApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regToWx();
    }

    @AfterViews
    void init() {
        titleBar.getRightTextView().setOnClickListener(this);
        ipcCloudApi = ServiceManager.get(IpcCloudApiAnno.class);
        changeCashVideoCard();
        changeCloudCard();
    }

    @UiThread
    protected void changeCloudCard() {
        ShopBundledCloudInfo info = DataSupport.where("shopId=?", String.valueOf(SpUtils.getShopId())).findFirst(ShopBundledCloudInfo.class);
        if (info != null && info.getSnSet().size() > 0) {
            tvCloudStorage.setText(R.string.str_use_free);
            ivTipFree.setVisibility(View.VISIBLE);
        } else {
            tvCloudStorage.setText(R.string.str_subscribe_now);
            ivTipFree.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        ServiceManageActivity_.intent(mActivity).start();
    }

    @Click(resName = "ll_cash_video")
    void cashVidoClick() {
        if (isFastClick(500)) {
            return;
        }
        if (cashVideoServiceBeans.size() > 0) {
            Router.withApi(IpcApi.class).goToCashVideoOverview(mActivity, cashVideoServiceBeans, false);
        } else {
            WebViewCloudServiceActivity_.intent(mActivity).mUrl(CommonConfig.SERVICE_H5_URL + CommonConstants.H5_CASH_VIDEO).start();
        }
    }

    @Click(resName = "ll_cloud_storage")
    void cloudStorageClick() {
        if (!checkNetwork() || isFastClick(500)) {
            return;
        }
        WebViewCloudServiceActivity_.intent(mActivity).mUrl(CommonConfig.SERVICE_H5_URL + CommonConstants.H5_CLOUD_STORAGE).start();
    }

    @Click(resName = "ll_after_sales")
    void afterSalesClick() {
        if (isFastClick(500)) {
            return;
        }
        launchMiniProgram(SunmiServiceConfig.WECHART_USER_NAME, SunmiServiceConfig.WECHAT_PATH, SunmiServiceConfig.WECHAT_MINI_PROGRAM_TYPE);
    }

    @Click(resName = "ll_sunmi_store")
    void sunmiStoreClick() {
        if (!checkNetwork() || isFastClick(500)) {
            return;
        }
        WebViewSunmiMallActivity_.intent(mActivity).mUrl(SunmiServiceConfig.SUNMI_MALL_HOST + "?channel=2&subchannel=4")
                .start();
    }

    @Click(resName = "tv_weBank")
    void weBankClick() {
        if (!checkNetwork() || isFastClick(500)) {
            return;
        }
        WebViewActivity_.intent(mActivity).url(SunmiServiceConfig.WE_BANK_HOST).start();
    }

    private boolean checkNetwork() {
        if (!NetworkUtils.isNetworkAvailable(mActivity)) {
            shortTip(R.string.toast_network_error);
            return false;
        }
        return true;
    }

    private void changeCashVideoCard() {
        cashVideoServiceBeans.clear();
        if (ipcCloudApi != null) {
            ipcCloudApi.getAuditVideoServiceList(null, new RetrofitCallback<ServiceListResp>() {
                @Override
                public void onSuccess(int code, String msg, ServiceListResp data) {
                    List<ServiceListResp.DeviceListBean> beans = data.getDeviceList();
                    if (beans.size() > 0) {
                        for (ServiceListResp.DeviceListBean bean : beans) {
                            if (bean.getStatus() == CommonConstants.SERVICE_ALREADY_OPENED) {
                                CashVideoServiceBean info = new CashVideoServiceBean();
                                info.setDeviceId(bean.getDeviceId());
                                info.setDeviceSn(bean.getDeviceSn());
                                info.setDeviceName(bean.getDeviceName());
                                info.setImgUrl(bean.getImgUrl());
                                cashVideoServiceBeans.add(info);
                            }
                        }
                    }
                    if (cashVideoServiceBeans.size() > 0) {
                        tvCashVideo.setText(R.string.str_setting_detail);
                    } else {
                        tvCashVideo.setText(R.string.str_learn_more);
                    }
                }

                @Override
                public void onFail(int code, String msg, ServiceListResp data) {
                    changeCashVideoCard();
                }
            });
        }
    }

    /*private void initRefreshLayout() {
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGANormalRefreshViewHolder refreshViewHolder =
                new BGANormalRefreshViewHolder(getActivity(), false);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        mRefreshLayout.setIsShowLoadingMoreView(false); // 设置正在加载更多时的文本
    }*/

    /*private void initWebView() {
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
        webView.loadUrl(getBaseUrl());
    }*/

    @Override
    public int[] getStickNotificationId() {
        return new int[]{
                CommonNotifications.activeCloudChange, CommonNotifications.cashVideoSubscribe, CommonNotifications.shopSwitched
        };
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.activeCloudChange) {
            changeCloudCard();
        } else if (id == CommonNotifications.cashVideoSubscribe || id == CommonNotifications.shopSwitched) {
            changeCashVideoCard();
        }
    }

    private void launchMiniProgram(String userName, String path, int miniProgramType) {
        if (api == null) return;
        if (!api.isWXAppInstalled()) {
            shortTip(R.string.tip_wechat_not_installed);
            return;
        }

        int miniProgramTypeInt = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        try {
            miniProgramTypeInt = miniProgramType;
        } catch (Exception e) {
            e.printStackTrace();
        }

        WXLaunchMiniProgram.Req miniProgramReq = new WXLaunchMiniProgram.Req();
        miniProgramReq.userName = userName;// 小程序原始id
        miniProgramReq.path = path; //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        miniProgramReq.miniprogramType = miniProgramTypeInt;// 可选打开 开发版，体验版和正式版
        api.sendReq(miniProgramReq);
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(mActivity, SunmiServiceConfig.WECHAT_APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(SunmiServiceConfig.WECHAT_APP_ID);
    }

    /**
     * app主动发送消息给微信，发送完成之后会切回到第三方app界面
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

  /*  @Override
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
        //tvTitle.setText(title);
    }

    @Override
    public void onPageEnd(int l, int t, int oldl, int oldt) {
        *//*int deltaY = t - oldt;
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
        }*//*
    }

    @Override
    public void onPageTop(int l, int t, int oldl, int oldt) {

    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }

    @UiThread
    void endRefresh() {
        *//*if (mRefreshLayout != null)
            mRefreshLayout.endRefreshing();*//*
    }

    private void refreshService() {
        if (!NetworkUtils.isNetworkAvailable(mActivity)) {
            shortTip(getString(R.string.str_check_net));
            endRefresh();
            return;
        }
        *//*rlNetException.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.clearCache(true);
        webView.clearHistory();
        webView.loadUrl(getBaseUrl());//services*//*
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
    }*/

}
