package sunmi.common.rpc.cloud;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SafeUtils;

/**
 * Description:商米store接口
 * Created by bruce on 2019/7/30.
 */
public class SunmiStoreApi {

    public static final String TAG = "SunmiStoreApi";

    private static final class Singleton {
        private static final SunmiStoreApi INSTANCE = new SunmiStoreApi();
    }

    public static SunmiStoreApi getInstance() {
        return Singleton.INSTANCE;
    }

    private SunmiStoreApi() {
    }

    /**
     * 用户是否存在
     *
     * @param username 是	string	邮箱或手机号
     */
    public static void isUserExist(String username, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", username)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .isUserExist(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户注册
     *
     * @param username 是	string	邮箱或手机号
     * @param password 是	string	DES(cbc)加密后密码
     * @param code     否	number	手机或邮箱验证码
     */
    public static void register(String username, String password,
                                String code, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", username)
                    .put("password", SafeUtils.EncryptDES_CBC(password))
                    .put("code", code)
                    .put("app_type", 2)//来源 1-web 2-app
                    .put("auto_login", 1)//自动登录 0-no 1-yes
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .register(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送验证码
     *
     * @param username 是	string	手机号
     * @param type     是	string	1-注册时发送验证码 2-找回密码、修改邮箱和手机号发送验证码
     */
    public static void sendVerifyCode(String username, int type, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", username)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .sendVerifyCode(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录
     *
     * @param mobile   是	string	手机号
     * @param password 是	string	密码 des加密后
     */
    public static void login(String mobile, String password, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", mobile)
                    .put("password", SafeUtils.EncryptDES_CBC(password))
                    .put("app_type", 2)//来源 1-web 2-app
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .login(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机验证码登录
     *
     * @param mobile  是	string	手机号
     * @param captcha 是	string	密码 des加密后
     */
    public static void quickLogin(String mobile, String captcha, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("phone", mobile)
                    .put("code", captcha)
                    .put("app_type", 2)//来源 1-web 2-app
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .login(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //登出
    public static void logout(RetrofitCallback callback) {
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .logout(new BaseRequest(""))
                .enqueue(callback);
    }

    public static void getUserInfo(RetrofitCallback callback) {
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .getUserInfo(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * 校验图片验证码
     *
     * @param key  是	string	获取图片验证码是返回的key
     * @param code 否	string	图片验证码
     */
    public static void checkImgCode(String key, String code, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("key", key)
                    .put("code", code)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .checkImgCode(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置密码
     *
     * @param username 是	string	手机号
     * @param password 是	string	重置的密码
     * @param code     否   string	手机短信验证码
     */
    public static void resetPassword(String username, String password, String code, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", username)
                    .put("password", password)
                    .put("code", code)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .resetPassword(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 修改密码
    public static void changePassword(String oldPsw, String newPsw, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("old_password", SafeUtils.EncryptDES_CBC(oldPsw))
                    .put("new_password", SafeUtils.EncryptDES_CBC(newPsw))
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .changePassword(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void checkToken(RetrofitCallback callback) {
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .checkToken(new BaseRequest(""))
                .enqueue(callback);
    }

    // 修改用户昵称
    public static void updateUsername(String username, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", username)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .updateUsername(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 修改用户昵称 todo icon是文件
    public static void updateIcon(String icon, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("username", icon)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .updateIcon(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getStoreToken(String userId, String token, String companyId,
                                     RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("user_id", userId)
                    .put("token", token)
                    .put("merchant_id", companyId)
                    .put("app_type", 2)//1代表web, 2 代表app
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .getStoreToken(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
