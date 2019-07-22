package com.sunmi.cloudprinter.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.PrinterDevice;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.rpc.IOTCloudApi;
import com.sunmi.cloudprinter.utils.Utility;

import java.util.UUID;

import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.utils.ByteUtils;

/**
 * Description: 1、将蓝牙搜索打印机的接口进行封装，包含打印机搜索与各种错误返回；startScan
 * 2、将打印机SN、软件商ID、商铺号ID绑定接口进行封装，包含绑定后各种结果返回；bindPrinter
 * 3、将蓝牙从打印机获取wifi接入点的接口进行封装，包含搜索到wifi列表和各种错误返回；getPrinterWifiList
 * 4、将蓝牙与打印机发送wifi账号ssid和pwd的接口进行封装，包含配对结果与各种错误返回；setPrinterWifi
 * 5、将打印机的在线状态查询接口进行封装，包含返回各种打印机在线离线、错误状态；getPrinterStatus
 * Created by bruce on 2019/7/19.
 */
public class SunmiPrinterClient implements BluetoothAdapter.LeScanCallback {

    private Context context;

    private BluetoothClient mClient;
    //蓝牙adapter
    private BluetoothAdapter btAdapter;
    //回调
    private IPrinterClient iPrinterClient;

    private String bleAddress;

    private byte[] receivedData;
    private int receivedLen;
    private int retryCount;

    public SunmiPrinterClient(Context context, String bleAddress, IPrinterClient iPrinterClient) {
        this.context = context;
        this.iPrinterClient = iPrinterClient;
        this.bleAddress = bleAddress;
        mClient = new BluetoothClient(context);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
    }

    /**
     * 打开系统蓝牙
     */
    public void enableBluetooth() {
        if (btAdapter != null && btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            btAdapter.enable();
        }
    }

    /**
     * 开始扫描打印机
     */
    public void startScan() {
        if (btAdapter != null) {
            btAdapter.startLeScan(this);
        }
    }

    /**
     * 停止扫描打印机
     * 注-退出界面的时候请调用此方法
     */
    public void stopScan() {
        if (btAdapter != null) {
            btAdapter.stopLeScan(this);
        }
    }

    /**
     * 获取打印机的sn，用于后续的绑定
     */
    public void getPrinterSn(String btAddress) {
        sendData(btAddress, Utility.cmdGetSn());
    }

    public void getPrinterWifiList(String btAddress) {
        sendData(btAddress, Utility.cmdGetWifi());
    }

    public void disconnect(String bleAddress) {
        stopScan();
        if (mClient != null) {
            mClient.disconnect(bleAddress);
            mClient = null;
        }
    }

    /**
     * 给打印机配置wifi
     *
     * @param ssid     wifi的ssid
     * @param password ssid对应的密码
     */
    public void setPrinterWifi(String bleAddress, byte[] ssid, String password) {
        byte[] pswByte;
        if (!TextUtils.isEmpty(password)) {
            pswByte = ByteUtils.String2Byte64(password);
        } else {
            pswByte = ByteUtils.getNoneByte64();
        }
        sendData(bleAddress, Utility.cmdConnectWifi(ssid, pswByte));
    }

    public void bindPrinter(int shopId, String sn) {
        IOTCloudApi.bindPrinter(shopId, sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (iPrinterClient != null) {
                    iPrinterClient.bindPrinterSuccess(code, msg, data);
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (iPrinterClient != null) {
                    iPrinterClient.bindPrinterFail(code, msg, data);
                }
            }
        });
    }

    public void getPrinterStatus() {

    }


    /**
     * 搜索到一台打印机设备
     *
     * @param device     搜到的蓝牙设备
     * @param rssi       信号强度
     * @param scanRecord 广播信息
     */
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device != null && scanRecord.length > 11) {
            if (!TextUtils.isEmpty(device.getName()) && (
                    device.getName().startsWith("cloudprint_")
                            || device.getName().startsWith("CloudPrint_"))) {
                PrinterDevice printerDevice = new PrinterDevice();
                printerDevice.setAddress(device.getAddress());
                printerDevice.setName(device.getName());
                if (iPrinterClient != null)
                    iPrinterClient.onPrinterFount(printerDevice);
            }
        }
        if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
            btAdapter.enable();
        }
    }

    public void sendData(final String bleAddress, final byte[] data) {
        connectBle(mClient, bleAddress, data);
    }

    private void connectBle(final BluetoothClient mClient, final String btAddress, final byte[] data) {
        if (mClient == null) return;
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();
        mClient.connect(btAddress, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                if (code == 0) {
                    initNotify(mClient, btAddress, data);
                }
            }
        });
    }

    private void initNotify(final BluetoothClient mClient, final String btAddress, final byte[] data) {
        if (mClient == null) return;
        mClient.notify(btAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {//收到设备发来的数据
                Log.e("BtBlePresenter", "555555 onNotify value = " + value.length);
                onDataReceived(value);
            }

            @Override
            public void onResponse(int code) {//注册notify返回的结果
                Log.e("BtBlePresenter", "initNotify onResponse, code = " + code);
                if (code == 0) {
                    retryCount = 0;
                    sendCmd(mClient, btAddress, data);
                } else {
                    if (retryCount > 3) {
                        retryCount = 0;
                        if (iPrinterClient != null) {
                            iPrinterClient.sendDataFail(0, context.getString(R.string.tip_config_fail));
                        }
                        return;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            retryCount++;
                            initNotify(mClient, btAddress, data);
                        }
                    }, 3000);
                }
            }
        });
    }

    private void sendCmd(final BluetoothClient mClient, final String btAddress, final byte[] data) {
        int dataLen = data.length;
        if (dataLen > 20) {
            int remainLen = dataLen;
            while (remainLen > 0) {
                writeData(mClient, btAddress, ByteUtils.subBytes(data,
                        dataLen - remainLen, remainLen > 20 ? 20 : remainLen));
                remainLen -= 20;
            }
        } else {
            writeData(mClient, btAddress, data);
        }
    }

    private void writeData(final BluetoothClient mClient, final String btAddress, final byte[] data) {
        if (mClient == null) return;
        mClient.write(btAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID, data,
                new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        Log.e("BtBlePresenter", "send Message:" + code);
                        if (code == 0) {//TODO 加个延时
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            }, 10_000);
//                            if (iPrinterClient != null) {
//                                iPrinterClient.sendDataFail(code, "发送失败");
//                            }
                        } else {
                            if (iPrinterClient != null) {
                                iPrinterClient.sendDataFail(code, context.getString(R.string.tip_send_fail));
                            }
                        }
                    }
                });
    }

    private void onDataReceived(byte[] value) {
        if (value.length > 0) {
            if (Utility.isFirstPac(value)) {
                receivedLen = 0;
                receivedData = new byte[Utility.getPacLength(value)];
                System.arraycopy(value, 0, receivedData, 0, value.length);
            } else {
                System.arraycopy(value, 0, receivedData, receivedLen, value.length);
            }
            receivedLen += value.length;
            if (receivedData.length == receivedLen) {
                int cmd = Utility.getCmd(receivedData);
                if (cmd == Constants.SRV2CLI_SEND_SN) {
                    if (iPrinterClient != null) {
                        iPrinterClient.onSnReceived(Utility.getSn(receivedData));
                    }
                } else if (cmd == Constants.SRV2CLI_SEND_WIFI_ERROR) {
                    if (iPrinterClient != null) {
                        iPrinterClient.onGetWifiListFail();
                    }
                } else if (cmd == Constants.SRV2CLI_SEND_WIFI_AP) {
                    if (iPrinterClient != null) {
                        iPrinterClient.routerFound(Utility.getRouter(receivedData));
                    }
                } else if (cmd == Constants.SRV2CLI_SEND_WIFI_AP_COMPLETELY) {
                    //TODO Wi-Fi信息接受完毕
                } else if (cmd == Constants.SRV2CLI_SEND_ALREADY_CONNECTED_WIFI) {
                    if (iPrinterClient != null) {
                        iPrinterClient.wifiConfigSuccess();
                    }
                    sendData(bleAddress, Utility.cmdAlreadyConnectedWifi());
                }
            }
        }
    }

    public interface IPrinterClient {

        void onPrinterFount(PrinterDevice printerDevice);

        void sendDataFail(int code, String msg);

//        void writeDataFail(int code);

        void onSnReceived(String sn);

        void onGetWifiListSuccess();

        void onGetWifiListFail();

        void onSetWifiSuccess();

        void wifiConfigSuccess();

        void bindPrinterSuccess(int code, String msg, String data);

        void bindPrinterFail(int code, String msg, String data);

        void routerFound(Router router);

    }

}
