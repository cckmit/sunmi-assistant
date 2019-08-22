package sunmi.common.rpc.cloud;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.model.PlatformInfo;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopInfoResp;
import sunmi.common.model.ShopListResp;
import sunmi.common.model.ShopRegionResp;
import sunmi.common.model.SsoTokenResp;
import sunmi.common.model.UserAvatarResp;
import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.SafeUtils;
import sunmi.common.utils.SpUtils;

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

    public void getAdList(int companyId, int shopId, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(AdInterface.class)
                    .getAdList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户是否存在
     *
     * @param username 是	string	邮箱或手机号
     */
    public static void isUserExist(String username, RetrofitCallback<Object> callback) {
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
    public void register(String username, String password,
                         String code, RetrofitCallback<Object> callback) {
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
     * 登录
     *
     * @param mobile   是	string	手机号
     * @param password 是	string	密码 des加密后
     */
    public void login(String mobile, String password, RetrofitCallback<Object> callback) {
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
    public void quickLogin(String mobile, String captcha, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("phone", mobile)
                    .put("code", captcha)
                    .put("app_type", 2)//来源 1-web 2-app
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .quickLogin(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //登出
    public void logout(RetrofitCallback<Object> callback) {
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .logout(new BaseRequest(""))
                .enqueue(callback);
    }

    public void getUserInfo(RetrofitCallback<UserInfoBean> callback) {
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
    public void resetPassword(String username, String password, String code,
                              RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("username", username)
                    .put("password", SafeUtils.EncryptDES_CBC(password))
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
    public void changePassword(String oldPsw, String newPsw, RetrofitCallback<Object> callback) {
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

    public void checkToken(RetrofitCallback<Object> callback) {
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .checkToken(new BaseRequest(""))
                .enqueue(callback);
    }

    // 修改用户昵称
    public void updateUsername(String username, RetrofitCallback<Object> callback) {
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

    /**
     * 修改用户头像
     *
     * @param avatar   头像文件
     * @param callback 回调
     */
    public void updateIcon(String name, File avatar, RetrofitCallback<UserAvatarResp> callback) {
        RequestBody file = RequestBody.create(MediaType.parse("image/*"), avatar);
        MultipartBody.Part part = MultipartBody.Part.createFormData("icon", name, file);
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .updateIcon(part)
                .enqueue(callback);
    }

    public void getSsoToken(RetrofitCallback<SsoTokenResp> callback) {
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .getSsoToken(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * 重置账号密码
     */
    public void sendRecoveryEmail(String email, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("email", email)
                    .put("country_code", 1)   //1-国内 2-国外，默认为1
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .sendRecoveryEmail(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改邮箱
     */
    public void updateEmail(String password, String email, int code, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("password", password)
                    .put("email", email)
                    .put("code", code)
                    .put("country_code", 1)   //1-国内 2-国外，默认为1
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .updateEmail(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改账号绑定手机号
     */
    public void updatePhone(String password, String phone, int code, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("password", password)
                    .put("phone", phone)
                    .put("code", code)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                    .updatePhone(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //****************************** 商户相关 ******************************

    public void getCompanyInfo(int companyId, RetrofitCallback<CompanyInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .getInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建商户
     */
    public void createCompany(String companyName, RetrofitCallback<CompanyInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_name", companyName)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .createCompany(new BaseRequest(params))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCompanyName(int companyId, String companyName,
                                  RetrofitCallback<CompanyInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("company_name", companyName)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .updateCompany(new BaseRequest(params))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新商户信息
     */
    public void updateCompanyInfo(CompanyInfoResp info,
                                  RetrofitCallback<CompanyInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("company_name", info.getCompany_name())
                    .put("contact_person", info.getContact_person())
                    .put("contact_tel", info.getContact_tel())
                    .put("contact_email", info.getContact_email())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .updateCompany(new BaseRequest(params))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取商户列表
     */
    public void getCompanyList(RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("page_num", 1)
                    .put("page_size", 999)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .getList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //****************************** 门店相关 ******************************

    public void getShopList(int companyId, RetrofitCallback<ShopListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("page_num", 1)
                    .put("page_size", 999)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getShopInfo(int shopId, RetrofitCallback<ShopInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建门店
     *
     * @param companyId 是
     * @param shopName  是
     * @param person    否
     * @param tel       否
     * @param callback  回调
     */
    public void createShop(int companyId, String shopName, String person, String tel,
                           RetrofitCallback<CreateShopInfo> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_name", shopName)
                    .put("contact_person", person)
                    .put("contact_tel", tel)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .createShop(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新门店信息
     */
    public void updateShopInfo(ShopInfo shopInfo, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", shopInfo.getShopId())
                    .put("shop_name", shopInfo.getShopName())
                    //营业状态 0:营业 1:停业
                    .put("business_status", 0)
                    .put("type_one", shopInfo.getTypeOne())
                    .put("type_two", shopInfo.getTypeTwo())
                    .put("province", shopInfo.getProvince())
                    .put("city", shopInfo.getCity())
                    .put("area", shopInfo.getArea())
                    .put("address", shopInfo.getAddress())
                    .put("contact_person", shopInfo.getContactPerson())
                    .put("contact_tel", shopInfo.getContactTel())
                    .put("business_area", shopInfo.getBusinessArea())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .editShop(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getShopCategory(RetrofitCallback<ShopCategoryResp> callback) {
        SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                .getShopCategory(new BaseRequest(""))
                .enqueue(callback);
    }

    public void getShopRegion(RetrofitCallback<ShopRegionResp> callback) {
        SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                .getShopRegion(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * saas信息
     */
    public void getSaasUserInfo(String phone, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("phone", phone)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getSaasUserInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 米商引擎所支持的Saas平台信息
     */
    public void getPlatformList(RetrofitCallback<PlatformInfo> callback) {
        try {
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getPlatformList(new BaseRequest(""))
                    .enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 米商引擎手机发送验证码
     */
    public void sendSaasVerifyCode(String phone, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("phone", phone)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .sendSaasVerifyCode(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 米商引擎手机校验验证码
     */
    public void confirmSaasVerifyCode(String phone, String code,
                                      RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("phone", phone)
                    .put("code", code)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .confirmSaasVerifyCode(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户授权获取Saas平台数据
     *
     * @param companyId
     * @param shopId
     * @param saasSource Saas来源标识id
     * @param shopNo     店铺在商米引擎的唯一标识
     * @param saasName   Saas软件商名称
     * @param callback
     */
    public void authorizeSaas(int companyId, int shopId, int saasSource, String shopNo,
                              String saasName, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("saas_source", saasSource)
                    .put("shop_no", shopNo)
                    .put("saas_name", saasName)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .authorizeSaas(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
