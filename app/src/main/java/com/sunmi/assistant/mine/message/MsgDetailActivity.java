package com.sunmi.assistant.mine.message;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.adapter.MsgDetailAdapter;
import com.sunmi.assistant.mine.contract.MessageDetailContract;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.presenter.MessageDetailPresenter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import me.leolin.shortcutbadger.ShortcutBadger;
import sunmi.common.base.BaseMvpActivity;
import sunmi.common.constant.CommonNotifications;
import sunmi.common.notification.BaseNotification;
import sunmi.common.utils.NetworkUtils;
import sunmi.common.utils.SpUtils;
import sunmi.common.view.TitleBarView;

@EActivity(R.layout.activity_msg_detail)
public class MsgDetailActivity extends BaseMvpActivity<MessageDetailPresenter>
        implements MessageDetailContract.View, BGARefreshLayout.BGARefreshLayoutDelegate {

    @ViewById(R.id.title_bar)
    TitleBarView titleBar;
    @ViewById(R.id.bga_refresh)
    BGARefreshLayout refreshLayout;
    @ViewById(R.id.rv_msg)
    RecyclerView rvMsg;
    @ViewById(R.id.tv_message)
    TextView tvMsg;
    @ViewById(R.id.layout_network_error)
    View networkError;

    @Extra
    int modelId;
    @Extra
    String title;
    @Extra
    String modelName;

    private MsgDetailAdapter adapter;
    private int pageNum = 1, pageSize = 10;
    private boolean loadFinish;
    List<MessageListBean.MsgListBean> dataList = new ArrayList<>();
    private int deletePosition;
    private BGARefreshViewHolder viewHolder;

    @AfterViews
    void init() {
        titleBar.setAppTitle(title);
        titleBar.getLeftLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPresenter = new MessageDetailPresenter();
        mPresenter.attachView(this);
        mPresenter.getMessageList(modelId, pageNum, pageSize, true);
        showLoadingDialog();
        refreshLayout.setDelegate(this);
        viewHolder = new BGANormalRefreshViewHolder(context, true);
        viewHolder.setLoadingMoreText(getString(R.string.str_loding_more));
        viewHolder.setLoadMoreBackgroundColorRes(R.color.bg_common);
        // 设置下拉刷新和上拉加载更多的风格(参数1：应用程序上下文，参数2：是否具有上拉加载更多功能)
        refreshLayout.setRefreshViewHolder(viewHolder);
        ShortcutBadger.applyCount(context, SpUtils.getRemindUnreadMsg()); //for 1.1.4+
    }

    @Override
    public void getMessageListSuccess(List<MessageListBean.MsgListBean> bean, int total, int returnCount, boolean needUpdate) {
        networkError.setVisibility(View.GONE);
        refreshLayout.endLoadingMore();
        refreshLayout.endRefreshing();
        if (total <= 0) {
            tvMsg.setVisibility(View.VISIBLE);
        } else {
            if (returnCount <= 0) {
                return;
            }
            if (pageNum == 1 || total > dataList.size()) {
                addData(bean);
                if (total > (pageNum - 1) * pageSize + returnCount) {
                    pageNum++;
                } else {
                    loadFinish = true;
                }
            }
            if (bean.get(0).getReceiveStatus() == 0 && needUpdate) {
                mPresenter.updateReceiveStatus(modelId);
            }
        }
    }

    @Override
    public void onBackPressed() {
        BaseNotification.newInstance().postNotificationName(CommonNotifications.msgReadedOrChange);
        finish();
    }

    @Override
    public void getMessageListFail(int code, String msg) {
        refreshLayout.endRefreshing();
        refreshLayout.endLoadingMore();
        if (dataList.size() <= 0) {
            networkError.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    protected void addData(List<MessageListBean.MsgListBean> beans) {
        if (beans.size() > 0) {
            initMsgDetail();
            dataList.addAll(beans);
            adapter.notifyDataSetChanged();
        }
    }

    private void initMsgDetail() {
        if (adapter == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvMsg.setLayoutManager(layoutManager);
            adapter = new MsgDetailAdapter(dataList, context);
            adapter.setMsgLongClickListener(new MsgDetailAdapter.OnMsgLongClickListener() {
                @Override
                public void onLongClick(View view, int msgId, int position) {
                    deletePosition = position;
                    PopupMenu popupMenu = msgDeleteMenu(view, msgId);
                    popupMenu.show();
                }
            });
            rvMsg.setAdapter(adapter);
        }
    }

    @Override
    public void deleteMessageSuccess() {
        dataList.remove(deletePosition);
        adapter.notifyItemRemoved(deletePosition);
    }

    @Click(R.id.btn_refresh)
    void refeshClick() {
        mPresenter.getMessageList(modelId, pageNum, pageSize, true);
    }

    @Override
    public void deleteMessageFail(int code, String msg) {
        shortTip(R.string.str_delete_fail);
    }

    @Override
    public void updateReceiveStatusSuccess() {

    }

    @Override
    public void updateReceiveStatusFail(int code, String msg) {

    }

    private PopupMenu msgDeleteMenu(View view, int msgId) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_msg_detail);
        popupMenu.setGravity(Gravity.CENTER);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.msg_delete) {
                    mPresenter.deleteMessage(msgId);
                }
                return true;
            }
        });

        return popupMenu;
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        pageNum = 1;
        pageSize = 10;
        mPresenter.getMessageList(modelId, pageNum, pageSize, true);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (NetworkUtils.isNetworkAvailable(context) && !loadFinish) {
            mPresenter.getMessageList(modelId, pageNum, pageSize, false);
            return true;
        }
        return false;
    }
}
