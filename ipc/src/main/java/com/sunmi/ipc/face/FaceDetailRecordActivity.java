package com.sunmi.ipc.face;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.face.model.Face;
import com.sunmi.ipc.model.FaceEntryHistoryResp;
import com.sunmi.ipc.rpc.IpcCloudApi;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseActivity;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

import static com.sunmi.ipc.face.FaceDetailActivity.DATE_FORMAT_ENTER_SHOP;
import static sunmi.common.utils.DateTimeUtils.secondToDate;

/**
 * @author yangShiJie
 * @date 2019/8/27
 */
@EActivity(resName = "face_activity_photo_detail_time_record")
public class FaceDetailRecordActivity extends BaseActivity implements BGARefreshLayout.BGARefreshLayoutDelegate {
    private static final int PAGE_SIZE = 15;//每页条数
    @ViewById(resName = "bga_refresh")
    BGARefreshLayout refreshView;
    @ViewById(resName = "recyclerView")
    RecyclerView recyclerView;
    @ViewById(resName = "tv_empty")
    TextView tvEmpty;
    @ViewById(resName = "include_network_error")
    View includeNetworkError;
    @Extra
    int mShopId;
    @Extra
    Face mFace;
    private int pageNumFlag = 1;
    private boolean isHasMore;
    private List<FaceEntryHistoryResp.EntryHistory> list = new ArrayList<>();
    private ArrivalListAdapter mAdapter;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new ArrivalListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        initRefreshLayout();
        arrivalList(true);
    }

    private void initRefreshLayout() {
        refreshView.setDelegate(this);
        refreshView.setRefreshViewHolder(new BGANormalRefreshViewHolder(this, true));
        refreshView.setPullDownRefreshEnable(true);
        refreshView.setIsShowLoadingMoreView(true);
    }

    @Click(resName = "btn_refresh")
    void onRefreshClick() {
        arrivalList(true);
    }

    private void arrivalList(final boolean isRefresh) {
        if (isRefresh) {
            list.clear();
            pageNumFlag = 1;
        }
        IpcCloudApi.arrivalListFaceGroup(SpUtils.getCompanyId(), mShopId, mFace.getFaceId(),
                pageNumFlag, PAGE_SIZE, new RetrofitCallback<FaceEntryHistoryResp>() {
                    @Override
                    public void onSuccess(int code, String msg, FaceEntryHistoryResp data) {
                        endRefresh(isRefresh);
                        includeNetworkError.setVisibility(View.GONE);
                        if (pageNumFlag == 1 && data.getHistoryList().size() == 0) {
                            tvEmpty.setText(R.string.ipc_face_group_no_enter_shop_time);
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            if (data.getHistoryList().size() == PAGE_SIZE) {
                                isHasMore = true;
                                pageNumFlag++;
                            } else {
                                isHasMore = false;
                            }
                            tvEmpty.setVisibility(View.GONE);
                            list.addAll(data.getHistoryList());
                            mAdapter.setData(list);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg, FaceEntryHistoryResp data) {
                        endRefresh(isRefresh);
                        includeNetworkError.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void endRefresh(boolean isRefresh) {
        if (isRefresh) {
            refreshView.endRefreshing();
        } else {
            refreshView.endLoadingMore();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        refreshView.beginRefreshing();
        arrivalList(true);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (isHasMore) {
            arrivalList(false);
            return true;
        }
        return false;
    }

    private static class ArrivalListAdapter extends CommonListAdapter<FaceEntryHistoryResp.EntryHistory> {

        private ArrivalListAdapter(Context context) {
            super(context, R.layout.item_face_photo_time_record, null);
        }

        @Override
        public void convert(ViewHolder holder, FaceEntryHistoryResp.EntryHistory info) {
            holder.setText(R.id.tv_time, secondToDate(info.getArrivalTime(), DATE_FORMAT_ENTER_SHOP));
        }
    }
}