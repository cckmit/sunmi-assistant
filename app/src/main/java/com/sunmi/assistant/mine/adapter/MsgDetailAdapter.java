package com.sunmi.assistant.mine.adapter;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.model.MsgTag;
import com.sunmi.assistant.utils.MessageUtils;

import java.util.List;
import java.util.Map;

import sunmi.common.utils.DateTimeUtils;
import sunmi.common.view.tablayout.utils.UnreadMsgUtils;
import sunmi.common.view.tablayout.widget.MsgView;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-21.
 */
public class MsgDetailAdapter extends BaseQuickAdapter<MessageListBean.MsgListBean, BaseViewHolder> {

    private Context context;
    private Map<String, String> detailMap;
    private OnMsgLongClickListener listener;

    public MsgDetailAdapter(List<MessageListBean.MsgListBean> data, Context context) {
        super(R.layout.item_messgae_detail, data);
        this.context = context;
    }

    public interface OnMsgLongClickListener {
        void onLongClick(View view, int msgId, int position);
    }

    public void setMsgLongClickListener(OnMsgLongClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageListBean.MsgListBean item) {
        MsgView msgView = helper.getView(R.id.msg_view);
        if (item.getReceiveStatus() == 0) {
            UnreadMsgUtils.show(msgView, 0);
        } else {
            msgView.setVisibility(View.GONE);
        }
        MsgTag titleTag = new MsgTag(item.getTitle());
        Map<String, String> titleMap = titleTag.getMsgMap();
        MsgTag detailTag = new MsgTag(item.getContent());
        detailMap = detailTag.getMsgMap();
        helper.setText(R.id.tv_msg_title, titleMap.get("company_name") + titleMap.get("shop_name"));
        helper.setText(R.id.tv_msg_time, DateTimeUtils.secondToDate(item.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
        setMsgDetail(helper, MessageUtils.getInstance().getMsgFirst(titleTag.getTag()));
        helper.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(helper.getView(R.id.tv_msg_detail), item.getMsgId(), helper.getAdapterPosition());
            }
            return false;
        });

    }

    private void setMsgDetail(BaseViewHolder helper, String string) {
        String disconnectTime = detailMap.get("disconnect_time");
        String deviceName = detailMap.get("device_name");
        String binVersion = detailMap.get("bin_version");
        String timestamp = detailMap.get("timestamp");
        String saasName = detailMap.get("saas_name");
        String totalCount = detailMap.get("total_count");
        String detail;
        if (disconnectTime != null) {
            try {
                String time = DateTimeUtils.secondToDate(Long.parseLong(disconnectTime), "yyyy-MM-dd HH:mm:ss");
                detail = String.format(string, deviceName, time);
            } catch (NumberFormatException e) {
                detail = String.format(string, deviceName, disconnectTime);
            }
        } else if (timestamp != null && saasName != null && totalCount != null) {
            try {
                String time = DateTimeUtils.secondToDate(Long.parseLong(timestamp), "yyyy-MM-dd HH:mm:ss");
                detail = String.format(string, time, saasName, totalCount);
            } catch (NumberFormatException e) {
                detail = String.format(string, timestamp, saasName, totalCount);
            }
        } else if (binVersion != null) {
            detail = String.format(string, deviceName, binVersion);
        } else {
            detail = String.format(string, deviceName);
        }
        helper.setText(R.id.tv_msg_detail, detail);
    }
}
