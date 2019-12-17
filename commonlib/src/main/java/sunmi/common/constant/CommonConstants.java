package sunmi.common.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import sunmi.common.base.BaseApplication;
import sunmi.common.model.SunmiDevice;
import sunmi.common.utils.CommonHelper;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public class CommonConstants {
    public static final String FILE_PROVIDER_AUTHORITY =
            CommonHelper.getAppPackageName(BaseApplication.getContext()) + ".fileprovider";
    public static Map<String, SunmiDevice> SUNMI_DEVICE_MAP = new ConcurrentHashMap<>();

    public static long LONGITUDE;//精度
    public static long LATITUDE;//纬度

    //UDP发送IP
    public final static String SEND_IP = "255.255.255.255";
    //UDP发送端口号
    public final static int SEND_PORT = 10001;
    public static final int WHAT_ERROR = 997; //访问失败或无网络
    public static final int WHAT_UDP_GET_SN = 9988; //发送udp报文获取sn

    //tab support
    public static final int tabSupport = 100000;
    //tab store
    public static final int tabDevice = 100001;

    public static int TYPE_AP = 0;
    public static int TYPE_IPC_SS = 1;
    public static int TYPE_IPC_FS = 2;
    public static int TYPE_PRINTER = 3;

    //选择商户和门店
    public static int ACTION_LOGIN_CHOOSE_COMPANY = 0;
    public static int ACTION_LOGIN_CHOOSE_SHOP = 1;
    public static int ACTION_CHANGE_COMPANY = 2;

    public static final String GOOGLE_PLAY = "google";

    //服务订阅状态
    public static final int SERVICE_ALREADY_OPENED = 1;
    public static final int SERVICE_NOT_OPENED = 2;
    public static final int SERVICE_EXPIRED = 3;

    //云存储续费状态
    public static final int CLOUD_STORAGE_RENEWABLE = 1;
    public static final int CLOUD_STORAGE_NOT_RENEWABLE = 2;

    //储捆绑服务状态
    public static final int SERVICE_INACTIVATED = 1;
    public static final int SERVICE_ACTIVATED = 2;

    public static final int CONFIG_IPC_FROM_COMMON = 1;
    public static final int CONFIG_IPC_FROM_CASH_VIDEO = 2;

    public static final int IMPORT_ORDER_FROM_COMMON = 1;
    public static final int IMPORT_ORDER_FROM_CASH_VIDEO = 2;

    //H5后序地址
    public static final String H5_CLOUD_STORAGE = "cloudStorage?topPadding=";
    public static final String H5_CASH_VIDEO = "cashvideo/welcome?topPadding=";
    public static final String H5_ORDER_MANAGE = "orderManagement/orderList?topPadding=";

}
