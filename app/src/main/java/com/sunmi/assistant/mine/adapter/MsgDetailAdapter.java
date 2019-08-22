package com.sunmi.assistant.mine.adapter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.message.MsgConstants;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.model.MsgTag;

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
        String modelName = item.getModelName();
        MsgTag titleTag = new MsgTag(item.getTitle());
        Map<String, String> titleMap = titleTag.getMsgMap();
        MsgTag detailTag = new MsgTag(item.getContent());
        detailMap = detailTag.getMsgMap();
        String title = titleTag.getTag();
        helper.setText(R.id.tv_msg_title, titleMap.get("company_name") + titleMap.get("shop_name"));
        helper.setText(R.id.tv_msg_time, DateTimeUtils.secondToDate(item.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
        if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_TF_DETECT)) {
            if (title.contains(MsgConstants.TF_NON_EXIST)) {
                setMsgDetail(helper, R.string.msg_ipc_tf_non_exist);
            } else if (title.contains(MsgConstants.TF_EXIST)) {
                setMsgDetail(helper, R.string.msg_ipc_tf_exist);
            } else if (title.contains(MsgConstants.TF_CAPABLE)) {
                setMsgDetail(helper, R.string.msg_ipc_tf_capable);
            } else if (title.contains(MsgConstants.TF_NON_CAPABLE)) {
                setMsgDetail(helper, R.string.msg_ipc_tf_non_capable);
            }
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_ON_OFFLINE)) {
            setMsgDetail(helper, R.string.msg_ipc_offline);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_DETECT_AUDIO)) {
            setMsgDetail(helper, R.string.msg_ipc_pic_detect);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_DETECT_VIDEO)) {
            setMsgDetail(helper, R.string.msg_ipc_pic_detect);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_IPC_OTA)) {
            setMsgDetail(helper, R.string.msg_ipc_ota);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_ESL_ON_OFFLINE)) {
            setMsgDetail(helper, R.string.msg_esl_offline);
        } else if (TextUtils.equals(modelName, MsgConstants.NOTIFY_ESL_OTA)) {
            setMsgDetail(helper, R.string.msg_esl_ota);
        }
        helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongClick(helper.getView(R.id.tv_msg_detail), item.getMsgId(), helper.getAdapterPosition());
                }
                return false;
            }
        });

    }

    private void setMsgDetail(BaseViewHolder helper, @StringRes int resId) {
        String disconnectTime = detailMap.get("disconnect_time");
        String deviceName = detailMap.get("device_name");
        String binVersion = detailMap.get("bin_version");
        String detail;
        if (disconnectTime != null) {
            try {
                String time = DateTimeUtils.secondToDate(Long.parseLong(disconnectTime), "yyyy-MM-dd HH:mm:ss");
                detail = context.getString(resId, deviceName, time);
            } catch (NumberFormatException e) {
                detail = context.getString(resId, deviceName, disconnectTime);
            }
        } else if (binVersion != null) {
            detail = context.getString(resId, deviceName, binVersion);
        } else {
            detail = context.getString(resId, deviceName);
        }
        helper.setText(R.id.tv_msg_detail, detail);
    }
}
