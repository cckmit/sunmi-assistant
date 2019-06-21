package sunmi.common.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import sunmi.common.base.BaseApplication;
import sunmi.common.utils.log.LogCat;

/**
 * Created by YangShiJie on 2019/4/10.
 * 多端登录剔除，针对部分手机无法后台默认自启动如：Oppo等
 */
public class GotoActivityUtils {

    public static void gotoLoginActivity(String className) {
        if (TextUtils.isEmpty(SpUtils.getLoginStatus())
                && !className.contains("LoginActivity")
                && !className.contains("LeadPagesActivity")
                && !className.contains("WelcomeActivity")
                && !className.contains("RegisterActivity")
                && !className.contains("SendSmsLoginActivity")
                && !className.contains("RetrievePasswordActivity")
                && !className.contains("InputCaptchaActivity")
                && !className.contains("InputMobileActivity")
                && !className.contains("SetPasswordActivity")
                && !className.contains("ProtocolActivity")
                && !className.contains("UserMergeActivity")
                ) {
            LogCat.e("TAG", "gotoLoginActivity= " + className);
            gotoLoginActivity(BaseApplication.getContext(), "1"); //1 剔除多端登录
        }
    }

    private static void gotoLoginActivity(Context context, String extra) {
        try {
            Class<?> loginActivity = Class.forName("com.sunmi.assistant.ui.activity.login.LoginActivity_");
            Intent intent = new Intent(context, loginActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);

            if (!TextUtils.isEmpty(extra))
                intent.putExtra("reason", extra);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoMainActivity(Context context) {
        try {
            Class<?> mainActivity = Class.forName("com.sunmi.assistant.ui.activity.MainActivity_");
            Intent intent = new Intent(context, mainActivity);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gotoSunmiLinkSearchActivity(Context context, String shopId, String sn) {
        try {
            Class<?> loginActivity = Class.forName("com.sunmi.assistant.ui.activity.SunmiLinkSearchActivity_");
            Intent intent = new Intent(context, loginActivity);
            if (!TextUtils.isEmpty(sn))
                intent.putExtra("sn", sn);
            if (!TextUtils.isEmpty(shopId))
                intent.putExtra("shopId", shopId);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
