package com.sunmi.assistant.mine.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.adapter.MsgContentAdapter;
import com.sunmi.assistant.mine.adapter.MsgTabAdapter;
import com.sunmi.assistant.mine.model.MessageCountBean;
import com.sunmi.assistant.mine.model.MsgCountChildren;
import com.sunmi.assistant.utils.MessageUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import sunmi.common.base.BaseFragment;
import sunmi.common.view.SmRecyclerView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
@EFragment(R.layout.fragment_message)
public class DeviceMessageFragment extends BaseFragment
        implements BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.tv_message)
    TextView tvMsg;
    @ViewById(R.id.rv_tab)
    RecyclerView rvTab;
    @ViewById(R.id.rv_msg)
    SmRecyclerView rvMsg;
    @ViewById(R.id.bga_refresh)
    BGARefreshLayout refreshLayout;
    @ViewById(R.id.layout_network_error)
    View networkError;

    private MessageCountBean.ModelCountListBean bean; //设备消息

    private HashMap<String, List<MsgCountChildren>> msgMap = new HashMap<>(); //用于存储对应的消息类型
    private MsgTabAdapter msgTabAdapter;
    private MsgContentAdapter msgContentAdapter;
    private List<String> tabTitle = new ArrayList<>();
    private List<Integer> msgCount = new ArrayList<>();
    private List<MsgCountChildren> showData = new ArrayList<>();
    private String selectTab;


    @AfterViews
    void init() {
        showLoadingDialog();
        selectTab = getString(R.string.order_filter_all);
        refreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        refreshLayout.setRefreshViewHolder(new BGANormalRefreshViewHolder(mActivity, false));
        refreshLayout.setIsShowLoadingMoreView(false);
        LinearLayoutManager llManager = new LinearLayoutManager(mActivity,
                LinearLayoutManager.HORIZONTAL, false);
        rvTab.setLayoutManager(llManager);
        rvMsg.init(R.drawable.shap_line_divider);
    }

    public void getMessageCountSuccess(MessageCountBean data) {
        if (networkError == null) {
            return;
        }
        endRefresh();
        networkError.setVisibility(View.GONE);
        bean = data.getModelCountList().get(0);
        initData();
    }

    public void getMessageCountFail() {
        if (networkError == null) {
            return;
        }
        endRefresh();
        networkError.setVisibility(View.VISIBLE);
        shortTip(R.string.tip_get_data_fail);
    }

    private void initData() {
        if (bean.getTotalCount() <= 0) {
            tvMsg.setVisibility(View.VISIBLE);
        } else {
            List<MsgCountChildren> msgData = bean.getChildren();
            tabTitle.clear();
            msgCount.clear();
            List<MsgCountChildren> total = new ArrayList<>();
            tabTitle.add(getString(R.string.order_filter_all));
            msgCount.add(bean.getRemindUnreadCount());
            for (MsgCountChildren bean : msgData) {
                if (bean.getTotalCount() > 0) {
                    total.addAll(getShowData(bean.getChildren()));
                    String title = MessageUtils.getInstance().getMsgFirst(bean.getModelName());
                    tabTitle.add(title);
                    msgCount.add(bean.getRemindUnreadCount());
                    msgMap.put(title, getShowData(bean.getChildren()));
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
            msgTabAdapter.setOnItemClickListener(data -> {
                selectTab = data;
                initMsgContent();
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
            msgContentAdapter.setOnMsgClickListener((modelId, modelName) ->
                    MsgDetailActivity_.intent(mActivity).modelId(modelId).modelName(modelName).start());
            rvMsg.setAdapter(msgContentAdapter);
        } else {
            msgContentAdapter.notifyDataSetChanged();
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

    @Click(R.id.btn_refresh)
    void refreshClick() {
        refreshMsgCount();
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        refreshMsgCount();
    }

    private void refreshMsgCount() {
        if (getActivity() != null) {
            ((MsgCenterActivity) getActivity()).refreshMsgCount();
        }
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
