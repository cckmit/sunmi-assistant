package sunmi.common.rpc;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class RpcErrorCode {
    public static final int RPC_ERR_TIMEOUT = 990; //自定义的请求超时
    public static final int WHAT_ERROR = 997; //访问失败或无网络

    //云端http请求错误
    public static int HTTP_RESP_TOKEN_ERR = 401;//缺少或者非法token
    public static int HTTP_RESP_FORBID = 403;//用户没有操作权限
    public static int HTTP_RESP_TOKEN_EXPIRE = 499;//token过期

}
