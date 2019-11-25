package com.sunmi.ipc.view.activity;

import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-21.
 */
@EActivity(resName = "activity_ipc_start_config")
public class IpcStartConfigNetworkActivity extends BaseActivity {

    @ViewById(resName = "tv_content")
    TextView tvContent;
    @ViewById(resName = "tv_config_tip")
    TextView tvConfigTip;
    @ViewById(resName = "iv_ipc")
    ImageView ivIpc;

    @Extra
    int ipcType;
    @Extra
    int network;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (network == IpcConstants.IPC_WIRED_NETWORK) {
            tvContent.setText(R.string.tip_use_wired_network);
            tvConfigTip.setText(R.string.str_config_tip_ipc_2);
            if (ipcType == CommonConstants.TYPE_IPC_FS) {
                ivIpc.setBackgroundResource(R.drawable.ic_ipc_config_fs_wired);
            } else {
                ivIpc.setBackgroundResource(R.drawable.ic_ipc_config_ss_wired);
            }
        } else {
            tvContent.setText(R.string.tip_use_wireless_network);
            tvConfigTip.setText(Html.fromHtml(getString(R.string.str_config_tip_ipc_3)));
            if (ipcType == CommonConstants.TYPE_IPC_FS) {
                ivIpc.setBackgroundResource(R.drawable.ic_ipc_config_fs_wireless);
            } else {
                ivIpc.setBackgroundResource(R.drawable.ic_ipc_config_ss_wireless);
            }
        }
    }

    @Click(resName = "btn_start")
    public void nextClick() {
        if (isFastClick(500)) {
            return;
        }
        if (network == IpcConstants.IPC_WIRED_NETWORK) {
            IpcStartLinkedActivity_.intent(context).ipcType(ipcType).start();
        } else {
            IPCSearchActivity_.intent(context).deviceType(ipcType).network(network).shopId(SpUtils.getShopId() + "").start();
        }

    }

}
