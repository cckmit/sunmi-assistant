package sunmi.common.rpc.cloud;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import sunmi.common.constant.CommonConfig;
import sunmi.common.model.AuthorizeInfoResp;
import sunmi.common.model.CompanyInfoResp;
import sunmi.common.model.CreateShopInfo;
import sunmi.common.model.CustomerAgeGenderResp;
import sunmi.common.model.CustomerAgeNewOldResp;
import sunmi.common.model.CustomerCountResp;
import sunmi.common.model.CustomerDataResp;
import sunmi.common.model.CustomerDistributionResp;
import sunmi.common.model.CustomerEnterRateTrendResp;
import sunmi.common.model.CustomerFrequencyAvgResp;
import sunmi.common.model.CustomerFrequencyDistributionResp;
import sunmi.common.model.CustomerFrequencyTrendResp;
import sunmi.common.model.CustomerHistoryDetailResp;
import sunmi.common.model.CustomerHistoryResp;
import sunmi.common.model.CustomerHistoryTrendResp;
import sunmi.common.model.CustomerRateResp;
import sunmi.common.model.CustomerShopDataResp;
import sunmi.common.model.CustomerShopDistributionResp;
import sunmi.common.model.HealthInfoBean;
import sunmi.common.model.NetEventListBean;
import sunmi.common.model.PlatformInfo;
import sunmi.common.model.SaleDataResp;
import sunmi.common.model.ServiceEnableResp;
import sunmi.common.model.ServiceListResp;
import sunmi.common.model.ShopCategoryResp;
import sunmi.common.model.ShopInfo;
import sunmi.common.model.ShopListResp;
import sunmi.common.model.ShopRegionResp;
import sunmi.common.model.SsoTokenResp;
import sunmi.common.model.TotalCustomerDataResp;
import sunmi.common.model.TotalRealTimeShopSalesResp;
import sunmi.common.model.TotalRealtimeCustomerTrendResp;
import sunmi.common.model.TotalRealtimeSalesTrendResp;
import sunmi.common.model.UserAvatarResp;
import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.mqtt.EmqTokenResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.CommonHelper;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;
import sunmi.common.utils.SecurityUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description:商米store接口
 * Created by bruce on 2019/7/30.
 */
public class SunmiStoreApi {

    public static final String TAG = "SunmiStoreApi";

    private SunmiStoreApi() {
    }

    public static SunmiStoreApi getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * 对参数进行加签
     *
     * @param params 参数
     * @return 加签后的Map
     */
    private static HashMap<String, String> getSignedMap(String params) {
        HashMap<String, String> map = new HashMap<>(6);
        String timeStamp = DateTimeUtils.currentTimeSecond() + "";
        String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String isEncrypted = "0";
        String sign = SecurityUtils.md5(params + isEncrypted +
                timeStamp + randomNum + SecurityUtils.md5(CommonConfig.CLOUD_TOKEN));
        map.put("timeStamp", timeStamp);
        map.put("randomNum", randomNum);
        map.put("isEncrypted", isEncrypted);
        map.put("params", params);
        map.put("sign", sign);
        map.put("lang", "zh");
        return map;
    }

    /**
     * 创建APP用户登录EMQ的token
     * <p>
     * user_id 是	int	sso uId，登录后包含在jwt token中，无需显示传参
     * source  是	string	用户来源， APP或WEB
     */
    public void createEmqToken(RetrofitCallback<EmqTokenResp> callback) {
        try {
            String params = new JSONObject()
                    .put("source", "APP")
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(EmqInterface.class)
                    .create(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void getServiceList(RetrofitCallback<ServiceListResp> callback) {
        SunmiStoreRetrofitClient.getInstance().create(AdInterface.class)
                .getServiceList(new BaseRequest(""))
                .enqueue(callback);
    }

    /**
     * 用户是否存在
     *
     * @param username 是	string	邮箱或手机号
     */
    public void isUserExist(String username, RetrofitCallback<Object> callback) {
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
    public void logout(Callback<BaseResponse<Object>> callback) {
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
    public void checkImgCode(String key, String code, RetrofitCallback callback) {
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
        HashMap<String, String> paramsMap = getSignedMap("");
        RequestBody file = RequestBody.create(MediaType.parse("image/*"), avatar);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .addFormDataPart("icon", "t", file);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        SunmiStoreRetrofitClient.getInstance().create(UserInterface.class)
                .updateIcon(builder.build())
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

    /**
     * 获取商户下所有门店列表
     *
     * @param companyId
     * @param callback
     */
    public void getTotalShopList(int companyId, RetrofitCallback<ShopListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("page_num", 1)
                    .put("page_size", 999)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CompanyInterface.class)
                    .getTotalShopList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
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

    public void getShopInfo(int shopId, RetrofitCallback<ShopInfo> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
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
    public void createShop(int companyId, String shopName, int province, int city, int area,
                           String address, int typeOne, int typeTwo,
                           float businessArea, String person, String tel, String lat, String lng,
                           RetrofitCallback<CreateShopInfo> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_name", shopName)
                    .put("province", province)
                    .put("city", city)
                    .put("area", area)
                    .put("address", address)
                    .put("type_one", typeOne)
                    .put("type_two", typeTwo)
                    .put("business_area", businessArea)
                    .put("contact_person", person)
                    .put("contact_tel", tel)
                    .put("lat", lat)
                    .put("lng", lng)
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
                    .put("lat", shopInfo.getLat())
                    .put("lng", shopInfo.getLng())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .editShop(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getShopCategory(RetrofitCallback<ShopCategoryResp> callback) {
        try {
            String params = new JSONObject()
                    .put("language", CommonHelper.getLanguage())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getShopCategory(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getShopRegion(RetrofitCallback<ShopRegionResp> callback) {
        SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                .getShopRegion(new BaseRequest(""))
                .enqueue(callback);
    }

    public void getServiceEnable(RetrofitCallback<ServiceEnableResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getServiceEnable(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
     * @param saasSource    Saas来源标识id
     * @param shopNo        店铺在商米引擎的唯一标识
     * @param saasName      Saas软件商名称
     * @param importPayment 是否导入历史订单数据， 默认值是 1 表是导入， 2 代表不导入
     * @param callback
     */
    public void authorizeSaas(int companyId, int shopId, int saasSource, String shopNo,
                              String saasName, int importPayment, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("saas_source", saasSource)
                    .put("shop_no", shopNo)
                    .put("saas_name", saasName)
                    .put("import_payment", importPayment)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .authorizeSaas(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店SaaS授权信息，包括授权状态，授权时间，数据导入状态
     */
    public void getAuthorizeInfo(int companyId, int shopId, RetrofitCallback<AuthorizeInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getAuthorizeInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取商户下所有门店SaaS授权信息，包括授权状态，授权时间，数据导入状态
     */
    public void getAuthorizeInfo(int companyId, RetrofitCallback<AuthorizeInfoResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .getCompanyAuthorizeInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 门店导入Saas历史数据
     *
     * @param companyId  是	number	商户id
     * @param shopId     是	number	店铺id
     * @param shopNo     是	string	对接店铺号
     * @param saasSource 是	number	saas source id
     */
    public void importSaas(int companyId, int shopId, String shopNo, int saasSource,
                           RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("shop_no", shopNo)
                    .put("saas_source", saasSource)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(ShopInterface.class)
                    .importSaas(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // -------------- 客流统计相关 --------------

    /**
     * 获取最近时间维度客流量（今日、本周、本月，昨日、上周、上月）
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param timeType  是	number	日: 1、周: 2、月: 3
     */
    public void getCustomer(int companyId, int shopId, int timeType,
                            RetrofitCallback<CustomerCountResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", timeType)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomer(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最近时间维度客流转换率，客流量，交易量（今日，本周，本月）
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param timeType  是	number	日: 1、周: 2、月: 3
     */
    public void getCustomerRate(int companyId, int shopId, int timeType,
                                RetrofitCallback<CustomerRateResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", timeType)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerRate(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店客群年龄性别分布
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param startTime 是	string	筛选开始时间 “YYYY-MM-DD”(“YYYY-MM-DD HH-MM-SS” 暂未启用）
     * @param endTime   是	string	筛选结束时间 “YYYY-MM-DD”(“YYYY-MM-DD HH-MM-SS” 暂未启用）
     */
    public void getCustomerByAgeGender(int companyId, int shopId, String startTime, String endTime,
                                       RetrofitCallback<CustomerAgeGenderResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerByAgeGender(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店客群年龄生熟客分布
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param startTime 是	string	筛选开始时间 “YYYY-MM-DD”(“YYYY-MM-DD HH-MM-SS” 暂未启用）
     * @param endTime   是	string	筛选结束时间 “YYYY-MM-DD”(“YYYY-MM-DD HH-MM-SS” 暂未启用）
     */
    public void getCustomerByAgeNewOld(int companyId, int shopId, String startTime, String endTime,
                                       RetrofitCallback<CustomerAgeNewOldResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerByAgeNewOld(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取历史客流数据
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param type      是	number	日: 1、周: 2、月: 3、昨日：4
     */
    @Deprecated
    public void getHistoryCustomer(int companyId, int shopId, int type,
                                   RetrofitCallback<CustomerHistoryResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getHistoryCustomer(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取历史客流数据
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param startTime 开始时间 “YYYY-MM-DD”
     * @param endTime   结束时间 “YYYY-MM-DD” （如果需要查询某一天，开始和结束时间相同）
     */
    public void getHistoryCustomer(int companyId, int shopId, String startTime, String endTime,
                                   RetrofitCallback<CustomerHistoryResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getHistoryCustomerByRange(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取历史客流变化趋势（今日、本周、本月，昨日）
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param type      是	number	日: 1、周: 2、月: 3、昨日：4
     * @param group     是	string	“hour”、”day”（”week”、”month”）
     */
    public void getHistoryCustomerTrend(int companyId, int shopId, int type, String group,
                                        RetrofitCallback<CustomerHistoryTrendResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .put("group_by", group)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getHistoryCustomerTrend(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取历史客流详情（今日、本周、本月，昨日）
     *
     * @param companyId 是	number	商户ID
     * @param shopId    是	number	门店ID
     * @param type      2:本周 3:本月 4:昨日
     */
    public void getHistoryCustomerDetail(int companyId, int shopId, int type,
                                         RetrofitCallback<CustomerHistoryDetailResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getHistoryCustomerDetail(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * T+1客流分析概览
     *
     * @param type     2:本周 3:本月 4:昨日
     * @param callback
     */
    public void getCustomerData(int companyId, int shopId, int type, RetrofitCallback<CustomerDataResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerData(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * T+1客流分析-进店率趋势
     *
     * @param type     2:本周 3:本月 4:昨日
     * @param groupBy  “hour”、”day”
     * @param callback
     */
    public void getCustomerEnterRateTrend(int companyId, int shopId, int type, String groupBy,
                                          RetrofitCallback<CustomerEnterRateTrendResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .put("group_by", groupBy)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerEnterRateTrend(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * T+1客流分析-客群到店频率分布
     *
     * @param type     2:本周 3:本月 4:昨日
     * @param callback
     */
    public void getCustomerFrequencyDistribution(int companyId, int shopId, int type,
                                                 RetrofitCallback<CustomerFrequencyDistributionResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerFrequencyDistribution(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * T+1客流分析-客群到店频率趋势
     *
     * @param type     2:本周 3:本月
     * @param groupBy  “week”、”day”
     * @param callback
     */
    public void getCustomerFrequencyTrend(int companyId, int shopId, int type, String groupBy,
                                          RetrofitCallback<CustomerFrequencyTrendResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .put("group_by", groupBy)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerFrequencyTrend(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * T+1客流分析-客群平均到店频率
     *
     * @param type     2:本周 3:本月
     * @param callback
     */
    public void getCustomerFrequencyAvg(int companyId, int shopId, int type,
                                        RetrofitCallback<CustomerFrequencyAvgResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerFrequencyAvg(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部实时客流概况
     *
     * @param companyId
     * @param callback
     */
    public void getTotalRealtimeCustomer(int companyId, RetrofitCallback<CustomerCountResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getTotalCustomerLatest(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部实时客流量趋势
     *
     * @param companyId
     * @param callback
     */
    public void getTotalRealtimeCustomerTrend(int companyId, RetrofitCallback<TotalRealtimeCustomerTrendResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getTotalCustomerRealTimeTrend(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部实时分店客流量列表
     *
     * @param companyId
     * @param callback
     */
    public void getTotalRealtimeCustomerByShop(int companyId, RetrofitCallback<CustomerShopDataResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getTotalCustomerShopData(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部实时经营概况
     *
     * @param companyId
     * @param callback
     */
    public void getTotalRealtimeSales(int companyId, RetrofitCallback<SaleDataResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaleInterFace.class)
                    .getTotalSaleData(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部实时销售额趋势
     *
     * @param companyId
     * @param callback
     */
    public void getTotalRealtimeSalesTrend(int companyId, RetrofitCallback<TotalRealtimeSalesTrendResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaleInterFace.class)
                    .getTotalSaleRealtimeTrend(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部实时分店客流量列表
     *
     * @param companyId
     * @param callback
     */
    public void getTotalRealtimeSalesByShop(int companyId, RetrofitCallback<TotalRealTimeShopSalesResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaleInterFace.class)
                    .getTotalSaleShopData(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部T+1销售概况
     *
     * @param companyId
     * @param startTime 时间 “YYYY-MM-DD”
     * @param type      1:日
     * @param callback
     */
    public void getTotalSales(int companyId, String startTime, int type,
                              RetrofitCallback<SaleDataResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("start_time", startTime)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(SaleInterFace.class)
                    .getTotalHistorySaleData(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部T+1客流概况
     *
     * @param companyId
     * @param startTime 时间 “YYYY-MM-DD”
     * @param type      1:日 2:周 3:月
     * @param callback
     */
    public void getTotalCustomer(int companyId, String startTime, int type,
                                 RetrofitCallback<TotalCustomerDataResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("start_time", startTime)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getTotalCustomerData(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取总部T+1客群年龄生熟客分布
     *
     * @param companyId
     * @param startTime 时间 “YYYY-MM-DD”
     * @param type      1:日 2:周 3:月
     * @param callback
     */
    public void getTotalCustomerNewOldDistribution(int companyId, String startTime, int type,
                                                   RetrofitCallback<CustomerDistributionResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("start_time", startTime)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerAgeDistribution(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部T+1客群年龄性别分布
     *
     * @param companyId
     * @param startTime 时间 “YYYY-MM-DD”
     * @param type      1:日 2:周 3:月
     * @param callback
     */
    public void getTotalCustomerGenderDistribution(int companyId, String startTime, int type,
                                                   RetrofitCallback<CustomerDistributionResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("start_time", startTime)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerAgeGenderDistribution(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部T+1客群-年龄生熟客分布-分店列表
     *
     * @param companyId
     * @param startTime 时间 “YYYY-MM-DD”
     * @param type      1:日 2:周 3:月
     * @param callback
     */
    public void getTotalCustomerNewOldDistributionByShop(int companyId, String startTime, int type,
                                                         RetrofitCallback<CustomerShopDistributionResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("start_time", startTime)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerShopAgeDistribution(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取总部T+1客群年龄性别分布-分店列表
     *
     * @param companyId
     * @param startTime 时间 “YYYY-MM-DD”
     * @param type      1:日 2:周 3:月
     * @param callback
     */
    public void getTotalCustomerGenderDistributionByShop(int companyId, String startTime, int type,
                                                         RetrofitCallback<CustomerShopDistributionResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("start_time", startTime)
                    .put("type", type)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CustomerInterface.class)
                    .getCustomerShopAgeGenderDistribution(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * company_id	是	number	商户id
     * shop_id	是	number	店铺id
     * sn	是	string	路由器的sn号
     * page_num	否	int	默认是1
     * page_size	否	int	默认是10
     */
    public void getEventList(String sn, int pageNum, int pageSize, RetrofitCallback<NetEventListBean> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("sn", sn)
                    .put("page_num", pageNum)
                    .put("page_size", pageSize)
                    .put("time_range_start", DateTimeUtils.startTime0())
                    .put("time_range_end", DateTimeUtils.endTime24())
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(NetworkInterface.class)
                    .getEventList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * company_id	是	number	商户id
     * shop_id	是	number	店铺id
     * sn	是	string	路由器的sn号
     * domain_id_list	否	array[int]	唯一saas的域名id
     */
    public void getHealthInfo(String sn, List<Integer> list,
                              RetrofitCallback<HealthInfoBean> callback) {
        try {

            JSONArray array = new JSONArray(list);
            JSONObject object = new JSONObject();
            object.put("company_id", SpUtils.getCompanyId());
            object.put("shop_id", SpUtils.getShopId());
            object.put("sn", sn);
            if (list != null) {
                object.put("domain_id_list", array);
            }
            SunmiStoreRetrofitClient.getInstance().create(NetworkInterface.class)
                    .getHealthInfo(new BaseRequest(object.toString()))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * company_id	是	number	商户id
     * shop_id	是	number	店铺id
     * sn	是	string	路由器的sn号
     * domain_id_list	是	array[int]	mqtt中返回的唯一saas的域名id
     * time_range_start	是	int	开始时间 (包括这个时间)
     * time_range_end	是	int	结束时间 （不包括这个时间）
     */
    public void getNetHealthList(String sn, List<Integer> list,
                                 String start, String end,
                                 RetrofitCallback<Object> callback) {
        try {
            JSONArray array = new JSONArray(list);
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("sn", sn)
                    .put("domain_id_list", array)
                    .put("time_range_start", start)
                    .put("time_range_end", end)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(NetworkInterface.class)
                    .getNetHealthList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static final class Singleton {
        private static final SunmiStoreApi INSTANCE = new SunmiStoreApi();
    }

}
