package com.sunmi.ipc.rpc;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunmi.ipc.face.model.FaceArrivalCount;
import com.sunmi.ipc.face.model.FaceArrivalLogResp;
import com.sunmi.ipc.model.CashOrderResp;
import com.sunmi.ipc.model.CashVideoAbnormalEventResp;
import com.sunmi.ipc.model.CashVideoCountResp;
import com.sunmi.ipc.model.CashVideoListBean;
import com.sunmi.ipc.model.CashVideoResp;
import com.sunmi.ipc.model.CashVideoTimeSlotBean;
import com.sunmi.ipc.model.CloudTimeSlotResp;
import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceCheckResp;
import com.sunmi.ipc.model.FaceEntryHistoryResp;
import com.sunmi.ipc.model.FaceGroupCreateReq;
import com.sunmi.ipc.model.FaceGroupCreateResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.model.FaceGroupUpdateReq;
import com.sunmi.ipc.model.FaceListResp;
import com.sunmi.ipc.model.FaceSaveResp;
import com.sunmi.ipc.model.IpcNewFirmwareResp;
import com.sunmi.ipc.model.MotionVideoListResp;
import com.sunmi.ipc.model.MotionVideoTimeSlotsResp;
import com.sunmi.ipc.model.VideoListResp;
import com.sunmi.ipc.rpc.api.CashInterface;
import com.sunmi.ipc.rpc.api.DeviceInterface;
import com.sunmi.ipc.rpc.api.FaceInterface;
import com.sunmi.ipc.rpc.api.MediaInterface;
import com.sunmi.ipc.rpc.api.MotionDetectionInterface;
import com.xiaojinzi.component.anno.ServiceAnno;

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
import retrofit2.Call;
import sunmi.common.constant.CommonConfig;
import sunmi.common.model.ServiceListResp;
import sunmi.common.router.IpcCloudApiAnno;
import sunmi.common.router.model.IpcListResp;
import sunmi.common.rpc.cloud.SunmiStoreRetrofitClient;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;
import sunmi.common.rpc.retrofit.BaseRetrofitClient;
import sunmi.common.rpc.retrofit.RetrofitCallback;
import sunmi.common.utils.DateTimeUtils;
import sunmi.common.utils.SecurityUtils;
import sunmi.common.utils.SpUtils;

/**
 * Description: IpcCloudApi
 *
 * @author Bruce
 * @date 2019/3/31
 */
@ServiceAnno(value = {IpcCloudApiAnno.class}, singleTon = true)
public class IpcCloudApi implements IpcCloudApiAnno {

    public static IpcCloudApi getInstance() {
        return Single.INSTANCE;
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
     * @param shopId    是	integer	店铺id
     * @param sn        是	string	设备序列号
     * @param bindMode  是	string	绑定模式， 0 代表ap mode, 1 代表sunmi-link mode
     * @param bindToken 是	string	bind认证token
     * @param longitude 是	float	经度
     * @param latitude  是	float	纬度
     */
    public void bindIpc(String companyId, String shopId, String sn, int bindMode, String bindToken,
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
            Map<String, String> headers = new HashMap<>();
            if (!TextUtils.isEmpty(SpUtils.getStoreToken())) {
                headers.put("Authorization", "Bearer " + SpUtils.getStoreToken());
            }
            BaseRetrofitClient baseRetrofitClient = new BaseRetrofitClient();
            baseRetrofitClient.init(CommonConfig.SUNMI_STORE_URL, headers, 4);
            baseRetrofitClient.create(DeviceInterface.class)
                    .bind(new BaseRequest(params))
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
    public void unbindIpc(int companyId, int shopId,
                          int deviceId, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .unbind(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param companyId 是	int64	商户id
     * @param shopId    是	int64	店铺id
     */
    @Override
    public void getDetailList(int companyId, int shopId, RetrofitCallback<IpcListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .getDetailList(new BaseRequest(params))
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
    public void updateBaseInfo(int companyId, int shopId, int deviceId, String deviceName,
                               RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .put("device_name", deviceName)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .updateBaseInfo(new BaseRequest(params))
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
    public void newFirmware(int companyId, int shopId, int deviceId,
                            RetrofitCallback<IpcNewFirmwareResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("device_id", deviceId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .newFirmware(new BaseRequest(params))
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
    public void getTimeSlots(int deviceId, long startTime, long endTime,
                             RetrofitCallback<CloudTimeSlotResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_id", deviceId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MediaInterface.class)
                    .getTimeSlots(new BaseRequest(params))
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
    public void getVideoList(int deviceId, long startTime, long endTime,
                             RetrofitCallback<VideoListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_id", deviceId)
                    .put("start_time", startTime)
                    .put("end_time", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(MediaInterface.class)
                    .getVideoList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * -------------------- 人脸库相关 --------------------
     */
    public void getFaceAgeRange(int companyId, int shopId,
                                RetrofitCallback<FaceAgeRangeResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getAgeRange(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Call<BaseResponse<FaceListResp>> getFaceList(
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
                    .getList(new BaseRequest(params.toString()));
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return call;
    }

    /**
     * @param companyId
     * @param shopId
     * @param faceId
     * @param sourceGroupId 当前人脸分组id
     * @param callback
     */
    public void updateFaceName(int companyId, int shopId, int faceId, int sourceGroupId,
                               String name,
                               RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("face_id", faceId)
                    .put("source_group_id", sourceGroupId)
                    .put("name", name)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .update(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateFaceTargetGroupId(int companyId, int shopId, int faceId, int sourceGroupId,
                                        int targetGroupId,
                                        RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("face_id", faceId)
                    .put("source_group_id", sourceGroupId)
                    .put("target_group_id", targetGroupId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .update(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateFaceGender(int companyId, int shopId, int faceId, int sourceGroupId,
                                 int gender,
                                 RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("face_id", faceId)
                    .put("source_group_id", sourceGroupId)
                    .put("gender", gender)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .update(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateFaceAgeRangeCode(int companyId, int shopId, int faceId, int sourceGroupId,
                                       int ageRangeCode,
                                       RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("face_id", faceId)
                    .put("source_group_id", sourceGroupId)
                    .put("age_range_code", ageRangeCode)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .update(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteFace(int companyId, int shopId, int groupId, List<Integer> faceIds,
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
                    .delete(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void uploadFaceAndCheck(int companyId, int shopId, int groupId, File image,
                                   RetrofitCallback<FaceCheckResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId)
                    .toString();
            HashMap<String, String> paramsMap = getSignedMap(params);
            RequestBody file = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .addFormDataPart("file", image.getName(), file);
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .uploadAndCheck(builder.build())
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void saveFace(int companyId, int shopId, int groupId, int faceId, List<String> faceImageList,
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
                    .put("face_id", faceId)
                    .put("face_img_list", array)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .save(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void moveFace(int companyId, int shopId, int sourceGroup, int targetGroup, List<Integer> faceIds,
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
                    .move(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getFaceGroup(int companyId, int shopId, RetrofitCallback<FaceGroupListResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getGroupList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createFaceGroup(FaceGroupCreateReq request, RetrofitCallback<FaceGroupCreateResp> callback) {
        String params = new Gson().toJson(request);
        SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                .createGroup(new BaseRequest(params))
                .enqueue(callback);
    }

    public void updateFaceGroup(FaceGroupUpdateReq request, RetrofitCallback<Object> callback) {
        String params = new Gson().toJson(request);
        SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                .updateGroup(new BaseRequest(params))
                .enqueue(callback);
    }

    public void deleteFaceGroup(int companyId, int shopId, int groupId, RetrofitCallback<Object> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("group_id", groupId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .deleteGroup(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void arrivalListFaceGroup(int companyId, int shopId, int faceId, int pageNum, int pageSize,
                                     RetrofitCallback<FaceEntryHistoryResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId)
                    .put("face_id", faceId)
                    .put("page_num", pageNum)
                    .put("page_size", pageSize)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getArrivalHistory(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * companyId company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * start_time	否	string	起始时间 YYYY-MM-DD （不传为查询全部）
     * end_time	否	string	结束时间 YYYY-MM-DD （不传为查询全部） 起始时间必须同时传才生效
     * group_id	否	int64	人脸库id （不传为查询全部）
     * device_id	否	int64	设备id （不传为查询全部）
     * age_range	否	array[int64]	年龄范围 （不传为查询全部）
     * gender	否	int64	性别 （不传为查询全部）
     * page_num	否	int32	页码 （不传为查询全部不分页）
     * page_size	否	int32	每页条数 （不传为查询全部不分页）
     */
    public void getArrivalListByTimeRange(int companyId, int shopId, String startTime, String endTime, int groupId,
                                          int deviceId, List<Integer> ageRange, int gender, int pageNum,
                                          int pageSize, RetrofitCallback<FaceArrivalLogResp> callback) {
        try {
            JSONObject params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId);
            if (startTime != null) {
                params.put("start_time", startTime);
            }
            if (endTime != null) {
                params.put("end_time", endTime);
            }
            if (groupId != -1) {
                params.put("group_id", groupId);
            }
            if (deviceId != -1) {
                params.put("device_id", deviceId);
            }
            if (ageRange != null) {
                params.put("age_range", ageRange);
            }
            if (gender != 0) {
                params.put("gender", gender);
            }
            params.put("page_num", pageNum);
            params.put("page_size", pageSize);
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getArrivalListByTimeRange(new BaseRequest(params.toString()))
                    .enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * start_time	否	string	起始时间 YYYY-MM-DD （不传为查询全部）
     * end_time	否	string	结束时间 YYYY-MM-DD （不传为查询全部）
     * face_id	是	int64	人脸id
     */
    public void getArrivalCountByTimeRange(int companyId, int shopId, String startTime, String endTime, int faceId,
                                           RetrofitCallback<FaceArrivalCount> callback) {
        try {
            JSONObject params = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId);
            if (startTime != null) {
                params.put("start_time", startTime);
            }
            if (endTime != null) {
                params.put("end_time", endTime);
            }
            params.put("face_id", faceId);
            SunmiStoreRetrofitClient.getInstance().create(FaceInterface.class)
                    .getArrivalCountByTimeRange(new BaseRequest(params.toString()))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * device_id	否	int64	设备id 不传为查询所有设
     */
    @Override
    public void getStorageList(List<String> snList, RetrofitCallback<ServiceListResp> callback) {
        try {
            JSONArray array = new JSONArray(snList);
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_sn_list", array)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .getStorageList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * device_id	否	int64	设备id 不传为查询所有设
     */
    @Override
    public void getAuditVideoServiceList(List<String> snList, RetrofitCallback<ServiceListResp> callback) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            if (snList != null) {
                JSONArray array = new JSONArray(snList);
                jsonObject.put("device_sn_list", array);
            }
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .getAuditVideoServiceList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收银审计订单信息
     * company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * order_no	是	string	订单编号
     */
    public void getOrderInfo(String orderNo, RetrofitCallback<CashOrderResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("order_no", orderNo)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getOrderInfo(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取收银视频的日期信息，返回已有收银视频的日期时间戳列表
     * company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * device_id	否	int64	ipc设备id
     * time_range_start	是	int64
     * time_range_end	是	int64
     */
    public void getCashVidoTimeSlots(int deviceId, long startTime, long endTime, RetrofitCallback<CashVideoTimeSlotBean> callback) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            if (deviceId != -1) {
                jsonObject.put("device_id", deviceId);
            }
            jsonObject.put("time_range_start", startTime);
            jsonObject.put("time_range_end", endTime);
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getCashVidoTimeSlots(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定店铺下的收银视频统计信息
     * company_id	是	integer	商户id
     * shop_id	是	integer	店铺id
     * time_range_start	是
     * time_range_end	是
     *
     * @param startTime
     * @param endTime
     * @param callback
     */
    public void getShopCashVideoCount(long startTime, long endTime, RetrofitCallback<CashVideoListBean> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("time_range_start", startTime)
                    .put("time_range_end", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getShopCashVideoCount(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定店铺下指定设备的收银视频统计信息
     * ompany_id	是	integer	商户id
     * shop_id	是	integer	店铺id
     * device_id_list	是	array[integer]	ipc设备列表
     * time_range_start	是	integer	交易开始时间搜索范围起始日,单位为秒,闭区间(包含该时间)
     * time_range_end	是	integer	交易开始时间搜索范围结束日,单位为秒,开区间(不包含该时间)
     *
     * @param deviceId
     * @param startTime
     * @param endTime
     * @param callback
     */
    public void getIpcCashVideoCount(List<Integer> deviceId, long startTime, long endTime, RetrofitCallback<CashVideoCountResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("device_id_list", deviceId)
                    .put("time_range_start", startTime)
                    .put("time_range_end", endTime)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getIpcCashVideoCount(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * company_id	是	int64	商户id
     * shop_id	是	int64	店铺id
     * device_id	否	int64	ipc设备id
     * video_type	否	int32	标记类型 1:正常视频，2:异常视频，不传该参数为查询所有类型视频
     * time_range_start	是	int64	交易开始时间搜索范围起始日,单位为秒,闭区间(包含该时间)
     * time_range_end	是	int64	交易开始时间搜索范围结束日,单位为秒,开区间(不包含该时间)
     * page_size	否	int32	页码
     * page_num	否	int32	每页条数
     */
    public void getCashVideoList(int deviceId, int videoType, long startTime, long endTime, int pageNum, int pageSize,
                                 RetrofitCallback<CashVideoResp> callback) {

        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            if (deviceId != -1) {
                jsonObject.put("device_id", deviceId);
            }
            if (videoType != 0) {
                jsonObject.put("video_type", videoType);
            }
            jsonObject.put("time_range_start", startTime);
            jsonObject.put("time_range_end", endTime);
            jsonObject.put("page_num", pageNum);
            jsonObject.put("page_size", pageSize);
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getCashVideoList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定设备是收银防损开通状态
     *
     * @param snList
     * @param callback
     */
    @Override
    public void getAuditSecurityPolicyList(List<String> snList, RetrofitCallback<ServiceListResp> callback) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            if (snList != null) {
                JSONArray array = new JSONArray(snList);
                jsonObject.put("device_sn_list", array);
            }
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(DeviceInterface.class)
                    .getAuditSecurityPolicyList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取行为异常视频列表
     *
     * @param deviceId  否
     * @param startTime 是
     * @param endTime   是
     * @param pageNum   否
     * @param pageSize  否
     * @param callback
     */
    public void getAbnormalBehaviorVideoList(int deviceId, long startTime, long endTime, int pageNum, int pageSize,
                                             RetrofitCallback<CashVideoResp> callback) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            if (deviceId != -1) {
                jsonObject.put("device_id", deviceId);
            }

            jsonObject.put("time_range_start", startTime);
            jsonObject.put("time_range_end", endTime);
            jsonObject.put("page_num", pageNum);
            jsonObject.put("page_size", pageSize);
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getAbnormalBehaviorVideoList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getCashVideoAbnormalEvent(long eventId, RetrofitCallback<CashVideoAbnormalEventResp> callback) {
        try {
            String params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("event_id", eventId)
                    .toString();
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .getCashVideoAbnormalEvent(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收银审计视频--是否异常视频tag
     * <p>
     * company_id	    是	int64	商户id
     * shop_id	        是	int64	店铺id
     * audit_video_id	是	int64	收银视频id
     * description	    否	string	标记文本内容
     * video_type	    是	int32	标记类型 1:正常视频，2:异常视频
     * video_tag	    否	array[integer]	标记类型 1:自定义类型，2:飞单，3:钱箱未关，4:偷钱，5:漏扫，6:偷换条码，7:交易类型不匹配
     */
    public void updateTag(long videoId, int videoType, List<Integer> videoTags, String desc,
                          RetrofitCallback<Object> callback) {
        try {
            JSONObject params = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId())
                    .put("audit_video_id", videoId)
                    .put("video_type", videoType);
            boolean hasCustomTag = false;
            if (videoTags != null && !videoTags.isEmpty()) {
                JSONArray array = new JSONArray(videoTags);
                params.put("video_tag", array);
            }
            if (!TextUtils.isEmpty(desc)) {
                params.put("description", desc);
            }
            SunmiStoreRetrofitClient.getInstance().create(CashInterface.class)
                    .updateTag(new BaseRequest(params.toString()))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取门店下设备动态侦测/闲时看店事件视频时间信息（天）
     *
     * @param companyId 是	int	商户id
     * @param shopId    是	int	店铺id
     * @param deviceId  否	int	没有设备id，查询的是店铺下所有动态侦测视频。有设备id，查询特定设备的动态侦测视频
     * @param timeStart 是	int64	筛选开始时间，闭区间 ，包括这个时间
     * @param timeEnd   是	int64	筛选结束时间，开区间，不包括这个时间
     * @param callback  回调
     */
    public void getMotionTimeSlots(int companyId, int shopId, int deviceId, long timeStart, long timeEnd,
                                   RetrofitCallback<MotionVideoTimeSlotsResp> callback) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", companyId)
                    .put("shop_id", shopId);
            if (deviceId != -1) {
                jsonObject.put("device_id", deviceId);
            }
            jsonObject.put("time_range_start", timeStart);
            jsonObject.put("time_range_end", timeEnd);
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(MotionDetectionInterface.class)
                    .getTimeSlots(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备动态侦测/闲时看店事件视频列表
     *
     * @param companyId 是	int	商户id
     * @param shopId    是	int	店铺id
     * @param deviceId  否	int	设备id
     * @param source    否	int	事件来源
     * @param timeStart 是	int	搜索起始时间
     * @param timeEnd   是	int	搜索结束时间
     * @param pageNum   否	int	页码
     * @param pageSize  否	int	每页条数
     * @param callback  回调
     */
    public void getMotionVideoList(int companyId, int shopId, int deviceId, int source,
                                   long timeStart, long timeEnd, int pageNum, int pageSize,
                                   RetrofitCallback<MotionVideoListResp> callback) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("company_id", SpUtils.getCompanyId())
                    .put("shop_id", SpUtils.getShopId());
            if (deviceId != -1) {
                jsonObject.put("id", deviceId);
            }
            if (source != 0) {
                jsonObject.put("source", source);
            }
            jsonObject.put("time_range_start", timeStart);
            jsonObject.put("time_range_end", timeEnd);
            jsonObject.put("page_num", pageNum);
            jsonObject.put("page_size", pageSize);
            String params = jsonObject.toString();
            SunmiStoreRetrofitClient.getInstance().create(MotionDetectionInterface.class)
                    .getVideoList(new BaseRequest(params))
                    .enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static final class Single {
        private static final IpcCloudApi INSTANCE = new IpcCloudApi();
    }

}
