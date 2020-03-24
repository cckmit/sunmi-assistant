package com.sunmi.ipc.view.activity;

import android.graphics.Paint;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunmi.ipc.R;
import com.sunmi.ipc.config.IpcConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.enums.ModelType;
import sunmi.common.utils.SpUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.CommonListAdapter;
import sunmi.common.view.ViewHolder;
import sunmi.common.view.dialog.ListDialog;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-11-21.
 */
@EActivity(resName = "activity_ipc_start_config")
public class IpcStartLinkedActivity extends BaseActivity {

    @ViewById(resName = "tv_content")
    TextView tvContent;
    @ViewById(resName = "tv_config_tip")
    TextView tvConfigTip;
    @ViewById(resName = "tv_indicator_light")
    TextView tvIndicator;
    @ViewById(resName = "iv_ipc")
    ImageView ivIpc;

    @Extra
    int ipcType;
    @Extra
    int source;

    private List<CharSequence> list = new ArrayList<>();

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        tvContent.setText(R.string.str_confirm_ipc_linked);
        tvConfigTip.setText(Html.fromHtml(getString(R.string.str_config_tip_ipc_4)));
        if (ipcType == ModelType.MODEL_FS) {
            ivIpc.setBackgroundResource(R.drawable.ic_ipc_config_fs_blue);
        } else {
            ivIpc.setBackgroundResource(R.drawable.ic_ipc_config_ss_blue);
        }
        tvIndicator.setVisibility(View.VISIBLE);
        tvIndicator.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tvIndicator.getPaint().setAntiAlias(true);
        list.add(getString(R.string.tip_config_ipc_dialog4));
        list.add(Html.fromHtml(getString(R.string.tip_config_ipc_dialog3)));
    }

    @Click(resName = "btn_start")
    public void nextClick() {
        if (isFastClick(500)) {
            return;
        }
        IPCSearchActivity_.intent(context).deviceType(ipcType).network(IpcConstants.IPC_CONFIG_MODE_WIRED)
                .shopId(SpUtils.getShopId() + "").source(source).start();
    }

    @Click(resName = "tv_indicator_light")
    public void indicatorClick() {
        final CommonListAdapter adapter = new CommonListAdapter<CharSequence>(context, R.layout.item_ipc_config_dialog, list) {
            @Override
            public void convert(ViewHolder holder, CharSequence charSequence) {
                holder.setText(R.id.tv_content, charSequence);
            }
        };
        new ListDialog.Builder<CommonListAdapter>(context)
                .setTitle(R.string.str_ipc_config_dialog_title)
                .setItemDecoration(false)
                .setListHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setAdapter(adapter)
                .setConfirmButton(R.string.str_confirm)
                .create().show();
    }

}
