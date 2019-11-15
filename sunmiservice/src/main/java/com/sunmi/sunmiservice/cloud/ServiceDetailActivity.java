package com.sunmi.sunmiservice.cloud;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.contract.ServiceDetailContract;
import com.sunmi.presenter.ServiceDetailPresenter;
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

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.constant.RouterConfig;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.StatusBarUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-24.
 */
@EActivity(resName = "activity_service_detail")
public class ServiceDetailActivity extends BaseMvpActivity<ServiceDetailPresenter> implements ServiceDetailContract.View {

    @ViewById(resName = "rl_service")
    RelativeLayout rlService;
    @ViewById(resName = "rl_order")
    RelativeLayout rlOrder;
    @ViewById(resName = "tv_service_name")
    TextView tvServiceName;
    @ViewById(resName = "tv_status")
    TextView tvStatus;
    @ViewById(resName = "tv_device_name")
    TextView tvDeviceName;
    @ViewById(resName = "tv_device_model")
    TextView tvDeviceModel;
    @ViewById(resName = "tv_device_sn")
    TextView tvDeviceSn;
    @ViewById(resName = "tv_subscribe_time")
    TextView tvSubScribeTime;
    @ViewById(resName = "tv_expire_time")
    TextView tvExpireTime;
    @ViewById(resName = "tv_remaining")
    TextView tvRemaining;
    @ViewById(resName = "tv_service_num")
    TextView tvServiceNum;
    @ViewById(resName = "tv_order_num")
    TextView tvOrderNum;
    @ViewById(resName = "btn_renewal")
    Button btnRenewal;
    @ViewById(resName = "layout_network_error")
    View networkError;

    @Extra
    String mSn;
    @Extra
    boolean isBind;
    @Extra
    String deviceName;

    private int status, errorCode;

    /**
     * 路由启动Activity
     *
     * @param request
     * @return
     */
    @RouterAnno(
            path = RouterConfig.SunmiService.SERVICE_DETAIL
    )
    public static Intent start(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), ServiceDetailActivity_.class);
        return intent;
    }

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new ServiceDetailPresenter();
        mPresenter.attachView(this);
        mPresenter.getServiceDetailByDevice(mSn);
    }

    @UiThread
    @Override
    public void getServiceDetail(ServiceDetailBean bean) {
        if (bean != null) {
            initNetworkNormal();
            status = bean.getRenewStatus();
            errorCode = bean.getRenewErrorCode();
            String sn = bean.getDeviceSn();
            tvServiceName.setText(bean.getServiceName());
            if (isBind) {
                tvDeviceName.setText(deviceName);
            } else {
                tvDeviceName.setText("- -");
                tvStatus.setText(R.string.str_unbind);
                // btnRenewal.setVisibility(View.GONE);
            }
            tvDeviceModel.setText(bean.getDeviceModel());
            tvDeviceSn.setText(sn);
            tvSubScribeTime.setText(DateTimeUtils.secondToDate(bean.getSubscribeTime(), "yyyy-MM-dd HH:mm"));
            tvExpireTime.setText(DateTimeUtils.secondToDate(bean.getExpireTime(), "yyyy-MM-dd HH:mm"));
            if (bean.getStatus() != CommonConstants.CLOUD_STORAGE_EXPIRED) {
                tvRemaining.setText(DateTimeUtils.secondToPeriod(bean.getValidTime()));
            } else {
                tvStatus.setText(R.string.str_expired);
                tvRemaining.setText("- -");
            }
            tvServiceNum.setText(bean.getServiceNo());
            tvOrderNum.setText(bean.getOrderNo());

        } else {
            initNetworkError();
        }
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.cloudStorageChange};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (CommonNotifications.cloudStorageChange == id) {
            mPresenter.getServiceDetailByDevice(mSn);
        }
    }

    private void initNetworkError() {
        rlService.setVisibility(View.GONE);
        rlOrder.setVisibility(View.GONE);
        //btnRenewal.setVisibility(View.GONE);
        networkError.setVisibility(View.VISIBLE);
    }

    private void initNetworkNormal() {
        rlService.setVisibility(View.VISIBLE);
        rlOrder.setVisibility(View.VISIBLE);
        //btnRenewal.setVisibility(View.VISIBLE);
        networkError.setVisibility(View.GONE);
    }


    @Click(resName = "btn_renewal")
    void renewalClick() {
        if (status == CommonConstants.CLOUD_STORAGE_NOT_RENEWABLE) {
            switch (errorCode) {
                case RpcErrorCode.ERR_SERVICE_SUBSCRIBE_ERROR:
                    shortTip(R.string.tip_renewal_less_three_days);
                    break;
                default:
                    break;
            }
        } else {
            ArrayList<String> snList = new ArrayList<>();
            snList.add(mSn);
            WebViewCloudServiceActivity_.intent(context).snList(snList).mUrl(CommonConfig.CLOUD_STORAGE_URL).start();
        }
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        mPresenter.getServiceDetailByDevice(mSn);
    }
}
