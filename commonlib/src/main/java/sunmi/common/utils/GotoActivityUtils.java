package sunmi.common.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaojinzi.component.impl.Router;

import sunmi.common.base.BaseApplication;
import sunmi.common.constant.RouterConfig;
import sunmi.common.router.AppApi;
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
                && !className.contains("RetrievePasswordActivity")
                && !className.contains("InputCaptchaActivity")
                && !className.contains("InputMobileActivity")
                && !className.contains("SetPasswordActivity")
                && !className.contains("ProtocolActivity")
                && !className.contains("UserMergeActivity")
                && !className.contains("LoginChooseShopActivity")
                && !className.contains("PlatformMobileActivity")
                && !className.contains("SelectPlatformActivity")
                && !className.contains("SelectStoreActivity")
                && !className.contains("CreateCompanyActivity")
                && !className.contains("CreateCompanyNextActivity")
                && !className.contains("CreateShopActivity")
                && !className.contains("CreateShopNewActivity")
                && !className.contains("CreateShopPreviewActivity")
        ) {
            LogCat.e("TAG", "gotoLoginActivity= " + className);
            Router.withApi(AppApi.class).goToLogin(BaseApplication.getContext(), "1"); //1 剔除多端登录
        }
    }

}
