package sunmi.common.rpc;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class RpcErrorCode {
    public static final int RPC_COMMON_ERROR = 990; //访问失败或无网络
    public static final int RPC_ERR_TIMEOUT = 991; //自定义的请求超时

    public static final int OPCODE_RESPONSE_NULL = 980; //opcode请求返回为空

    //云端http请求 网关错误
    public static int HTTP_RESP_UNKNOWN_REQUEST = 400;//服务器不理解请求的语法
    public static int HTTP_RESP_TOKEN_ERR = 401;//缺少或者非法token
    public static int HTTP_RESP_FORBID = 403;//用户没有操作权限
    public static int HTTP_RESP_TOKEN_EXPIRE = 499;//token过期

    //云端http请求 业务错误
    public static int HTTP_INVALID_TOKEN = 5027;//invalid store token
    public static int HTTP_EXPIRE_TOKEN = 5028;//store token expire
    public static int HTTP_JWT_TOKEN_EXPIRED = 5029;//jwt token expire


    //服务相关错误码
    public static final int ERR_SERVICE_SUBSCRIBE_ERROR = 5420;

    public static final int ERROR_USER_NOT_EXIST = 3603;

    /**
     * 收银防损查询AI事件不存在
     */
    public static final int ERR_CASH_EVENT_NOT_EXIST = 5600;

}
