package sunmi.common.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import sunmi.common.base.BaseApplication;

public class SpUtils {

    public static final String TOKEN_ROUTER = "TOKEN_ROUTER";
    private static final String HAS_LOGIN = "has_login";//Y-已登录，N-未登录,null-未登录
    private static final String USERNAME = "username";
    private static final String MOBILE = "MOBILE";
    private static final String EMAIL = "email";
    private static final String AVATAR_URL = "avatar_url";
    private static final String MERCHANT_UID = "MERCHANT_UID";
    private static final String UID = "UID";
    private static final String TOKEN = "TOKEN";
    private static final String LEAD_PAGES = "LEAD_PAGES";
    private static final String WEB_WIDTH = "WEB_WIDTH";
    private static final String WEB_HEIGHT = "WEB_HEIGHT";
    private static final String SET_ROUTER_MANGER_PASSWORD = "SET_ROUTER_MANGER_PASSWORD";//设置路由管理密码
    private static final String UDP_ROUTER = "UDP_ROUTER";    //快速配置路由的token
    private static final String BIND_TYPE_ERROR = "BIND_TYPE_ERROR";    //快速配置绑定路由 1 net异常  2 其他人绑定

    SpUtils() {
    }

    private static SharedPreferences getSharedPreference() {
        return SharedManager.getSharedPreference(BaseApplication.getContext());
    }

    //用户名称
    public static String getUsername() {
        return SharedManager.getValue(BaseApplication.getContext(), USERNAME);
    }

    public static void setUsername(String nickname) {
        SharedManager.putValue(BaseApplication.getContext(), USERNAME, nickname);
    }

    //用户Mobile
    public static String getMobile() {
        return SharedManager.getValue(BaseApplication.getContext(), MOBILE);
    }

    public static void setMobile(String mobile) {
        SharedManager.putValue(BaseApplication.getContext(), MOBILE, mobile);
    }

    //邮箱
    public static String getEmail() {
        return SharedManager.getValue(BaseApplication.getContext(), EMAIL);
    }

    public static void setEmail(String email) {
        SharedManager.putValue(BaseApplication.getContext(), EMAIL, email);
    }

    //用户头像
    public static String getAvatarUrl() {
        return SharedManager.getValue(BaseApplication.getContext(), AVATAR_URL);
    }

    public static void setAvatarUrl(String avatarUrl) {
        SharedManager.putValue(BaseApplication.getContext(), AVATAR_URL, avatarUrl);
    }

    //用户登录成功返回的商户id
    public static String getMerchantUid() {
        return SharedManager.getValue(BaseApplication.getContext(), MERCHANT_UID);
    }

    public static void setMerchantUid(String merchantUid) {
        SharedManager.putValue(BaseApplication.getContext(), SpUtils.MERCHANT_UID, merchantUid);
    }

    //用户uid
    public static String getUID() {
        return SharedManager.getValue(BaseApplication.getContext(), UID);
    }

    public static void setUID(String uid) {
        SharedManager.putValue(BaseApplication.getContext(), UID, uid);
    }

    //用户Token
    public static String getToken() {
        return SharedManager.getValue(BaseApplication.getContext(), TOKEN);
    }

    public static void setToken(String token) {
        SharedManager.putValue(BaseApplication.getContext(), TOKEN, token);
    }

    //登录状态
    public static void setLoginStatus(String hasLogin) {
        SharedManager.putValue(BaseApplication.getContext(), HAS_LOGIN, hasLogin);
    }

    public static String getLoginStatus() {
        return SharedManager.getValue(BaseApplication.getContext(), HAS_LOGIN);
    }

    /**
     * udp
     *
     * @param name
     */
    public static void saveUDPName(String name) {
        SharedManager.putValue(BaseApplication.getContext(), UDP_ROUTER, name);
    }

    public static void clearUDPName() {
        SharedManager.clearValue(BaseApplication.getContext(), UDP_ROUTER);
    }

    public static String getUDPName() {
        return SharedManager.getValue(BaseApplication.getContext(), UDP_ROUTER);
    }

    /**
     * 快速配置 设置管理密码
     *
     * @param password
     */
    public static void saveRouterMangerPassword(String password) {
        SharedManager.putValue(BaseApplication.getContext(), SET_ROUTER_MANGER_PASSWORD, password);
    }

    //获取管理密码
    public static String getRouterMangerPassword() {
        return SharedManager.getValue(BaseApplication.getContext(), SET_ROUTER_MANGER_PASSWORD);
    }

    //保存路由器Token
    public static void saveRouterToken(String token) {
        SharedManager.putValue(BaseApplication.getContext(), TOKEN_ROUTER, token);
    }

    //用户路由器Token
    public static String getTokenRouter() {
        return SharedManager.getValue(BaseApplication.getContext(), TOKEN_ROUTER);
    }

    public static void clearRouterMangerPassword() {
        SharedManager.clearValue(BaseApplication.getContext(), SET_ROUTER_MANGER_PASSWORD);
    }

    //保存手机像素高度
    public static void saveHeightPixel(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        //int widthPixel = outMetrics.widthPixels;
        int heightPixel = outMetrics.heightPixels;
        SharedManager.putValue(BaseApplication.getContext(), "heightPixel", heightPixel + "");
    }

    //获取手机像素高度
    public static int getHeightPixel() {
        return Integer.valueOf(SharedManager.getValue(BaseApplication.getContext(), "heightPixel"));
    }

    //保存是否显示引导页
    public static void saveLead() {
        SharedManager.putValue(BaseApplication.getContext(), LEAD_PAGES, "TRUE");
    }

    //获取引导页值
    public static String getLead() {
        return SharedManager.getValue(BaseApplication.getContext(), LEAD_PAGES);
    }

    //clear引导页值
    public static void clearLead() {
        SharedManager.clearValue(BaseApplication.getContext(), LEAD_PAGES);
    }

    public static void saveWebWidthHeight(int width, int height) {
        SharedManager.putValue(BaseApplication.getContext(), WEB_WIDTH, width + "");
        SharedManager.putValue(BaseApplication.getContext(), WEB_HEIGHT, height + "");
    }

    public static int getWebWidth() {
        return Integer.valueOf(SharedManager.getValue(BaseApplication.getContext(), WEB_WIDTH));
    }

    public static int getWebHeight() {
        return Integer.valueOf(SharedManager.getValue(BaseApplication.getContext(), WEB_HEIGHT));
    }

    //快速配置绑定失败类型
    public static void setConfigBindType(String type) {
        SharedManager.putValue(BaseApplication.getContext(), BIND_TYPE_ERROR, type);
    }

    public static String getConfigBindType() {
        return SharedManager.getValue(BaseApplication.getContext(), BIND_TYPE_ERROR);
    }

    public static void clearConfigBindType() {
        SharedManager.clearValue(BaseApplication.getContext(), BIND_TYPE_ERROR);
    }
}
