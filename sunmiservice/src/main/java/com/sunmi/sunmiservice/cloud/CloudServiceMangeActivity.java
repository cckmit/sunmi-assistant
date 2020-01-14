package com.sunmi.sunmiservice.cloud;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.sunmi.adapter.ServiceListAdapter;
import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.constant.ServiceConstants;
import com.sunmi.contract.CloudServiceMangeContract;
import com.sunmi.presenter.CloudServiceMangePresenter;
import com.sunmi.sunmiservice.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.WebViewParamsUtils;

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

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        mPresenter = new CloudServiceMangePresenter();
        mPresenter.attachView(this);
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
    @UiThread
    public void getSubscriptionListSuccess(List<ServiceDetailBean> beans, int total) {
        if (networkError.isShown()) {
            networkError.setVisibility(View.GONE);
        }
        refreshLayout.endRefreshing();
        refreshLayout.endLoadingMore();
        if (total <= 0) {
            rlNoService.setVisibility(View.VISIBLE);
        } else {
            if (rlNoService.isShown()) {
                rlNoService.setVisibility(View.GONE);
            }
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
    @UiThread
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
        WebViewCloudServiceActivity_.intent(context).params(WebViewParamsUtils.getCloudStorageParams(new ArrayList<>(), ""))
                .mUrl(CommonConstants.H5_CLOUD_STORAGE).start();
    }

    private void initServiceList() {
        if (adapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvService.setLayoutManager(layoutManager);
            adapter = new ServiceListAdapter(dataList, context);
            adapter.setOnServiceClickListener(bean -> {
                if (bean.getRenewStatus() == CommonConstants.CLOUD_STORAGE_NOT_RENEWABLE) {
                    showErrror(bean.getRenewErrorCode());

                } else {
                    WebViewCloudServiceActivity_.intent(context).params(WebViewParamsUtils.getCloudStorageParams(bean.getDeviceSn(), bean.getProductNo()))
                            .mUrl(CommonConstants.H5_CLOUD_RENEW).start();
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
            mPresenter.getSubscriptionList(pageNum, pageSize, ServiceConstants.CLOUD_STORAGE_CATEGORY);
            return true;
        }
        return false;
    }

    @Override
    public int[] getStickNotificationId() {
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
        mPresenter.getSubscriptionList(pageNum, pageSize, ServiceConstants.CLOUD_STORAGE_CATEGORY);
    }
}
