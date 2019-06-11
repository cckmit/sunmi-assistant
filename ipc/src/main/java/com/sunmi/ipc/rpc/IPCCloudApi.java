package com.sunmi.ipc.rpc;

import com.sunmi.ipc.rpc.api.DeviceInterface;
import com.sunmi.ipc.rpc.api.EmqInterface;
import com.sunmi.ipc.rpc.api.MediaInterface;
import com.sunmi.ipc.rpc.api.UserInterface;

import org.json.JSONException;
import org.json.JSONObject;

import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.http.BaseHttpApi;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description: IPCCloudApi
 * Created by Bruce on 2019/3/31.
 */
public class IPCCloudApi extends BaseHttpApi {

    public static void getStoreToken(RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("user_id", SpUtils.getUID())
                    .put("token", SpUtils.getToken())
                    .put("merchant_id", SpUtils.getCompanyId())
                    .put("app_type", 2)//1代表web, 2 代表app
                    .toString();
            RetrofitClient.getInstance().create(UserInterface.class)
                    .getStoreToken(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param shopId    是	integer	店铺id
     * @param sn        是	string	设备序列号
     * @param bindMode  是	string	绑定模式， 0 代表ap mode, 1 代表sunmi-link mode
     * @param bindToken 是	string	bind认证token
     * @param longitude 是	float	经度
     * @param latitude  是	float	纬度
     */
    public static void bindIPC(String companyId, String shopId, String sn, int bindMode, String bindToken,
                               float longitude, float latitude, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("sn", sn)
                    .put("bind_mode", bindMode)
                    .put("bind_token", bindToken)
                    .put("longitude", longitude)
                    .put("latitude", latitude)
                    .toString();
            RetrofitClient.getInstance().create(DeviceInterface.class)
                    .bind(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param companyId 是	integer	商户id
     * @param shopId    是	integer	店铺id
     * @param deviceId  是	integer	设备id
     */
    public static void unbindIPC(int companyId, int shopId,
                                 String deviceId, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .toString();
            RetrofitClient.getInstance().create(DeviceInterface.class)
                    .unbind(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param companyId 是	int64	商户id
     * @param shopId    是	int64	店铺id
     */
    public static void getDetailList(int companyId, String shopId, RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            RetrofitClient.getInstance().create(DeviceInterface.class)
                    .getDetailList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建APP用户登录EMQ的token
     * <p>
     * user_id 是	int	sso uId，登录后包含在jwt token中，无需显示传参
     * source  是	string	用户来源， APP或WEB
     */
    public static void createEmqToken(RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("source", "APP")
                    .toString();
            RetrofitClient.getInstance().create(EmqInterface.class)
                    .create(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定时间的视频时间轴信息
     *
     * @param deviceId  是	int	ipc设备id
     * @param startTime 是	int	开始时间戳
     * @param endTime   是	int	结束时间戳
     */
    public static void getTimeSlots(int deviceId, long startTime, long endTime,
                                    RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("device_id", deviceId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            RetrofitClient.getInstance().create(MediaInterface.class)
                    .getTimeSlots(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备录制视频列表
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @param callback
     */
    public static void getVideoList(int deviceId, long startTime, long endTime,
                                    RetrofitCallback callback) {
        try {
            String params = new JSONObject()
                    .put("device_id", deviceId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            RetrofitClient.getInstance().create(MediaInterface.class)
                    .getVideoList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 参数加签
     */
    private static BaseRequest getSignedRequest(String params) {
        String timeStamp = DateTimeUtils.currentTimeSecond() + "";
        String randomNum = (int) ((Math.random() * 9 + 1) * 100000) + "";
        String isEncrypted = "0";
        String sign = SafeUtils.md5(params + isEncrypted +
                timeStamp + randomNum + SafeUtils.md5(CommonConfig.CLOUD_TOKEN));
        return new BaseRequest.Builder()
                .setTimeStamp(timeStamp)
                .setRandomNum(randomNum)
                .setIsEncrypted(isEncrypted)
                .setParams(params)
                .setSign(sign)
                .setLang("zh").createBaseRequest();
    }

}
