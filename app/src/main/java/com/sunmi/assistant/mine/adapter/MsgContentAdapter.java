package com.sunmi.assistant.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.message.MsgConstants;
import com.sunmi.assistant.mine.model.MsgCountChildren;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.badgeview.BGABadgeRelativeLayout;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-08-19.
 */
public class MsgContentAdapter extends BaseQuickAdapter<MsgCountChildren, BaseViewHolder> {

    private Map<String, String> titleMap = new HashMap<>();
    private Context context;
    private OnMsgTypeClickListener listener;

    public MsgContentAdapter(List<MsgCountChildren> data, Context context) {
        super(R.layout.item_msg_content, data);
        this.context = context;
    }

    public interface OnMsgTypeClickListener {
        void onClick(int modelId, String title);
    }

    public void setOnMsgClickListener(OnMsgTypeClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MsgCountChildren item) {
        BGABadgeRelativeLayout rlMsg = helper.getView(R.id.rl_msg);
        if (item.getUnreadCount() > 0) {
            int count = item.getRemindUnreadCount();
            Log.e(TAG, item.getModelName() + count);
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
        String title = "";
        if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_TF_DETECT)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_ipc);
            title = context.getString(R.string.str_tf_detect);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_ipc_tf_detect);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_ON_OFFLINE)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_ipc);
            title = context.getString(R.string.str_ipc_on_offline);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_ipc_on_offline);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_DETECT_AUDIO)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_ipc);
            title = context.getString(R.string.str_ipc_audio);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_ipc_detect);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_DETECT_VIDEO)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_ipc);
            title = context.getString(R.string.str_ipc_video);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_ipc_detect);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_OTA)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_ipc);
            title = context.getString(R.string.str_device_ota);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_device_ota);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_ESL_ON_OFFLINE)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_esl);
            title = context.getString(R.string.str_esl_on_offline);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_esl_offline);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_ESL_OTA)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_esl);
            title = context.getString(R.string.str_device_ota);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_device_ota);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_TASK_ERP)) {
            helper.setImageResource(R.id.iv_msg, R.mipmap.ic_msg_task);
            title = context.getString(R.string.str_task_erp);
            titleMap.put(modelName, title);
            helper.setText(R.id.tv_msg_title, title);
            helper.setText(R.id.tv_msg_content, R.string.tip_task_erp);
        }
        helper.getView(R.id.rl_msg_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(item.getModelId(), titleMap.get(item.getModelName()));
                }
            }
        });
    }
}
