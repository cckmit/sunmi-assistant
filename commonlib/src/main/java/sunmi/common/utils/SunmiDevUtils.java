package sunmi.common.utils;

import android.text.TextUtils;

import com.commonlibrary.R;

/**
 * 搜索商米设备logo
 * Created by YangShiJie on 2019/1/24.
 */
public class SunmiDevUtils {
    // D1 ,D1s D2,L2  ,M1 ,M2,P1, P2 lite,S2,T1,T2 lite ,T2 mini, T2, UNKONWN,V1 ,V1S,V2-por
    public static int setSearchLogo(String model) {
        if (TextUtils.isEmpty(model)) {
            return R.mipmap.unknow;
        } else if (model.equalsIgnoreCase("W1")) {
            return R.mipmap.ic_sunmi_w1;
        } else if (model.equalsIgnoreCase("D1")) {
            return R.mipmap.ic_sunmi_d1;
        } else if (model.equalsIgnoreCase("D1s")) {
            return R.mipmap.ic_sunmi_d1s;
        } else if (model.equalsIgnoreCase("D2")) {
            return R.mipmap.ic_sunmi_d2;
        } else if (model.equalsIgnoreCase("L2")) {
            return R.mipmap.ic_sunmi_l2;
        } else if (model.equalsIgnoreCase("M1")) {
            return R.mipmap.ic_sunmi_m1;
        } else if (model.equalsIgnoreCase("M2")) {
            return R.mipmap.ic_sunmi_m2;
        } else if (model.equalsIgnoreCase("P1")) {
            return R.mipmap.ic_sunmi_p1;
        } else if (model.equalsIgnoreCase("P2 lite")) {
            return R.mipmap.ic_sunmi_p2_lite;
        } else if (model.equalsIgnoreCase("S2")) {
            return R.mipmap.ic_sunmi_s2;
        } else if (model.equalsIgnoreCase("T1")) {
            return R.mipmap.ic_sunmi_t1;
        } else if (model.equalsIgnoreCase("T2 lite")) {
            return R.mipmap.ic_sunmi_t2_lite;
        } else if (model.equalsIgnoreCase("T2 mini")) {
            return R.mipmap.ic_sunmi_t2_mini;
        } else if (model.equalsIgnoreCase("T2")) {
            return R.mipmap.ic_sunmi_t2;
        } else if (model.equalsIgnoreCase("V1")) {
            return R.mipmap.ic_sunmi_v1;
        } else if (model.equalsIgnoreCase("V1S")) {
            return R.mipmap.ic_sunmi_v1s;
        } else if (model.equalsIgnoreCase("V2")) {
            return R.mipmap.ic_sunmi_v2_pro;
        } else if (model.equalsIgnoreCase("V2_PRO")) {
            return R.mipmap.ic_sunmi_v2_pro;
        } else if (model.equalsIgnoreCase("SS1")) {
            return R.mipmap.ic_sunmi_ss;
        } else if (model.equalsIgnoreCase("FS1")) {
            return R.mipmap.ic_sunmi_fs;
        } else {
            return R.mipmap.unknow;
        }
    }

}
