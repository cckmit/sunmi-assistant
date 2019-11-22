package com.sunmi.assistant.pos;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sunmi.assistant.R;
import com.sunmi.assistant.pos.data.PosApi;
import com.sunmi.assistant.pos.response.PosDetailsResp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import sunmi.common.base.BaseActivity;
import sunmi.common.model.SunmiDevice;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DeviceTypeUtils;
import sunmi.common.utils.StatusBarUtils;
import sunmi.common.utils.log.LogCat;
import sunmi.common.view.SettingItemLayout;

/**
 * @author yangShiJie
 * @date 2019-11-19
 */
@SuppressLint("Registered")
@EActivity(R.layout.pos_activity_details)
public class PosDetailsActivity extends BaseActivity {
    @ViewById(R.id.tv_pos_type)
    TextView tvPosType;
    @ViewById(R.id.tv_pos_name)
    TextView tvPosName;
    @ViewById(R.id.iv_pos)
    ImageView ivPos;
    @ViewById(R.id.sil_wifi)
    SettingItemLayout silWifi;
    @ViewById(R.id.sil_adds)
    SettingItemLayout silAdds;
    @ViewById(R.id.sil_sn)
    SettingItemLayout silSn;
    @ViewById(R.id.sil_resolution)
    SettingItemLayout silResolution;
    @ViewById(R.id.sil_temper)
    SettingItemLayout silTemper;
    @ViewById(R.id.sil_sys_version)
    SettingItemLayout silSysVer;
    @ViewById(R.id.sil_rom)
    SettingItemLayout silRom;
    @ViewById(R.id.sil_ip)
    SettingItemLayout silIp;
    @ViewById(R.id.sil_mac)
    SettingItemLayout silMac;
    @ViewById(R.id.sil_imei)
    SettingItemLayout silIMEI;
    @Extra
    SunmiDevice device;
    @Extra
    PosDetailsResp posResp;

    @AfterViews
    void init() {
        StatusBarUtils.setStatusBarColor(this, StatusBarUtils.TYPE_DARK);
        getPosType();
        tvPosName.setText(device.getModel());
        //ivPos.setImageResource(DeviceTypeUtils.getInstance().getSunmiDeviceImage(device.getModel()));
        //Glide.with(context).load(device.getImgPath()).into(ivPos);
        if (posResp != null) {
            //是否在使用有线网 0:不使用 1:使用
            int useEthernet = posResp.getNetInfo().getUseEthernet();
            //是否在使用wifi 0:不使用 1:使用
            int wifi = posResp.getNetInfo().getUseWifi();
            //是否在使用3g 0:不使用 1:使用
            int use3g = posResp.getNetInfo().getUse3g();
            String netConnectStatus;
            if (useEthernet == 1) {
                netConnectStatus = getString(R.string.str_text_white);
            } else if (wifi == 1) {
                netConnectStatus = getString(R.string.pos_wifi);
            } else if (use3g == 1) {
                netConnectStatus = getString(R.string.pos_3g);
            } else {
                netConnectStatus = getString(R.string.pos_unknown);
            }
            silWifi.setContent(netConnectStatus);
            silAdds.setContent(posResp.getDeviceInfo().getLocation());
            silSn.setContent(device.getDeviceid());
            silResolution.setContent(posResp.getDeviceInfo().getResolution());
            silTemper.setContent(posResp.getRunningInfo().getBatteryTemper());
            silSysVer.setContent("Android " + posResp.getDeviceInfo().getSystemVersion());
            silRom.setContent(posResp.getDeviceInfo().getRomVersion());
            silIp.setContent(posResp.getNetInfo().getIp());
            silMac.setContent(posResp.getNetInfo().getMac().toUpperCase());
            silIMEI.setContent(posResp.getDeviceInfo().getImei());
        }
    }

    private void getPosType() {
        String[] arrays = {"\"" + device.getModel() + "\""};
        PosApi.getInstance().getCategoryByModel(arrays, new RetrofitCallback<Object>() {
            @Override
            public void onSuccess(int code, String msg, Object data) {
                LogCat.e("TAG", "11111 code=" + code + " , msg=" + data.toString());
                String mData = data.toString();
                if (mData.contains(device.getModel())) {
                    String tag = mData.substring(mData.indexOf("tag=") + 4, mData.indexOf(","));
                    String imgUrl = mData.substring(mData.indexOf("url=") + 4, mData.indexOf("}")).trim();
                    devTag(tag, imgUrl);
                }
            }

            @Override
            public void onFail(int code, String msg, Object data) {
                LogCat.e("TAG", "code=" + code + " , msg=" + msg);
            }
        });
    }

    /**
     * Mobile_Products(手持非金融设备)
     * Smart_Payment_Products(手持金融设备)
     * Desktop_Products(台式收银设备)
     */
    private void devTag(String tag, String imgUrl) {
        if ("Mobile_Products".equalsIgnoreCase(tag)) {
            tvPosType.setText(R.string.pos_mobile_product);
        } else if ("Smart_Payment_Products".equalsIgnoreCase(tag)) {
            tvPosType.setText(R.string.pos_smart_payment_product);
        } else if ("Desktop_Products".equalsIgnoreCase(tag)) {
            tvPosType.setText(R.string.pos_desktop_product);
        }
        if (TextUtils.isEmpty(imgUrl)) {
            ivPos.setImageResource(DeviceTypeUtils.getInstance().getSunmiDeviceImage(device.getModel()));
        } else {
            Glide.with(context).load(imgUrl).into(ivPos);
        }
    }
}
