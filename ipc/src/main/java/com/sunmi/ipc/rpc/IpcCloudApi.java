package com.sunmi.ipc.rpc;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunmi.ipc.model.CloudTimeSlotResp;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceCheckResp;
import com.sunmi.ipc.model.FaceGroupCreateReq;
import com.sunmi.ipc.model.FaceGroupCreateResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.model.FaceGroupUpdateReq;
import com.sunmi.ipc.model.FaceListResp;
import com.sunmi.ipc.model.FaceSaveResp;
import com.sunmi.ipc.model.IpcListResp;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.rpc.api.DeviceInterface;
import com.sunmi.ipc.rpc.api.EmqInterface;
import com.sunmi.ipc.rpc.api.FaceInterface;
import com.sunmi.ipc.rpc.api.MediaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import sunmi.common.constant.CommonConfig;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.mqtt.EmqTokenResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SafeUtils;

/**
 * Description: IpcCloudApi
 *
 * @author Bruce
 * @date 2019/3/31
 */
public class IpcCloudApi {

    /**
     * @param shopId    是	integer	店铺id
     * @param sn        是	string	设备序列号
     * @param bindMode  是	string	绑定模式， 0 代表ap mode, 1 代表sunmi-link mode
     * @param bindToken 是	string	bind认证token
     * @param longitude 是	float	经度
     * @param latitude  是	float	纬度
     */
    public static void bindIpc(String companyId, String shopId, String sn, int bindMode, String bindToken,
                               float longitude, float latitude, RetrofitCallback<Object> callback) {
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
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
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
    public static void unbindIpc(int companyId, int shopId,
                                 int deviceId, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
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
    public static void getDetailList(int companyId, int shopId, RetrofitCallback<IpcListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .getDetailList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新IPC名称
     *
     * @param companyId  是	int64	商户id
     * @param shopId     是	int64	店铺id
     * @param deviceId   是	int64	IPC设备id
     * @param deviceName 是	string	IPC设备名称
     */
    public static void updateBaseInfo(int companyId, int shopId, int deviceId, String deviceName,
                                      RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .put("device_name", deviceName)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .updateBaseInfo(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测IPC新固件
     *
     * @param companyId 是	int64	商户id
     * @param shopId    是	int64	店铺id
     * @param deviceId  是	int64	IPC设备id
     */
    public static void newFirmware(int companyId, int shopId, int deviceId,
                                   RetrofitCallback<IpcNewFirmwareResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .newFirmware(getSignedRequest(params))
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
    public static void createEmqToken(RetrofitCallback<EmqTokenResp> callback) {
        try {
            String params = new JSONObject()
                    .put("source", "APP")
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(EmqInterface.class)
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
                                    RetrofitCallback<CloudTimeSlotResp> callback) {
        try {
            String params = new JSONObject()
                    .put("device_id", deviceId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MediaInterface.class)
                    .getTimeSlots(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备录制视频列表
     *
     * @param deviceId  DeviceID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param callback  回调
     */
    public static void getVideoList(int deviceId, long startTime, long endTime,
                                    RetrofitCallback<VideoListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("device_id", deviceId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MediaInterface.class)
                    .getVideoList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * -------------------- 人脸库相关 --------------------
     */

    public static void getFaceAgeRange(int companyId, int shopId,
                                       RetrofitCallback<FaceAgeRangeResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getAgeRange(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Call<BaseResponse<FaceListResp>> getFaceList(
            int companyId, int shopId, int groupId, int gender, int age, String name, int page, int size,
            RetrofitCallback<FaceListResp> callback) {
        Call<BaseResponse<FaceListResp>> call = null;
        try {
            JSONObject params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId);
            if (gender >= 0) {
                params.put("gender", gender);
            }
            if (age >= 0) {
                params.put("age_range_code", age);
            }
            if (!TextUtils.isEmpty(name)) {
                params.put("name", name);
            }
            params.put("page_num", page).put("page_size", size);
            call = SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getList(getSignedRequest(params.toString()));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    public static void updateFaceDetail(int faceId, int groupId, String name, int gender, int age,
                                        RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("face_id", faceId)
                    .put("name", name)
                    .put("gender", gender)
                    .put("group_id", groupId)
                    .put("age_range_code", age)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .update(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFace(int companyId, int shopId, int groupId, List<Integer> faceIds,
                                  RetrofitCallback<Object> callback) {
        try {
            if (faceIds == null || faceIds.isEmpty()) {
                callback.onSuccess(1, "Request Empty.", null);
                return;
            }
            JSONArray array = new JSONArray(faceIds);
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId)
                    .put("face_id_list", array)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .delete(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void uploadFaceAndCheck(int companyId, int shopId, int groupId, File image,
                                          RetrofitCallback<FaceCheckResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId)
                    .toString();
            RequestBody file = RequestBody.create(MediaType.parse("image/*"), image);
            RequestBody body = new MultipartBody.Builder()
                    .addFormDataPart("file", image.getName(), file)
                    .addFormDataPart("company_id", String.valueOf(companyId))
                    .addFormDataPart("shop_id", String.valueOf(shopId))
                    .addFormDataPart("group_id", String.valueOf(groupId))
                    .build();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .uploadAndCheck(body)
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveFace(int companyId, int shopId, int groupId, List<String> faceImageList,
                                RetrofitCallback<FaceSaveResp> callback) {
        try {
            if (faceImageList == null || faceImageList.isEmpty()) {
                callback.onSuccess(1, "Request Empty.", null);
                return;
            }
            JSONArray array = new JSONArray(faceImageList);
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId)
                    .put("face_img_list", array)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .save(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void moveFace(int companyId, int shopId, int sourceGroup, int targetGroup, List<Integer> faceIds,
                                RetrofitCallback<Object> callback) {
        try {
            if (faceIds == null || faceIds.isEmpty()) {
                callback.onSuccess(1, "Request Empty.", null);
                return;
            }
            JSONArray array = new JSONArray(faceIds);
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("update_type", 1)
                    .put("source_group_id", sourceGroup)
                    .put("target_group_id", targetGroup)
                    .put("face_id_list", array)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .move(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void getFaceGroup(int companyId, int shopId, RetrofitCallback<FaceGroupListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getGroupList(getSignedRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void createFaceGroup(FaceGroupCreateReq request, RetrofitCallback<FaceGroupCreateResp> callback) {
        String params = new Gson().toJson(request);
        SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                .createGroup(getSignedRequest(params))
                .enqueue(callback);
    }

    public static void updateFaceGroup(FaceGroupUpdateReq request, RetrofitCallback<Object> callback) {
        String params = new Gson().toJson(request);
        SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                .updateGroup(getSignedRequest(params))
                .enqueue(callback);
    }

    public static void deleteFaceGroup(int companyId, int shopId, int groupId, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .deleteGroup(getSignedRequest(params))
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
