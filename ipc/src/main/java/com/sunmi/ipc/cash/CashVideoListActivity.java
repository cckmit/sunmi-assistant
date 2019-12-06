package com.sunmi.ipc.cash;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.cash.adapter.CashVideoAdapter;
import com.sunmi.ipc.contract.CashVideoListConstract;
import com.sunmi.ipc.model.CashVideoResp;
import com.sunmi.ipc.presenter.CashVideoListPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.DropdownMenuNew;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-04.
 */

@EActivity(resName = "activity_cash_video_list")
public class CashVideoListActivity extends BaseMvpActivity<CashVideoListPresenter>
        implements CashVideoListConstract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(resName = "tv_date")
    TextView tvDate;
    @ViewById(resName = "bga_refresh")
    BGARefreshLayout refreshLayout;
    @ViewById(resName = "rv_cash_video")
    RecyclerView rvCashVideo;
    @ViewById(resName = "layout_network_error")
    View networkError;
    @ViewById(resName = "dm_device")
    DropdownMenuNew dmDevice;
    @ViewById(resName = "dm_time")
    DropdownMenuNew dmTime;
    @ViewById(resName = "tv_abnormal")
    TextView tvAbnormal;

    @Extra
    int deviceId = 0;
    @Extra
    long startTime;
    @Extra
    long endTime;
    @Extra
    int videoType = 0;

    private List<CashVideoResp.AuditVideoListBean> dataList = new ArrayList<>();
    private boolean hasMore;
    private CashVideoAdapter adapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarFullTransparent(this);
        mPresenter = new CashVideoListPresenter();
        if (deviceId != 0) {
            dmDevice.setVisibility(View.GONE);
        }
        tvDate.setText(DateTimeUtils.secondToDate(startTime, "yyyy.MM.dd"));
        refreshLayout.setDelegate(this);
        BGARefreshViewHolder viewHolder = new BGANormalRefreshViewHolder(context, true);
        viewHolder.setLoadingMoreText(getString(R.string.str_loding_more));
        viewHolder.setLoadMoreBackgroundColorRes(R.color.bg_common);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        refreshLayout.setRefreshViewHolder(viewHolder);
        showLoadingDialog();
        mPresenter.load(deviceId, videoType, startTime, endTime);
    }

    @Override
    public void getCashVideoSuccess(List<CashVideoResp.AuditVideoListBean> beans, boolean hasMore, int total) {
        this.hasMore = hasMore;
        addData(beans);
    }

    @Override
    public void netWorkError() {
        networkError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.load(deviceId, videoType, startTime, endTime);
        dataList.clear();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isNetworkAvailable(context) && hasMore) {
            mPresenter.loadMore();
            return true;
        }
        return false;
    }

    @UiThread
    protected void addData(List<CashVideoResp.AuditVideoListBean> beans) {
        if (beans.size() > 0) {
            initAdapter();
            dataList.addAll(beans);
            adapter.notifyDataSetChanged();
        }
    }

    private void initAdapter() {
        if (adapter != null) {
            adapter = new CashVideoAdapter(dataList, context);
            rvCashVideo.setLayoutManager(new LinearLayoutManager(context));
            adapter.setOnItemClickListener(new CashVideoAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(List<CashVideoResp.AuditVideoListBean> data, int pos) {

                }
            });
            rvCashVideo.setAdapter(adapter);
        }
    }
}
