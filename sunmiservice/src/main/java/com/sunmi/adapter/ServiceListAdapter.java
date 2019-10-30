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

import org.litepal.crud.DataSupport;

import java.util.List;

import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.DateTimeUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-10-22.
 */
public class ServiceListAdapter extends BaseQuickAdapter<ServiceDetailBean, BaseViewHolder> {

    private Context context;
    private OnServiceClickListener listener;
    private List<SunmiDevice> devices;

    public ServiceListAdapter(List<ServiceDetailBean> data, Context context, List<SunmiDevice> devices) {
        super(R.layout.item_service_detail, data);
        this.context = context;
        this.devices = devices;
    }

    public void setDevices(List<SunmiDevice> devices) {
        this.devices = devices;
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
        if (item.getStatus() != 3) {
            helper.setText(R.id.tv_remaining, context.getString(R.string.str_remaining_validity_period,
                    DateTimeUtils.secondToPeriod(item.getValidTime(), context)));
        } else {
            helper.setText(R.id.tv_remaining, R.string.str_expired);
            helper.setTextColor(R.id.tv_remaining, R.color.caution_primary);
        }
        final String sn = item.getDeviceSn();
        btnRenewal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRenewalClick(item);
                }
            }
        });

        helper.getView(R.id.btn_setting_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceDetailActivity_.intent(context).mSn(sn).start();
            }
        });
        if (devices.size() <= 0) {
            tvDeviceSn.setText(context.getString(R.string.ipc_sn, sn));
            tvDeviceName.setText(context.getString(R.string.ipc_device_name, "- -"));
            return;
        }
        SunmiDevice device = DataSupport.where("deviceid=?", sn).findFirst(SunmiDevice.class);
        if (device != null) {
            tvDeviceSn.setText(context.getString(R.string.ipc_sn, sn));
            tvDeviceName.setText(context.getString(R.string.ipc_device_name, device.getName()));
        } else {
            btnRenewal.setVisibility(View.GONE);
            tvDeviceSn.setVisibility(View.GONE);
            tvDeviceName.setVisibility(View.GONE);
            helper.getView(R.id.tv_unbind).setVisibility(View.VISIBLE);
        }
    }

    public interface OnServiceClickListener {
        void onRenewalClick(ServiceDetailBean bean);
    }

}
