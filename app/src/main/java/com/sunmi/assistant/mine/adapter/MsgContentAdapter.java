package com.sunmi.assistant.mine.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.message.MsgConstants;
import com.sunmi.assistant.mine.model.MsgCountChildren;
import com.sunmi.assistant.utils.MessageUtils;

import java.util.List;

import cn.bingoogolapple.badgeview.BGABadgeRelativeLayout;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-19.
 */
public class MsgContentAdapter extends BaseQuickAdapter<MsgCountChildren, BaseViewHolder> {

    private Context context;
    private OnMsgTypeClickListener listener;

    public MsgContentAdapter(List<MsgCountChildren> data, Context context) {
        super(R.layout.item_msg_content, data);
        this.context = context;
    }

    public interface OnMsgTypeClickListener {
        void onClick(int modelId, String modelName);
    }

    public void setOnMsgClickListener(OnMsgTypeClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgCountChildren item) {
        BGABadgeRelativeLayout rlMsg = helper.getView(R.id.rl_msg);
        if (item.getUnreadCount() > 0) {
            int count = item.getRemindUnreadCount();
            if (count <= 0) {
                rlMsg.showCirclePointBadge();
            } else if (count > 99) {
                rlMsg.showTextBadge("99+");
            } else {
                rlMsg.showTextBadge(String.valueOf(count));
            }
        } else {
            rlMsg.hiddenBadge();
        }
        helper.setText(R.id.tv_msg_time, DateTimeUtils.secondToDateMsg(item.getLastReceiveTime()));
        String modelName = item.getModelName();
        helper.setText(R.id.tv_msg_title, MessageUtils.getInstance().getMsgFirst(modelName));
        helper.setText(R.id.tv_msg_content, MessageUtils.getInstance().getMsgSecond(modelName));
        if (modelName.contains(MsgConstants.NOTIFY_DEVICE_IPC)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_ipc);
        } else if (modelName.contains(MsgConstants.NOTIFY_DEVICE_ESL)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_esl);
        } else if (modelName.contains(MsgConstants.NOTIFY_SYSTEM_TASK)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_task);
        }
        helper.getView(R.id.rl_msg_content).setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item.getModelId(), modelName);
            }
        });
    }
}
