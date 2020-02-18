package com.sunmi.assistant.mine.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.assistant.R;
import com.sunmi.assistant.mine.model.MessageListBean;
import com.sunmi.assistant.mine.model.MsgTag;
import com.sunmi.assistant.utils.MessageUtils;
import com.sunmi.ipc.rpc.IpcCloudApi;
import com.sunmi.ipc.view.activity.DynamicVideoActivity_;
import com.sunmi.ipc.view.activity.setting.IpcSettingSdcardActivity_;

import java.util.List;
import java.util.Map;

import sunmi.common.model.SunmiDevice;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.ToastUtils;
import sunmi.common.view.dialog.CommonDialog;
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
        helper.setText(R.id.tv_msg_company, (companyName != null ? companyName : ""));
        helper.setText(R.id.tv_msg_shop, (shopName != null ? shopName : ""));
        helper.setText(R.id.tv_msg_device_model, context.getString(R.string.ipc_device_model, "--"));
        helper.setText(R.id.tv_msg_time, DateTimeUtils.secondToDate(item.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
        setMsgDetail(helper, MessageUtils.getInstance().getMsgFirst(titleTag.getTag()));
        //动态侦测视频
        Button btnPlay = helper.getView(R.id.btn_play);

        if (TextUtils.equals(item.getMajorButtonName(), "notif-device-ipc-tf-card-detect-tf-exist-btn1")) {
            btnPlay.setText(R.string.str_sd_format);
            btnPlay.setVisibility(View.VISIBLE);
            btnPlay.setOnClickListener(v -> showFormatDialog(item));
        } else if (!TextUtils.isEmpty(item.getMajorButtonLink()) && item.getMajorButtonLink().contains("url")) {
            btnPlay.setVisibility(View.VISIBLE);
            MsgTag urlTag = item.getMajorButtonLinkTag();
            Map<String, String> urlMap = urlTag.getMsgMap();
            String url = urlMap.get("url");
            String deviceModel = urlMap.get("device_model");
            helper.setText(R.id.tv_msg_device_model, context.getString(R.string.ipc_device_model, deviceModel));
            btnPlay.setOnClickListener(v -> {
                msgView.setVisibility(View.GONE);
                DynamicVideoActivity_.intent(context).url(url)
                        .deviceModel(deviceModel).start();
            });
        } else {
            btnPlay.setVisibility(View.GONE);
        }
        helper.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(helper.getView(R.id.tv_msg_detail),
                        item.getMsgId(), helper.getAdapterPosition());
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
        String eslCode = detailMap.get("esl_code");
        String productName = detailMap.get("product_name");
        String pushTime = detailMap.get("push_time");
        String detail;
        if (disconnectTime != null) {
            try {
                String time = DateTimeUtils.secondToDate(Long.parseLong(disconnectTime), "yyyy-MM-dd HH:mm:ss");
                detail = String.format(string, time);
            } catch (NumberFormatException e) {
                detail = String.format(string, disconnectTime);
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
                detail = String.format(string, binVersion);
            } catch (Exception e) {
                detail = "";
            }
        } else if (eslCode != null && pushTime != null) {
            try {
                String time = DateTimeUtils.secondToDate(Long.parseLong(pushTime), "yyyy-MM-dd HH:mm:ss");
                detail = String.format(string, eslCode, productName, time);
            } catch (NumberFormatException e) {
                detail = String.format(string, eslCode, productName, pushTime);
            }
        } else {
            detail = string;
        }
        helper.setText(R.id.tv_msg_device_name, context.getString(R.string.ipc_device_name, eslCode != null ? eslCode : deviceName));
        helper.setText(R.id.tv_msg_detail, context.getString(R.string.ipc_device_msg_content, detail));
    }

    public void showFormatDialog(MessageListBean.MsgListBean msgListBean) {
        new CommonDialog.Builder(context)
                .setTitle(com.sunmi.ipc.R.string.tip_sdcard_unformat)
                .setMessage(com.sunmi.ipc.R.string.msg_sdcard_should_format)
                .setCancelButton(com.sunmi.ipc.R.string.str_in_later)
                .setConfirmButton(com.sunmi.ipc.R.string.str_sd_format, (dialog, which) -> {
                    getIpcList(msgListBean);
                }).create().show();
    }

    private void getIpcList(MessageListBean.MsgListBean msgListBean) {
        MsgTag urlTag = msgListBean.getMajorButtonLinkTag();
        Map<String, String> urlMap = urlTag.getMsgMap();
        String sn = urlMap.get("device_sn");
        if (TextUtils.isEmpty(sn)) {
            return;
        }
        IpcCloudApi.getInstance().getDetailList(msgListBean.getCompanyId(), msgListBean.getShopId(),
                new RetrofitCallback<IpcListResp>() {
                    @Override
                    public void onSuccess(int code, String msg, IpcListResp data) {
                        if (data.getFs_list() != null && data.getFs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getFs_list()) {
                                if (TextUtils.equals(sn, bean.getSn())) {
                                    gotoSdcardActivity(sn, bean);
                                    return;
                                }
                            }
                        }
                        if (data.getSs_list() != null && data.getSs_list().size() > 0) {
                            for (IpcListResp.SsListBean bean : data.getSs_list()) {
                                if (TextUtils.equals(sn, bean.getSn())) {
                                    gotoSdcardActivity(sn, bean);
                                    return;
                                }
                            }
                        }
                        ToastUtils.toastForShort(context, R.string.tip_device_unbound_already);
                    }

                    @Override
                    public void onFail(int code, String msg, IpcListResp data) {

                    }
                });
    }

    private void gotoSdcardActivity(String sn, IpcListResp.SsListBean bean) {
        IpcSettingSdcardActivity_.intent(context).mDevice(getIpcDevice(bean)).start();
    }

    @NonNull
    private SunmiDevice getIpcDevice(IpcListResp.SsListBean bean) {
        SunmiDevice device = new SunmiDevice();
        device.setType("IPC");
        device.setStatus(bean.getActive_status());
        device.setDeviceid(bean.getSn());
        device.setModel(bean.getModel());
        device.setName(bean.getDevice_name());
        device.setImgPath(bean.getCdn_address());
        device.setUid(bean.getUid());
        device.setShopId(bean.getShop_id());
        device.setId(bean.getId());
        device.setFirmware(bean.getBin_version());
        return device;
    }

    public interface OnMsgLongClickListener {
        void onLongClick(View view, int msgId, int position);
    }

}
