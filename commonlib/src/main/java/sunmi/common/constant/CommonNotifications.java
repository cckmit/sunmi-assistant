package sunmi.common.constant;

public class CommonNotifications {
    //common notice
    public static int totalEvents = 0x0001;

    public static final int mqttResponseTimeout = totalEvents++;  //网络异常
    public static final int netDisconnection = totalEvents++;     //网络断开
    public static final int netConnected = totalEvents++;         //网络连接
    public static final int ipcUpgrade = totalEvents++;           //ipc升级
    public static final int ipcUpgradeComplete = totalEvents++;   //ipc升级完成
    public static final int importShop = totalEvents++;           //刷新tab

    public static final int shopNameChanged = totalEvents++;      //修改店铺名称
    public static final int shopSwitched = totalEvents++;         //店铺切换
    public static final int shopCreate = totalEvents++;           //新建门店
    public static final int shopSaasDock = totalEvents++;         //门店接入SAAS成功
    public static final int companyNameChanged = totalEvents++;   //修改商户名称
    public static final int companySwitch = totalEvents++;        //商户切换

    public static final int homePageBadgeUpdate = totalEvents++;  //消息中心有消息更新
    public static final int msgCenterBadgeUpdate = totalEvents++; //消息已读或接收状态改变
    public static final int msgSettingsChange = totalEvents++;    //设备消息接收状态改变
    public static final int pushMsgArrived = totalEvents++;       //推送消息

    public static final int ipcDeviceStatus = totalEvents++;       //ipc设备状态

    public static final int cloudStorageChange = totalEvents++;    //云存储订阅状态修改
    public static final int ipcUpgradeSuccessUdp = totalEvents++;  //升级成功发送udp
    public static final int activeCloudChange = totalEvents++;     //云存储绑定服务的状态修改
    public static final int cashVideoPlayPosition = totalEvents++;     //收银播放位置
    public static final int cashVideoSubscribe = totalEvents++; //收银视屏服务订阅成功

}
