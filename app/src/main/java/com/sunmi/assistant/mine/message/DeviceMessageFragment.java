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
import com.sunmi.assistant.mine.model.ChildrenBean;
import com.sunmi.assistant.mine.model.MessageCountBean;
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
import sunmi.common.view.SmRecyclerView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-14.
 */
@EFragment(R.layout.fragment_message)
public class DeviceMessageFragment extends BaseMvpFragment<MessageCountPresenter>
        implements MessageCountContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.tv_message)
    TextView tvMsg;
    @ViewById(R.id.rv_tab)
    RecyclerView rvTab;
    @ViewById(R.id.rv_msg)
    SmRecyclerView rvMsg;
    @ViewById(R.id.bga_refresh)
    BGARefreshLayout refreshLayout;

    private MessageCountBean.ModelCountListBean bean; //设备消息

    private List<ChildrenBean> msgData;
    private HashMap<String, List<ChildrenBean>> msgMap = new HashMap<>(); //用于存储对应的消息类型
    private MsgTabAdapter msgTabAdapter;
    private MsgContentAdapter msgContentAdapter;
    private List<String> tabTitle = new ArrayList<>();
    private List<Integer> msgCount = new ArrayList<>();
    private List<ChildrenBean> showData = new ArrayList<>();
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
        bean = data.getModelCountList().get(0);
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
            List<ChildrenBean> total = new ArrayList<>();
            tabTitle.add(getString(R.string.order_filter_all));
            msgCount.add(bean.getRemindUnreadCount());
            for (ChildrenBean bean : msgData) {
                if (bean.getTotalCount() > 0) {
                    total.addAll(bean.getChildren());
                    if (TextUtils.equals(bean.getModelName(), MsgConstants.NOTIFY_MODEL_IPC)) {
                        tabTitle.add(getString(R.string.msg_ipc));
                        msgCount.add(bean.getRemindUnreadCount());
                        msgMap.put(getString(R.string.msg_ipc), bean.getChildren());
                    } else if (TextUtils.equals(bean.getModelName(), MsgConstants.NOTIFY_MODEL_ESL)) {
                        tabTitle.add(getString(R.string.msg_esl));
                        msgCount.add(bean.getRemindUnreadCount());
                        msgMap.put(getString(R.string.msg_esl), bean.getChildren());
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
        List<ChildrenBean> beans = msgMap.get(selectTab);
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

                }
            });
            rvMsg.setAdapter(msgContentAdapter);
        } else {
            msgContentAdapter.notifyDataSetChanged();
        }

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
