package com.sunmi.ipc.view;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.sunmi.ipc.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.RpcErrorCode;
import sunmi.common.utils.GotoActivityUtils;
import sunmi.common.utils.SunmiDevUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.activity.StartConfigSMDeviceActivity_;

/**
 * Description: IpcConfigCompletedActivity
 * Created by Bruce on 2019/3/31.
 */
@EActivity(resName = "activity_ipc_config_completed")
public class IpcConfigCompletedActivity extends BaseActivity {

    @ViewById(resName = "rv_result")
    RecyclerView rvResult;
    @ViewById(resName = "btn_complete")
    Button btnComplete;
    @ViewById(resName = "btn_retry")
    Button btnRetry;
    @ViewById(resName = "btn_finish")
    Button btnFinish;

    @Extra
    String shopId;
    @Extra
    boolean isSunmiLink;
    @Extra
    ArrayList<SunmiDevice> sunmiDevices;

    private List<SunmiDevice> list = new ArrayList<>();
    private int failCount;

    @AfterViews
    void init() {
        if (sunmiDevices != null) {
            for (SunmiDevice sm : sunmiDevices) {
                if (sm.getStatus() != 1) {
                    failCount++;
                }
            }
            if (failCount == sunmiDevices.size()) {
                btnFinish.setVisibility(View.VISIBLE);
                btnRetry.setVisibility(View.VISIBLE);
            } else {
                btnComplete.setVisibility(View.VISIBLE);
            }
        }
        list = sunmiDevices;
        initList();
    }

    @Click(resName = "btn_complete")
    void completeClick() {
        GotoActivityUtils.gotoMainActivity(context);
        finish();
    }

    @Click(resName = "btn_finish")
    void finishClick() {
        GotoActivityUtils.gotoMainActivity(context);
        finish();
    }

    @Click(resName = "btn_retry")
    void retryClick() {
        if (isSunmiLink) {
            setResult(RESULT_OK);
        } else
            StartConfigSMDeviceActivity_.intent(context)
                    .deviceType(CommonConstants.TYPE_IPC).shopId(shopId).start();
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
                holder.setText(R.id.tv_name, device.getModel());
                holder.getView(R.id.tv_adjust).setVisibility(View.GONE);
                holder.setImageResource(R.id.iv_device, SunmiDevUtils.setSearchLogo(device.getModel()));
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
                    String errStr = getString(R.string.str_bind_fail);
                    if (device.getStatus() == 5501) {
                        errStr = getString(R.string.tip_device_not_exist);
                    } else if (device.getStatus() == 5508 || device.getStatus() == 5509) {
                        errStr = getString(R.string.tip_already_bound);
                    } else if (device.getStatus() == 5510) {
                        errStr = getString(R.string.tip_bound_by_others);
                    } else if (device.getStatus() == 5511) {
                        errStr = getString(R.string.tip_device_offline);
                    } else if (device.getStatus() == RpcErrorCode.RPC_ERR_TIMEOUT) {
                        errStr = getString(R.string.tip_bind_timeout);
                    }
                    holder.setText(R.id.tv_status, errStr);
                    holder.setImageResource(R.id.iv_status, R.mipmap.ic_error);
                }
            }
        });
    }

}
