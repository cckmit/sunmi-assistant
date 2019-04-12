package sunmi.common.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import sunmi.common.base.BaseApplication;

/**
 * Created by YangShiJie on 2019/4/10.
 * 多端登录剔除，针对部分手机无法后台默认自启动如：Oppo等
 */
public class GotoLoginUtils {

    public static void gotoLoginActivity(String className) {
        if (TextUtils.isEmpty(SpUtils.getLoginStatus())
                && !className.contains("LoginActivity")
                && !className.contains("LeadPagesActivity")
                && !className.contains("WelcomeActivity")
                && !className.contains("RegisterActivity")
                && !className.contains("SendSmsLoginActivity")
                && !className.contains("RetrievePasswordActivity")
                && !className.contains("InputCaptchaActivity")
                && !className.contains("InputCaptchaActivity")
                && !className.contains("InputMobileActivity")
                && !className.contains("ProtocolActivity")
                ) {
            gotoLoginActivity(BaseApplication.getContext(), "1"); //1 剔除多端登录
        }
    }

    private static void gotoLoginActivity(Context context, String extra) {
        try {
            Class<?> loginActivity = Class.forName("com.sunmi.assistant.ui.activity.login.LoginActivity_");
            Intent intent = new Intent(context, loginActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (!TextUtils.isEmpty(extra))
                intent.putExtra("reason", extra);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
