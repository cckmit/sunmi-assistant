package sunmi.common.constant;

/**
 * Description:
 * Created by bruce on 2019/2/13.
 */
public class CommonConstants {
    public static final String FILE_PROVIDER_AUTHORITY = "com.sunmi.assistant.fileprovider";

    public static long LONGITUDE;//精度
    public static long LATITUDE;//纬度

    //UDP发送IP
    public final static String SEND_IP = "255.255.255.255";
    //UDP发送端口号
    public final static int SEND_PORT = 10001;

    public static final int WHAT_UDP = 998; //发送udp报文
    public static final int WHAT_UDP_GET_SN = 9988; //发送udp报文获取sn

    //tab support
    public static final int tabSupport = 100000;
    //tab store
    public static final int tabDevice = 100001;
    public static final int tabDashBoard = 100002;

    public static int TYPE_AP = 0;
    public static int TYPE_IPC = 1;
    public static int TYPE_PRINTER = 2;

}
