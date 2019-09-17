package com.sunmi.assistant.mine.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.model.MsgTag;
import com.sunmi.assistant.utils.MessageUtils;
import com.sunmi.ipc.dynamic.DynamicVideoActivity_;

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
        MsgTag titleTag = item.getTitleTag();
        MsgTag detailTag = item.getDetailTag();
        Map<String, String> titleMap = titleTag.getMsgMap();
        detailMap = detailTag.getMsgMap();
        String companyName = titleMap.get("company_name");
        String shopName = titleMap.get("shop_name");
        helper.setText(R.id.tv_msg_title, (companyName != null ? companyName : ""));
        helper.setText(R.id.tv_msg_shop, (shopName != null ? shopName : ""));
        helper.setText(R.id.tv_msg_time, DateTimeUtils.secondToDate(item.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
        setMsgDetail(helper, MessageUtils.getInstance().getMsgFirst(titleTag.getTag()));
        //动态侦测视频
        if (!TextUtils.isEmpty(item.getMajorButtonLink())) {
            helper.itemView.setOnClickListener(v -> {
                msgView.setVisibility(View.GONE);
                String[] link = item.getMajorButtonLink().split("&");
                for (int i = 0; i < link.length; i++) {
                    if (TextUtils.isEmpty(link[2]) || TextUtils.isEmpty(link[3])) {
                        return;
                    }
                    String url = MsgTag.getUrlDecoderString(link[2].substring(4));//url=
                    String deviceModel = link[3].substring(13);//device_model=
                    DynamicVideoActivity_.intent(context)
                            .url(url)
                            .deviceModel(deviceModel)
                            .start();
                }
            });
        }
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
            try {
                detail = String.format(string, deviceName, binVersion);
            } catch (Exception e) {
                detail = "";
            }
        } else {
            try {
                detail = String.format(string, deviceName);
            } catch (Exception e) {
                detail = "";
            }
        }
        helper.setText(R.id.tv_msg_detail, detail);
    }

    public interface OnMsgLongClickListener {
        void onLongClick(View view, int msgId, int position);
    }
}
