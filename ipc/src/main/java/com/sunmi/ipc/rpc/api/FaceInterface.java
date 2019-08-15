package com.sunmi.ipc.rpc.api;

import com.sunmi.ipc.model.FaceAgeRangeResp;
import com.sunmi.ipc.model.FaceCheckResp;
import com.sunmi.ipc.model.FaceEntryHistoryResp;
import com.sunmi.ipc.model.FaceGroupCreateResp;
import com.sunmi.ipc.model.FaceGroupListResp;
import com.sunmi.ipc.model.FaceHistoryResp;
import com.sunmi.ipc.model.FaceListResp;
import com.sunmi.ipc.model.FaceSaveResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * @author yinhui
 * @date 2019-08-15
 */
public interface FaceInterface {

    String URL = "/ipc/api/face/";

    /**
     * 获取人脸年龄分布列表
     *
     * @param request company_id    是   integer 商户Id
     *                shop_id       是   integer 店铺Id
     * @return Response
     */
    @POST(URL + "age/getRangeList")
    Call<BaseResponse<FaceAgeRangeResp>> getAgeRange(@Body BaseRequest request);

    /**
     * 获取人脸列表
     *
     * @param request company_id	是	integer	商户Id
     *                shop_id	是	integer	店铺Id
     *                group_id	是	Integer	分组Id
     *                gender	否	Integer	性别，1：男，2：女
     *                age_range_code	否	Integer	年龄段 0：10~20， 1：20~30？
     *                name	否	String	姓名
     *                page_num	否	Integer	页码
     *                page_size	否	Integer	页面size，默认展示10条
     * @return Response
     */
    @POST(URL + "group/getFaceList")
    Call<BaseResponse<FaceListResp>> getList(@Body BaseRequest request);

    /**
     * 更新人脸信息
     *
     * @param request face_id	是	integer	人脸在数据库中对应的唯一Id
     *                name	是	string	人员姓名
     *                gender	是	integer	性别
     *                group_id	是	integer	人脸分组id
     *                age_range_code	是	integer	年龄段分组
     * @return Response
     */
    @POST(URL + "group/update")
    Call<BaseResponse<Object>> updateDetail(@Body BaseRequest request);

    /**
     * 人脸删除
     *
     * @param request company_id	是	integer	商户Id
     *                shop_id	是	integer	店铺Id
     *                group_id	是	integer	原始分组id，需要利用此信息验证人员列表的合法性
     *                face_id_list	否	array	待删除的人员名单
     * @return Response
     */
    @POST(URL + "delete")
    Call<BaseResponse<Object>> delete(@Body BaseRequest request);

    /**
     * 添加人脸
     *
     * @param request company_id	是	integer	商户id
     *                shop_id	是	integer	店铺id
     *                group_id	是	integer	人脸分组id
     *                file	是	file	人员照片
     * @return Response
     */
    @POST(URL + "group/uploadFace")
    Call<BaseResponse<FaceCheckResp>> uploadAndCheck(@Body BaseRequest request);

    /**
     * 保存人脸
     *
     * @param request company_id	是	integer	商户id
     *                shop_id	是	integer	店铺id
     *                group_id	是	integer	人脸分组id
     *                face_id	否	integer	人脸id
     *                file_img_list	是	string	上传图片名字列表
     * @return Response
     */
    @POST(URL + "group/saveFaceList")
    Call<BaseResponse<FaceSaveResp>> save(@Body BaseRequest request);

    /**
     * 人脸移库
     *
     * @param request company_id	是	integer	商户Id
     *                shop_id	是	integer	店铺Id
     *                update_type	是	integer	更新类型，1：批量更新，2：全量更新
     *                source_group_id	是	integer	原始分组id，整体移库时使用，并且在批量更新时校验人脸是否属于该分组
     *                target_group_id	是	integer	目标分组类型
     *                face_id_list	否	array	原需要移库的人脸id集合, 自定义移库时使用, 需要验证该list中的人脸是否输入原始分组
     * @return Response
     */
    @POST(URL + "group/move")
    Call<BaseResponse<Object>> move(@Body BaseRequest request);

    /**
     * 获取人脸库列表
     *
     * @param request company_id	是	integer	商户id
     *                shop_id	是	integer	店铺id
     * @return Response
     */
    @POST(URL + "group/getList")
    Call<BaseResponse<FaceGroupListResp>> getGroupList(@Body BaseRequest request);

    /**
     * 创建人脸库
     *
     * @param request company_id	是	integer	商户id
     *                shop_id	是	integer	店铺id
     *                name	是	string	用户自定义人脸库名称
     *                type	是	integer	人脸库类型
     *                threshold	是	integer	达到生客移库条件的生客出现次数
     *                period	是	integer	达到生客移库条件的计数周期，以秒为单位
     *                mark	是	string	用户备注人脸库信息
     *                capacity	是	integer	人脸库人脸图片容量
     *                target_id	是	integer	人脸转移的目标库id(生客转熟客)
     * @return Response
     */
    @POST(URL + "group/create")
    Call<BaseResponse<FaceGroupCreateResp>> createGroup(@Body BaseRequest request);

    /**
     * 更新人脸库信息
     *
     * @param request company_id	是	integer	商户id
     *                shop_id	是	integer	店铺id
     *                group_id	是	integer	人脸库id
     *                name	是	string	修改后的人脸库名称
     *                mark	是	string	修改后的备注信息
     *                capacity	是	integer	修改后的图片数量上限
     *                threshold	是	integer	达到生客移库条件的生客出现次数
     *                period	是	integer	达到生客移库条件的计数周期，以秒为单位
     *                alarm_notified	是	integer	警告通知开关， 0：关，1：开
     * @return Response
     */
    @POST(URL + "group/update")
    Call<BaseResponse<Object>> updateGroupDetail(@Body BaseRequest request);

    /**
     * 删除人脸库
     *
     * @param request company_id	是	integer	商户id
     *                shop_id	是	integer	店铺id
     *                group_id	是	integer	人脸分组id
     * @return Response
     */
    @POST(URL + "group/delete")
    Call<BaseResponse<Object>> deleteGroup(@Body BaseRequest request);

    /**
     * 获取人脸日志
     *
     * @param request company_id	是	int64	商户id
     *                shop_id	是	int64	店铺id
     *                group_id	否	int64	人脸分组id
     *                face_id	否	int64	人脸分组id
     *                age_range	否	int32	年龄范围
     *                gender	否	int32	性别
     *                name	否	string	姓名
     *                page_num	否	int32	页码
     *                page_size	否	int32	每页条数
     * @return Response
     */
    @POST(URL + "history/getList")
    Call<BaseResponse<FaceHistoryResp>> getHistory(@Body BaseRequest request);

    /**
     * 获取进店记录
     *
     * @param request company_id	是	int64	商户id
     *                shop_id	是	int64	店铺id
     *                face_id	是	int64	人脸id
     *                device_id	否	int64	设备id
     *                page_num	否	int32	页码
     *                page_size	否	int32	每页条数
     * @return Response
     */
    @POST(URL + "history/arrival/getList")
    Call<BaseResponse<FaceEntryHistoryResp>> getArrivalHistory(@Body BaseRequest request);

    /**
     * 删除进店记录
     *
     * @param request company_id	是	int64	商户id
     *                shop_id	是	int64	店铺id
     *                history_id_list	是	list	进店记录id列表
     * @return Response
     */
    @POST(URL + "history/arrival/delete")
    Call<BaseResponse<Object>> deleteArrivalHistory(@Body BaseRequest request);


}
