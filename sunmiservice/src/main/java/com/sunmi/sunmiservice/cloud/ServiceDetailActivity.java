package com.sunmi.sunmiservice.cloud;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.contract.ServiceDetailContract;
import com.sunmi.presenter.ServiceDetailPresenter;
import com.sunmi.sunmiservice.R;
import com.sunmi.sunmiservice.SunmiServiceConfig;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.litepal.crud.DataSupport;

import sunmi.common.base.BaseMvpActivity;
import sunmi.common.model.SunmiDevice;
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

    private int status, errorCode;

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
            String sn = bean.getDeviceSn();
            SunmiDevice device = DataSupport.where("deviceid=?", sn).findFirst(SunmiDevice.class);
            tvServiceName.setText(bean.getServiceName());
            if (device != null) {
                tvDeviceName.setText(device.getName());
            } else {
                tvDeviceName.setTextColor(ContextCompat.getColor(context, R.color.caution_primary));
                tvDeviceName.setText(R.string.tip_device_unbind);
                btnRenewal.setVisibility(View.GONE);
            }
            tvDeviceModel.setText(bean.getDeviceModel());
            tvDeviceSn.setText(sn);
            tvSubScribeTime.setText(DateTimeUtils.secondToDate(bean.getSubscribeTime(), "yyyy-MM-dd HH:mm"));
            tvExpireTime.setText(DateTimeUtils.secondToDate(bean.getExpireTime(), "yyyy-MM-dd HH:mm"));
            tvRemaining.setText(DateTimeUtils.secondToPeriod(bean.getValidTime(), context));
            tvServiceNum.setText(bean.getServiceNo());
            tvOrderNum.setText(bean.getOrderNo());
            status = bean.getRenewStatus();
            if (status == 2) {
                btnRenewal.setAlpha(0.4f);
            }
            errorCode = bean.getRenewErrorCode();

        } else {
            initNetworkError();
        }
    }

    private void initNetworkError() {
        rlService.setVisibility(View.GONE);
        rlOrder.setVisibility(View.GONE);
        btnRenewal.setVisibility(View.GONE);
        networkError.setVisibility(View.VISIBLE);
    }

    private void initNetworkNormal() {
        rlService.setVisibility(View.VISIBLE);
        rlOrder.setVisibility(View.VISIBLE);
        btnRenewal.setVisibility(View.VISIBLE);
        networkError.setVisibility(View.GONE);
    }

    @Click(resName = "btn_renewal")
    void renewalClick() {
        if (status == 2) {
            switch (errorCode) {
                case RpcErrorCode.ERR_SERVICE_SUBSCRIBE_ERROR:
                    shortTip(R.string.tip_renewal_less_three_days);
                    break;
                default:
                    break;
            }
        }else {
            WebViewCloudServiceActivity_.intent(context).deviceSn(mSn).mUrl(SunmiServiceConfig.CLOUD_STORAGE_SERVICE).start();
        }
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        mPresenter.getServiceDetailByDevice(mSn);
    }
}
