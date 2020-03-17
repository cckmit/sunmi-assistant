package com.sunmi.sunmiservice;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmi.contract.SupportContract;
import com.sunmi.presenter.SupportPresenter;
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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseMvpFragment;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.CashServiceInfo;
import sunmi.common.model.ShopBundledCloudInfo;
import sunmi.common.router.IpcApi;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.WebViewParamsUtils;
import sunmi.common.view.TitleBarView;

@EFragment(resName = "fragment_support")
public class SupportFragment extends BaseMvpFragment<SupportPresenter> implements SupportContract.View {

    private static final int FAST_CLICK_INTERVAL = 500;

    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "layoutContent")
    View layoutContent;
    @ViewById(resName = "layoutNetworkError")
    View layoutNetworkError;
    @ViewById(resName = "tv_cloud_storage")
    TextView tvCloudStorage;
    @ViewById(resName = "iv_tip_free")
    ImageView ivTipFree;
    @ViewById(resName = "tv_cash_video")
    TextView tvCashVideo;
    @ViewById(resName = "tv_cash")
    TextView tvCash;
    @ViewById(resName = "tv_cash_prevent")
    TextView tvCashPrevent;
    @ViewById(resName = "ll_cash_prevent")
    LinearLayout llCashPrevent;
    @ViewById(resName = "ll_cash_video")
    LinearLayout llCashVideo;
    @ViewById(resName = "tv_loan")
    TextView tvLoan;
    @ViewById(resName = "ll_loan")
    LinearLayout llLoan;

    /**
     * 第三方app和微信通信的openApi接口
     */
    private IWXAPI api;

    private ArrayList<CashServiceInfo> cashServiceInfoList = new ArrayList<>();
    private boolean hasCloudService = false;
    private ArrayList<String> snList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regToWx();
    }

    private void regToWx() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(mActivity, CommonConfig.WECHAT_APP_ID, true);
        // 将应用的appId注册到微信
        api.registerApp(CommonConfig.WECHAT_APP_ID);
    }

    @AfterViews
    void init() {
        mPresenter = new SupportPresenter();
        mPresenter.attachView(this);
        showLoadingDialog();
        initTitleBar();
        initCloudCard();
        initCashPreventCardVisibility(false);
        if (SpUtils.getLoanStatus()) {
            llLoan.setVisibility(View.VISIBLE);
            tvLoan.setVisibility(View.VISIBLE);
        }
        mPresenter.load();
    }

    private void initTitleBar() {
        titleBar.getRightTextView().setOnClickListener(v ->
                WebViewCloudServiceActivity_.intent(mActivity)
                        .mUrl(CommonConstants.H5_SERVICE_MANAGER)
                        .params(WebViewParamsUtils.getUserInfoParams()).start());
    }

    @UiThread
    protected void initCloudCard() {
        ShopBundledCloudInfo info = DataSupport.where("shopId=?",
                String.valueOf(SpUtils.getShopId())).findFirst(ShopBundledCloudInfo.class);
        if (info != null && info.getSnSet().size() > 0) {
            tvCloudStorage.setText(R.string.str_use_free);
            ivTipFree.setVisibility(View.VISIBLE);
        } else {
            tvCloudStorage.setText(R.string.str_subscribe_now);
            ivTipFree.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadSuccess(List<CashServiceInfo> infoList) {
        hideLoadingDialog();
        layoutContent.setVisibility(View.VISIBLE);
        layoutNetworkError.setVisibility(View.GONE);
        cashServiceInfoList.clear();
        cashServiceInfoList.addAll(infoList);
        boolean hasCashLossPrevention = false;
        hasCloudService = false;
        if (cashServiceInfoList.isEmpty()) {
            tvCashVideo.setText(R.string.str_learn_more);
        } else {
            // 已经开通收银视频
            tvCashVideo.setText(R.string.str_setting_detail);
            // 确认是否有设备开通了收银防损，并更新卡片
            for (CashServiceInfo info : cashServiceInfoList) {
                if (info.isHasCloudStorage()) {
                    hasCloudService = true;
                }
                if (info.isHasCashLossPrevention()) {
                    hasCashLossPrevention = true;
                }
                snList.add(info.getDeviceSn());
            }
        }
        initCashPreventCardVisibility(hasCashLossPrevention);
    }

    @Override
    public void loadFailed() {
        hideLoadingDialog();
        layoutContent.setVisibility(View.GONE);
        layoutNetworkError.setVisibility(View.VISIBLE);
    }

    @Click(resName = "btn_refresh")
    public void refresh() {
        showDarkLoading();
        cashServiceInfoList.clear();
        mPresenter.load();
    }

    @Click(resName = "ll_online_course")
    void onlineCourseClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        WebViewCloudServiceActivity_.intent(mActivity).mUrl(CommonConstants.H5_SERVICE_COURSE)
                .params(WebViewParamsUtils.getUserInfoParams()).start();
    }

    @Click(resName = "ll_loan")
    void commerceBankClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        WebViewActivity_.intent(mActivity).url(CommonConstants.H5_LOAN).start();
    }

    @Click(resName = "ll_cash_prevent")
    void cashPreventClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        if (hasCloudService) {
            // 已经开通云存储服务,进入收银总揽页
            Router.withApi(IpcApi.class)
                    .goToCashVideoOverview(mActivity, cashServiceInfoList, false, true);
        } else {
            // 有设备开通收银反损，但是没有开通云存储，进入开通页
            CashVideoNonCloudActivity_.intent(mActivity).snList(snList).start();
        }
    }

    @Click(resName = "ll_cash_video")
    void cashVideoClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        if (cashServiceInfoList.isEmpty()) {
            // 没有设备开通收银视频，进入开通页
            WebViewCloudServiceActivity_.intent(mActivity).mUrl(CommonConstants.H5_CASH_VIDEO)
                    .params(WebViewParamsUtils.getCashVideoParams(null, 0)).start();
        } else if (hasCloudService) {
            // 有设备开通收银视频，并已经开通云存储服务，进入收银视频总览页
            Router.withApi(IpcApi.class)
                    .goToCashVideoOverview(mActivity, cashServiceInfoList, false, false);
        } else {
            // 有设备开通收银视频，但是没有开通云存储，进入开通页
            CashVideoNonCloudActivity_.intent(mActivity).snList(snList).start();
        }
    }

    @Click(resName = "ll_cloud_storage")
    void cloudStorageClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        WebViewCloudServiceActivity_.intent(mActivity).mUrl(CommonConstants.H5_CLOUD_STORAGE)
                .params(WebViewParamsUtils.getCloudStorageParams(new ArrayList<>(), "")).start();
    }

    @Click(resName = "ll_after_sales")
    void afterSalesClick() {
        if (isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        launchMiniProgram(SunmiServiceConfig.WECHAT_USER_NAME, SunmiServiceConfig.WECHAT_PATH,
                SunmiServiceConfig.WECHAT_MINI_PROGRAM_TYPE);
    }

   /* @Click(resName = "tv_weBank")
    void weBankClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        WebViewActivity_.intent(mActivity).url(SunmiServiceConfig.WE_BANK_HOST).start();
    }*/

    @Click(resName = "tv_recruit")
    void recruitClick() {
        if (isNetworkError() || isFastClick(FAST_CLICK_INTERVAL)) {
            return;
        }
        launchMiniProgram(SunmiServiceConfig.WECHAT_USER_NAME_QINGTUAN,
                SunmiServiceConfig.WECHAT_PATH_QINGTUAN + getParams(),
                SunmiServiceConfig.WECHAT_MINI_PROGRAM_TYPE);
    }

    private String getParams() {
        String param = "{\"param\":{\"name\":\"" + SpUtils.getUsername() + "\",\"mobile\":\"" + SpUtils.getMobile() + "\"}}";
        return new String(Base64.encode(param.getBytes(), Base64.NO_WRAP));
    }

    private boolean isNetworkError() {
        if (!NetworkUtils.isNetworkAvailable(getContext())) {
            shortTip(R.string.toast_network_error);
            return true;
        }
        return false;
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{
                CommonNotifications.activeCloudChange,
                CommonNotifications.cashVideoSubscribe,
                CommonNotifications.shopSwitched,
                CommonNotifications.cashPreventSubscribe,
                CommonNotifications.cloudStorageChange,
                CommonNotifications.perspectiveSwitch
        };
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.activeCloudChange) {
            initCloudCard();
        } else if (id == CommonNotifications.cashVideoSubscribe
                || id == CommonNotifications.shopSwitched
                || id == CommonNotifications.perspectiveSwitch
                || id == CommonNotifications.cashPreventSubscribe) {
            showDarkLoading();
            cashServiceInfoList.clear();
            mPresenter.load();
        } else if (id == CommonNotifications.cloudStorageChange) {
            hasCloudService = true;
        }
    }

    private void initCashPreventCardVisibility(boolean hasCashLossPrevention) {
        if (hasCashLossPrevention) {
            tvCash.setVisibility(View.GONE);
            llCashVideo.setVisibility(View.GONE);
            tvCashPrevent.setVisibility(View.VISIBLE);
            llCashPrevent.setVisibility(View.VISIBLE);
        } else {
            tvCash.setVisibility(View.VISIBLE);
            llCashVideo.setVisibility(View.VISIBLE);
            tvCashPrevent.setVisibility(View.GONE);
            llCashPrevent.setVisibility(View.GONE);
        }
    }

    /**
     * app主动发送消息给微信，发送完成之后会切回到第三方app界面
     * FIXME: 没有地方调用该方法，是否应该删除
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
     * FIXME: 没有地方调用该方法，是否应该删除
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

    private void launchMiniProgram(String userName, String path, int miniProgramType) {
        if (api == null) {
            return;
        }
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

}
