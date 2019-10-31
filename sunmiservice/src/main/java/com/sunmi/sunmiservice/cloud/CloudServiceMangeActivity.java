package com.sunmi.sunmiservice.cloud;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.sunmi.adapter.ServiceListAdapter;
import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.contract.CloudServiceMangeContract;
import com.sunmi.presenter.CloudServiceMangePresenter;
import com.sunmi.sunmiservice.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConfig;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;

@EActivity(resName = "activity_cloud_service_mange")
public class CloudServiceMangeActivity extends BaseMvpActivity<CloudServiceMangePresenter>
        implements CloudServiceMangeContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(resName = "bga_refresh")
    BGARefreshLayout refreshLayout;
    @ViewById(resName = "rv_service")
    RecyclerView rvService;
    @ViewById(resName = "rl_no_service")
    RelativeLayout rlNoService;
    @ViewById(resName = "layout_network_error")
    View networkError;

    private int pageNum, pageSize;
    private List<ServiceDetailBean> dataList = new ArrayList<>();
    private ServiceListAdapter adapter;
    private boolean loadFinish = false;
    private List<SunmiDevice> devices;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new CloudServiceMangePresenter();
        mPresenter.attachView(this);
        devices = DataSupport.where("type=?", "IPC").find(SunmiDevice.class);
        reloadSubscriptionList();
        showLoadingDialog();
        initServiceList();
        refreshLayout.setDelegate(this);
        BGARefreshViewHolder viewHolder = new BGANormalRefreshViewHolder(context, true);
        viewHolder.setLoadingMoreText(getString(R.string.str_loding_more));
        viewHolder.setLoadMoreBackgroundColorRes(R.color.bg_common);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        refreshLayout.setRefreshViewHolder(viewHolder);
    }

    @Override
    public void getSubscriptionListSuccess(List<ServiceDetailBean> beans, int total) {
        networkError.setVisibility(View.GONE);
        refreshLayout.endLoadingMore();
        refreshLayout.endRefreshing();
        if (total <= 0) {
            rlNoService.setVisibility(View.VISIBLE);
        } else {
            int returnCount = beans.size();
            if (beans.size() <= 0) {
                return;
            }
            if (pageNum == 1 || total > dataList.size()) {
                addData(beans, pageNum == 1);
                if (total > (pageNum - 1) * pageSize + returnCount) {
                    pageNum++;
                } else {
                    loadFinish = true;
                }
            }
        }
    }

    @Override
    public void getIpcDetailListSuccess() {
        devices = DataSupport.where("type=?", "IPC").find(SunmiDevice.class);
        adapter.setDevices(devices);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getSubscriptionListFail(int code, String msg) {
        refreshLayout.endRefreshing();
        refreshLayout.endLoadingMore();
        if (dataList.size() <= 0) {
            networkError.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    protected void addData(List<ServiceDetailBean> beans, boolean isRefresh) {
        if (beans.size() > 0) {
            if (isRefresh) {
                dataList.clear();
            }
            dataList.addAll(beans);
            adapter.notifyDataSetChanged();
        }
    }

    @Click(resName = "btn_refresh")
    void refreshClick() {
        reloadSubscriptionList();
    }

    @Click(resName = "btn_open")
    void onpenClick() {
        WebViewCloudServiceActivity_.intent(context).mUrl(CommonConfig.CLOUD_STORAGE_URL).start();
    }

    private void initServiceList() {
        if (adapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvService.setLayoutManager(layoutManager);
            adapter = new ServiceListAdapter(dataList, context, devices);
            adapter.setOnServiceClickListener(new ServiceListAdapter.OnServiceClickListener() {
                @Override
                public void onRenewalClick(ServiceDetailBean bean) {
                    if (bean.getRenewStatus() == 2) {
                        showErrror(bean.getRenewErrorCode());

                    } else {
                        WebViewCloudServiceActivity_.intent(context).deviceSn(bean.getDeviceSn())
                                .mUrl(CommonConfig.CLOUD_STORAGE_URL).start();
                    }
                }
            });
            rvService.setAdapter(adapter);
        }
    }

    private void showErrror(int code) {
        switch (code) {
            case RpcErrorCode.ERR_SERVICE_SUBSCRIBE_ERROR:
                shortTip(R.string.tip_renewal_less_three_days);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        reloadSubscriptionList();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isNetworkAvailable(context) && !loadFinish) {
            mPresenter.getSubscriptionList(pageNum, pageSize);
            return true;
        }
        return false;
    }

    @Override
    public int[] getUnStickNotificationId() {
        return new int[]{CommonNotifications.cloudStorageChange};
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (CommonNotifications.cloudStorageChange == id) {
            reloadSubscriptionList();
        }
    }

    private void reloadSubscriptionList() {
        pageNum = 1;
        pageSize = 10;
        loadFinish = false;
        if (devices.size() <= 0) {
            mPresenter.getIpcDetailList();
        }
        mPresenter.getSubscriptionList(pageNum, pageSize);
    }
}