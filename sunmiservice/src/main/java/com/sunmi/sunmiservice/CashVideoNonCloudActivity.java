package com.sunmi.sunmiservice;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.sunmiservice.cloud.WebViewCloudServiceActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import sunmi.common.base.BaseActivity;
import sunmi.common.constant.CommonConstants;

/**
 * Description:
 *
 * @author linyuanpeng on 2019-12-20.
 */
@EActivity(resName = "activity_cash_video_non_cloud")
public class CashVideoNonCloudActivity extends BaseActivity {

    @ViewById(resName = "tv_tip")
    TextView tvTip;
    @ViewById(resName = "btn_open")
    Button btnOpen;

    @Extra
    ArrayList<String> snList;
    @Extra
    int status;

    private final int REQ_OPEN_CLOUD = 0x200;


    @AfterViews
    void init() {
        if (status == CommonConstants.SERVICE_EXPIRED) {
            tvTip.setText(R.string.tip_cloud_expired);
            btnOpen.setText(R.string.str_renew_cloud_storage);
        }
    }

    @Click(resName = "btn_open")
    public void openClick() {
        WebViewCloudServiceActivity_.intent(context).mUrl(CommonConstants.H5_CLOUD_STORAGE).snList(snList).startForResult(REQ_OPEN_CLOUD);
    }

    @OnActivityResult(REQ_OPEN_CLOUD)
    public void onResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }
    }
}
