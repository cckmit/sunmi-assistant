package com.sunmi.ipc.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;

/**
 * Description: WifiConfigCompletedActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_wifi_config_completed")
public class WifiConfigCompletedActivity extends BaseActivity {

    @ViewById(resName = "rv_result")
    RecyclerView rvResult;

    @Extra
    String shopId;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;

    private List<SunmiDevice> list = new ArrayList<>();

    @AfterViews
    void init() {
        list = sunmiDevices;
        initList();
    }

    @Click(resName = "btn_complete")
    void completeClick() {
        GotoActivityUtils.gotoMainActivity(context);
        finish();
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvResult.setLayoutManager(layoutManager);
        rvResult.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rvResult.setAdapter(new CommonListAdapter<SunmiDevice>(context,
                R.layout.item_device_config_complete, list) {
            @Override
            public void convert(ViewHolder holder, final SunmiDevice device) {
                holder.setText(R.id.tv_name, device.getName());
                holder.getView(R.id.tv_adjust).setVisibility(View.GONE);
                if (TextUtils.equals("FS1", device.getModel())) {
                    holder.setImageResource(R.id.iv_device, R.mipmap.item_fs);
                } else if (TextUtils.equals("SS1", device.getModel())) {
                    holder.setImageResource(R.id.iv_device, R.mipmap.item_ss);
                }
                if (device.getStatus() == 1) {
                    holder.setText(R.id.tv_status, "添加成功");
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_done);
                    if (TextUtils.equals("FS1", device.getModel())) {
                        holder.getView(R.id.tv_adjust).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_adjust).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                } else {
                    String errStr = "绑定失败";
                    if (device.getStatus() == 5508) {
                        errStr = "已经绑定，不要重复绑定";
                    } else if (device.getStatus() == 5501) {
                        errStr = "设备不存在";
                    } else if (device.getStatus() == 5510) {
                        errStr = "已被其他用户绑定";
                    } else if (device.getStatus() == RpcErrorCode.RPC_ERR_TIMEOUT) {
                        errStr = "绑定超时";
                    }
                    holder.setText(R.id.tv_status, errStr);
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_error);
                }
            }
        });
    }

}
