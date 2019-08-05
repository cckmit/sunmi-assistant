package sunmi.common.rpc.cloud;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import sunmi.common.model.UserAvatarResp;
import sunmi.common.model.UserInfoBean;
import sunmi.common.rpc.mqtt.EmqTokenResp;
import sunmi.common.rpc.retrofit.BaseRequest;
import sunmi.common.rpc.retrofit.BaseResponse;

/**
 * Description:
 * Created by bruce on 2019/2/26.
 */
public interface UserInterface {
    String userPath = "api/user/";

    /**
     * 账号是否存在
     */
    @POST(userPath + "isUserExist")
    Call<BaseResponse<Object>> isUserExist(@Body BaseRequest request);

    /**
     * 用户注册
     */
    @POST(userPath + "register")
    Call<BaseResponse<Object>> register(@Body BaseRequest request);

    /**
     * 用户登录
     */
    @POST(userPath + "login")
    Call<BaseResponse<Object>> login(@Body BaseRequest request);

    /**
     * 验证码登录
     */
    @POST(userPath + "quickLogin")
    Call<BaseResponse<Object>> quickLogin(@Body BaseRequest request);

    /**
     * 退出登录
     */
    @POST(userPath + "logout")
    Call<BaseResponse<Object>> logout(@Body BaseRequest request);

    /**
     * 获取用户信息
     */
    @POST(userPath + "getUserInfo")
    Call<BaseResponse<UserInfoBean>> getUserInfo(@Body BaseRequest request);

    /**
     * 修改密码
     */
    @POST(userPath + "resetPassword")
    Call<BaseResponse<Object>> resetPassword(@Body BaseRequest request);

    /**
     * 修改密码
     */
    @POST(userPath + "changePassword")
    Call<BaseResponse<Object>> changePassword(@Body BaseRequest request);

    /**
     * 校验token
     */
    @POST(userPath + "checkToken")
    Call<BaseResponse<Object>> checkToken(@Body BaseRequest request);

    /**
     * 校验图片验证码
     */
    @POST(userPath + "checkImgCode")
    Call<BaseResponse<Object>> checkImgCode(@Body BaseRequest request);

    /**
     * 修改账号昵称
     */
    @POST(userPath + "updateUsername")
    Call<BaseResponse<Object>> updateUsername(@Body BaseRequest request);

    /**
     * 修改账号头像
     */
    @Multipart
    @POST(userPath + "updateIcon")
    Call<BaseResponse<UserAvatarResp>> updateIcon(@Part MultipartBody.Part file);

    /**
     * 获取token
     */
    @POST(userPath + "getStoreToken")
    Call<BaseResponse<Object>> getStoreToken(@Body BaseRequest request);

    /**
     * 创建emq token去连mqtt,以后用，暂时用emqInterface里的create
     */
    @POST(userPath + "createEmqToken")
    Call<BaseResponse<EmqTokenResp>> createEmqToken(@Body BaseRequest request);

}
