package com.sunmi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunmi.bean.ServiceDetailBean;
import com.sunmi.sunmiservice.R;
import com.sunmi.sunmiservice.cloud.ServiceDetailActivity_;

import java.util.List;

import sunmi.common.constant.CommonConstants;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class ServiceListAdapter extends BaseQuickAdapter<ServiceDetailBean, BaseViewHolder> {

    private Context context;
    private OnServiceClickListener listener;

    public ServiceListAdapter(List<ServiceDetailBean> data, Context context) {
        super(R.layout.item_service_detail, data);
        this.context = context;
    }


    public void setOnServiceClickListener(OnServiceClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, final ServiceDetailBean item) {
        Button btnRenewal = helper.getView(R.id.btn_renewal);
        TextView tvDeviceSn = helper.getView(R.id.tv_device_sn);
        TextView tvDeviceName = helper.getView(R.id.tv_device_name);
        helper.setText(R.id.tv_service_name, item.getServiceName());
        if (item.getStatus() != CommonConstants.SERVICE_EXPIRED) {
            helper.setText(R.id.tv_remaining, context.getString(R.string.str_remaining_validity_period,
                    DateTimeUtils.secondToPeriod(item.getValidTime())));
        } else {
            helper.setText(R.id.tv_remaining, R.string.str_expired);
            helper.setTextColor(R.id.tv_remaining, R.color.caution_primary);
        }
        if (item.isBind()) {
            helper.getView(R.id.tv_unbind).setVisibility(View.GONE);
            tvDeviceSn.setVisibility(View.VISIBLE);
            tvDeviceName.setVisibility(View.VISIBLE);
            btnRenewal.setVisibility(View.VISIBLE);
            tvDeviceSn.setText(context.getString(R.string.str_dev_sn, item.getDeviceSn()));
            tvDeviceName.setText(context.getString(R.string.ipc_device_name, item.getDeviceName()));
        } else {
            btnRenewal.setVisibility(View.GONE);
            tvDeviceSn.setVisibility(View.GONE);
            tvDeviceName.setVisibility(View.GONE);
            helper.getView(R.id.tv_unbind).setVisibility(View.VISIBLE);
        }
        btnRenewal.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRenewalClick(item);
            }
        });

        helper.getView(R.id.btn_setting_detail).setOnClickListener(v ->
                ServiceDetailActivity_.intent(context).mSn(item.getDeviceSn())
                        .deviceName(item.getDeviceName()).isBind(item.isBind()).start());

    }

    public interface OnServiceClickListener {
        void onRenewalClick(ServiceDetailBean bean);
    }

}
