package sunmi.common.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.commonlibrary.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.view.TitleBarView;

/**
 * Created by YangShiJie on 2019/3/29.
 * 主路由器开始配置
 */
@EActivity(resName = "activity_start_config_sm_device")
public class StartConfigSMDeviceActivity extends BaseActivity {
    @ViewById(resName = "title_bar")
    TitleBarView titleBar;
    @ViewById(resName = "iv_image")
    ImageView ivImage;
    @ViewById(resName = "tv_tip_1")
    TextView tvTip1;
    @ViewById(resName = "tv_tip_2")
    TextView tvTip2;
    @ViewById(resName = "tv_tip_3")
    TextView tvTip3;
    @ViewById(resName = "tv_config_tip")
    TextView tvConfigTip;

    @Extra
    String shopId;
    @Extra
    int deviceType;

    @AfterViews
    protected void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        if (deviceType == CommonConstants.TYPE_AP) {
            titleBar.setAppTitle(R.string.str_title_ap_set);
            ivImage.setImageResource(R.mipmap.ic_device_config_ap);
            tvTip1.setText(R.string.str_config_tip_ap);
            tvTip2.setText(Html.fromHtml(getString(R.string.str_config_tip_ap_1)));
            tvTip3.setText(Html.fromHtml(getString(R.string.str_config_tip_ap_2)));
            tvConfigTip.setVisibility(View.GONE);
        } else if (deviceType == CommonConstants.TYPE_IPC) {
            titleBar.setAppTitle(R.string.str_title_ipc_set);
            ivImage.setImageResource(R.mipmap.ic_device_config_ipc);
            tvTip1.setText(R.string.str_config_tip_ipc);
            tvTip2.setText(Html.fromHtml(getString(R.string.str_config_tip_ipc_1)));
            tvTip3.setText(Html.fromHtml(getString(R.string.str_config_tip_ap_2)));
        } else if (deviceType == CommonConstants.TYPE_PRINTER) {
            titleBar.setAppTitle(R.string.str_title_printer_set);
            ivImage.setImageResource(R.mipmap.ic_device_config_printer);
            tvTip1.setText(R.string.str_config_tip_printer);
            tvTip2.setText(Html.fromHtml(getString(R.string.str_config_tip_printer_1)));
            tvTip3.setText(Html.fromHtml(getString(R.string.str_config_tip_printer_2)));
        }
    }

    @Click(resName = "tv_config_tip")
    public void configTipClick(View v) {
        configChildDialog();
    }

    @Click(resName = "btn_start")
    public void nextClick(View v) {
        if (deviceType == CommonConstants.TYPE_AP) {
            startPrimaryRouteSearchActivity();
        } else if (deviceType == CommonConstants.TYPE_IPC) {
            try {
                Class<?> ipcSearchActivity = Class.forName("com.sunmi.ipc.view.IPCSearchActivity_");
                Intent intent = new Intent(context, ipcSearchActivity);
                intent.putExtra("shopId", shopId);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            SearchSMDeviceActivity_.intent(context).deviceType(deviceType).start();
        } else if (deviceType == CommonConstants.TYPE_PRINTER) {
            try {
                Class<?> printerSearchActivity = Class.forName("com.sunmi.cloudprinter.ui.Activity.PrinterSearchActivity_");
                Intent intent = new Intent(context, printerSearchActivity);
                intent.putExtra("shopId", shopId);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startPrimaryRouteSearchActivity() {
        try {
            Class<?> mainActivity = Class.forName("com.sunmi.apmanager.ui.activity.config.PrimaryRouteSearchActivity");
            Intent intent = new Intent(context, mainActivity);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configChildDialog() {
        final Dialog mDialog = new Dialog(this, R.style.Son_dialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_has_sunmi_ap, null);
        ImageView ivClose = view.findViewById(R.id.iv_close);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.show();
    }

}
