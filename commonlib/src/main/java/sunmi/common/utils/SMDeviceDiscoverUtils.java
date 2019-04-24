package sunmi.common.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import sunmi.common.constant.CommonConstants;
import sunmi.common.model.SunmiDevice;
import sunmi.common.notification.BaseNotification;

import static sunmi.common.utils.ByteUtils.bytesToHex;

/**
 * Description:通过udp获取sn号
 * Created by bruce on 2019/4/1.
 */
public class SMDeviceDiscoverUtils {

    //本机发送的data
    private static String data = "{\"reserve\":\"111\"}";
    private static byte[] dataByte = SafeUtils.encodeBase64ToByte(data);
    private static byte[] baseLength = ByteUtils.intToByte2(dataByte.length);
    //header：4字节
    private static final byte[] HEADER = {(byte) 0xFF, (byte) 0xFF, 0x33, (byte) 0xFF};
    //协议版本号：1字节
    private static final byte[] PROTOCOL_VERSION = {0x01};
    //消息类型（发送：01 接收02）：1字节
    private static final byte[] MESSAGE_TYPE = {0x01};
    //payload 长度：2字节Ø
    private static final byte[] LENGTH = baseLength;
    //payload 数据：xx字节
    private static final byte[] DATA = dataByte;
    //crc校验：4字节
    private static byte[] CRC_NUMBER = null;

    //发送消息类型
    private static byte[] send_type = {0x01};
    //接收消息数据类型
    private static byte[] receive_type = {0x02};

    //接收线程的循环标识
    private static boolean listenStatus = true;
    //接收报文信息
    private static byte[] receiveInfo;

    //针对部分手机无法接收udp问题
    private static WifiManager.MulticastLock lock;

    //初始化
    public static void scanDevice(Context context, int notify) {
        initStart(context);
        UdpRunnable task = new UdpRunnable(notify);
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
        pool.schedule(task, 0, TimeUnit.MILLISECONDS);
        pool.schedule(task, 500, TimeUnit.MILLISECONDS);
        pool.schedule(task, 1000, TimeUnit.MILLISECONDS);
    }

    //初始化信息
    private static void initStart(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("test_udp wifi");
        //本机发送的CRC校验数据
        byte[] ss = ByteUtils.byteMergerAll(HEADER, PROTOCOL_VERSION, MESSAGE_TYPE, LENGTH, DATA);
        CRC32 crc = new CRC32();
        crc.update(ss);
        //crc16进制
        CRC_NUMBER = ByteUtils.hexToBytes(String.valueOf(Long.toHexString(crc.getValue())));
    }

    //UDP数据发送线程
    public static class UdpRunnable implements Runnable {
        private int notify;

        UdpRunnable(int notify) {
            this.notify = notify;
        }

        @Override
        public void run() {
            try {
                //报文协议
                byte[] buf = ByteUtils.byteMergerAll(HEADER, PROTOCOL_VERSION, MESSAGE_TYPE, LENGTH, DATA, CRC_NUMBER);
                // 创建DatagramSocket对象，端口
                //方法一添加port
                //sendSocket = new DatagramSocket(AppConfig.SEND_PORT);
                //方法二添加port
                DatagramSocket sendSocket = new DatagramSocket(null);
                sendSocket.setReuseAddress(true);
                sendSocket.bind(new InetSocketAddress(CommonConstants.SEND_PORT));

                InetAddress serverAddr = InetAddress.getByName(CommonConstants.SEND_IP);
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length, serverAddr,
                        CommonConstants.SEND_PORT);
                lock.acquire();
                sendSocket.send(outPacket);

                //接收数据
                while (listenStatus) {
                    byte[] inBuf = new byte[1024];
                    DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
                    //lock.acquire();
                    sendSocket.receive(inPacket);

                    //返回的data
                    byte[] rd = inPacket.getData();

                    int len = inPacket.getLength();

                    //收发消息类型 发送01 接收02
                    byte[] message_type = new byte[1];
                    System.arraycopy(rd, 5, message_type, 0, 1);

                    //HEADER(4字节), PROTOCOL_VERSION(1字节), MESSAGE_TYPE(1字节), LENGTH(2字节), DATA(x字节),CRC_NUMBER(4字节)
                    //过滤掉本机发送的数据
                    if (Arrays.equals(message_type, receive_type)) {
                        //crc验证
                        byte[] crc_data = new byte[len - 4];//自己截取校验的返回crc数据
                        System.arraycopy(rd, 0, crc_data, 0, (len - 4));
                        CRC32 crc = new CRC32();
                        crc.update(crc_data);
                        String a = Long.toHexString(crc.getValue());
                        Log.e("TAG", "CRC32:---" + Long.toHexString(crc.getValue()));//十六进制

                        //server返回的crc末尾4个字节数据
                        byte[] crc_end4 = new byte[4];
                        System.arraycopy(rd, len - 4, crc_end4, 0, 4);
                        String b = bytesToHex(crc_end4);
                        Log.e("TAG", "server return**16-->>" + bytesToHex(crc_end4));
                        //crc验证
                        if (b.contains(a)) {//crc验证通过
                            byte[] data = new byte[len - 12];////解析数据data，截取除去data的其他12个字节
                            System.arraycopy(rd, 8, data, 0, (len - 12));
                            String jm = new String(Base64.decode(data, Base64.NO_WRAP));
                            SunmiDevice device = new GsonBuilder().create().fromJson(jm, SunmiDevice.class);
                            BaseNotification.newInstance().postNotificationName(notify, device);
                        } else {
                            closeSocket(sendSocket);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //停止接收线程，关闭套接字连接
    private static void closeSocket(DatagramSocket socket) {
        listenStatus = false; //关闭不在循环（如果不设置线程一直循环）
//        if (socket != null) {
//            socket.close();
//        }
    }
}
