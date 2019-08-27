package com.sunmi.assistant.mine.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.adapter.MsgContentAdapter;
import com.sunmi.assistant.mine.adapter.MsgTabAdapter;
import com.sunmi.assistant.mine.contract.MessageCountContract;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.model.MsgCountChildren;
import com.sunmi.assistant.mine.presenter.MessageCountPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseMvpFragment;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.view.SmRecyclerView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
@EFragment(R.layout.fragment_message)
public class SystemMessageFragment extends BaseMvpFragment<MessageCountPresenter>
        implements MessageCountContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.tv_message)
    TextView tvMsg;
    @ViewById(R.id.rv_tab)
    RecyclerView rvTab;
    @ViewById(R.id.rv_msg)
    SmRecyclerView rvMsg;
    @ViewById(R.id.bga_refresh)
    BGARefreshLayout refreshLayout;

    private MessageCountBean.ModelCountListBean bean;

    private List<MsgCountChildren> msgData;
    private HashMap<String, List<MsgCountChildren>> msgMap = new HashMap<>();
    private MsgTabAdapter msgTabAdapter;
    private MsgContentAdapter msgContentAdapter;
    private List<String> tabTitle = new ArrayList<>();
    private List<Integer> msgCount = new ArrayList<>();
    private List<MsgCountChildren> showData = new ArrayList<>();
    private String selectTab;


    @AfterViews
    void init() {
        mPresenter = new MessageCountPresenter();
        mPresenter.attachView(this);
        mPresenter.getMessageCount();
        showLoadingDialog();
        selectTab = getString(R.string.order_filter_all);
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        refreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mActivity, false));
        refreshLayout.setIsShowLoadingMoreView(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        rvTab.setLayoutManager(linearLayoutManager);
        rvMsg.init(R.drawable.shap_line_divider);
    }


    @Override
    public void getMessageCountSuccess(MessageCountBean data) {
        endRefresh();
        hideLoadingDialog();
        bean = data.getModelCountList().get(1);
        initData();
    }

    @Override
    public void getMessageCountFail(int code, String msg) {
        endRefresh();
        hideLoadingDialog();
        shortTip(R.string.tip_get_data_fail);
    }

    private void initData() {
        if (bean.getTotalCount() <= 0) {
            tvMsg.setVisibility(View.VISIBLE);
        } else {
            msgData = bean.getChildren();
            tabTitle.clear();
            msgCount.clear();
            List<MsgCountChildren> total = new ArrayList<>();
            tabTitle.add(getString(R.string.order_filter_all));
            msgCount.add(bean.getRemindUnreadCount());
            for (MsgCountChildren bean : msgData) {
                if (bean.getTotalCount() > 0) {
                    total.addAll(getShowData(bean.getChildren()));
                    if (TextUtils.equals(bean.getModelName(), MsgConstants.NOTIFY_MODEL_SERVICE)) {
                        tabTitle.add(getString(R.string.str_support));
                        msgCount.add(bean.getRemindUnreadCount());
                        msgMap.put(getString(R.string.str_support), getShowData(bean.getChildren()));
                    } else if (TextUtils.equals(bean.getModelName(), MsgConstants.NOTIFY_MODEL_TASK)) {
                        tabTitle.add(getString(R.string.str_task));
                        msgCount.add(bean.getRemindUnreadCount());
                        msgMap.put(getString(R.string.str_task), getShowData(bean.getChildren()));
                    }
                }
            }
            msgMap.put(getString(R.string.order_filter_all), total);
            initTab();
            initMsgContent();
        }
    }

    private void initTab() {
        if (msgTabAdapter == null) {
            msgTabAdapter = new MsgTabAdapter(mActivity, tabTitle, msgCount);
            msgTabAdapter.setOnItemClickListener(new MsgTabAdapter.OnItemClickListener() {
                @Override
                public void onClick(String data) {
                    selectTab = data;
                    initMsgContent();
                }
            });
            rvTab.setAdapter(msgTabAdapter);
        } else {
            msgTabAdapter.notifyDataSetChanged();
        }
    }

    private void initMsgContent() {
        List<MsgCountChildren> beans = msgMap.get(selectTab);
        if (beans != null) {
            showData.clear();
            showData.addAll(beans);
        }
        Collections.sort(showData);
        if (msgContentAdapter == null) {
            msgContentAdapter = new MsgContentAdapter(showData, mActivity);
            msgContentAdapter.setOnMsgClickListener(new MsgContentAdapter.OnMsgTypeClickListener() {
                @Override
                public void onClick(int modelId, String title) {
                    MsgDetailActivity_.intent(mActivity).modelId(modelId).title(title).start();
                }
            });
            rvMsg.setAdapter(msgContentAdapter);
        } else {
            msgContentAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == CommonNotifications.msgReadedOrChange) {
            showLoadingDialog();
            mPresenter.getMessageCount();
        }
    }

    private List<MsgCountChildren> getShowData(List<MsgCountChildren> data) {
        List<MsgCountChildren> list = new ArrayList<>();
        for (MsgCountChildren children : data) {
            if (children.getTotalCount() > 0) {
                list.add(children);
            }
        }
        return list;
    }

    @Override
    public int[] getStickNotificationId() {
        return new int[]{CommonNotifications.msgReadedOrChange};
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        mPresenter.getMessageCount();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    private void endRefresh() {
        if (refreshLayout != null) {
            refreshLayout.endRefreshing();
        }
    }
}
