package com.sunmi.cloudprinter.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.sunmi.cloudprinter.R;
import com.sunmi.cloudprinter.bean.PrinterDevice;
import com.sunmi.cloudprinter.bean.Router;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.rpc.IOTCloudApi;
import com.sunmi.cloudprinter.utils.Utility;

import java.util.UUID;

import library.BluetoothClient;
import library.connect.options.BleConnectOptions;
import library.connect.response.BleConnectResponse;
import library.connect.response.BleNotifyResponse;
import library.connect.response.BleUnnotifyResponse;
import library.connect.response.BleWriteResponse;
import library.model.BleGattProfile;
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


    private byte[] receivedData;
    private int receivedLen;
    private int retryCount;

    public SunmiPrinterClient(Context context, IPrinterClient iPrinterClient) {
        this.context = context;
        this.iPrinterClient = iPrinterClient;
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
     *
     * @param btAddress 蓝牙地址
     */
    public void getPrinterSn(String btAddress) {
        sendData(btAddress, Constants.CMD_REQ_SN, Utility.cmdGetSn());
    }

    /**
     * 获取打印机wifi列表
     *
     * @param btAddress 蓝牙地址
     */
    public void getPrinterWifiList(String btAddress) {
        sendData(btAddress, Constants.CMD_REQ_WIFI_LIST, Utility.cmdGetWifi());
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
        sendData(bleAddress, Constants.CMD_REQ_CONNECT_WIFI, Utility.cmdConnectWifi(ssid, pswByte));
    }

    public void deleteWifiInfo(String btAddress) {
        sendData(btAddress, Constants.CMD_REQ_DELETE_WIFI_INFO, Utility.cmdDeleteWifiInfo());
    }

    public void quitConfig(String btAddress) {
        sendData(btAddress, Constants.CMD_REQ_QUIT_CONFIG, Utility.cmdQuitConfig());
    }

    public void disconnect(String bleAddress) {
        stopScan();
        if (mClient != null) {
            mClient.disconnect(bleAddress);
            mClient = null;
        }
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
            if (isCloudPrinter(device.getName())) {
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

    private boolean isCloudPrinter(String name) {
        return !TextUtils.isEmpty(name) &&
                (name.startsWith("cloudprint_") || name.startsWith("CloudPrint_"));
    }

    private void sendData(final String bleAddress, final int cmd, byte[] data) {
        connectBle(mClient, bleAddress, cmd, data);
    }

    private void connectBle(final BluetoothClient mClient, final String btAddress,
                            final int cmd, final byte[] data) {
        if (mClient == null) return;
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(15_000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(15_000)
                .build();
        mClient.connect(btAddress, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                if (code == 0) {
                    Log.e("BtBlePresenter", "555555 connect success ");
                    initNotify(mClient, btAddress, cmd, data);
                } else {
                    Log.e("BtBlePresenter", "555555 connect code = " + code);
                    iPrinterClient.sendDataFail(0, context.getString(R.string.tip_config_fail));
                }
            }
        });
    }

    private void initNotify(final BluetoothClient mClient, final String btAddress,
                            final int cmd, final byte[] data) {
        if (mClient == null) return;
        BleNotifyResponse notifyResponse = new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {//收到设备发来的数据
                onDataReceived(value, btAddress);
            }

            @Override
            public void onResponse(int code) {//注册notify返回的结果
                if (code == 0) {
                    retryCount = 0;
                    Log.e("BtBlePresenter", "555555 initNotify success ");
                    sendCmd(mClient, btAddress, cmd, data);
                } else {
                    Log.e("BtBlePresenter", "initNotify onResponse, code = " + code);
                    if (retryCount > 3) {
                        retryCount = 0;
                        if (iPrinterClient != null) {
                            iPrinterClient.sendDataFail(1, context.getString(R.string.tip_config_fail));
                        }
                        return;
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            retryCount++;
                            initNotify(mClient, btAddress, cmd, data);
                        }
                    }, 3000);
                }
            }
        };
        try {
            mClient.unnotify(btAddress, Constants.SERVICE_UUID,
                    Constants.CHARACTER_UUID, new BleUnnotifyResponse() {
                        @Override
                        public void onResponse(int i) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mClient.notify(btAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID, notifyResponse);
    }

    private void sendCmd(final BluetoothClient mClient, final String btAddress,
                         final int cmd, final byte[] data) {
        int dataLen = data.length;
        if (dataLen > 20) {
            int remainLen = dataLen;
            while (remainLen > 0) {
                writeData(mClient, btAddress, cmd, ByteUtils.subBytes(data,
                        dataLen - remainLen, remainLen > 20 ? 20 : remainLen));
                remainLen -= 20;
            }
        } else {
            writeData(mClient, btAddress, cmd, data);
        }
    }

    private void writeData(final BluetoothClient mClient, final String btAddress,
                           final int cmd, final byte[] data) {
        if (mClient == null) return;
        mClient.write(btAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID, data,
                new BleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        Log.e("BtBlePresenter", "send Message:" + code);
                        if (iPrinterClient != null) {
                            if (code == 0) {
                                if (cmd == Constants.CMD_REQ_SN) {
                                    iPrinterClient.getSnRequestSuccess();
                                } else if (cmd == Constants.CMD_REQ_WIFI_LIST) {
                                    iPrinterClient.getSnRequestSuccess();
                                } else if (cmd == Constants.CMD_REQ_CONNECT_WIFI) {
                                    iPrinterClient.onSetWifiSuccess();
                                }
                            } else {
                                if (cmd != Constants.CMD_REQ_WIFI_CONNECTED
                                        && cmd != Constants.CMD_REQ_DELETE_WIFI_INFO
                                        && cmd != Constants.CMD_REQ_QUIT_CONFIG) {
                                    iPrinterClient.sendDataFail(code, context.getString(R.string.tip_send_fail));
                                }
                            }
                        }
                    }
                });
    }

    private void onDataReceived(byte[] value, String btAddress) {
        if (context == null) return;
        if (value.length > 0) {
            if (Utility.isFirstPac(value)) {
                receivedLen = 0;
                receivedData = new byte[Utility.getPacLength(value)];
                System.arraycopy(value, 0, receivedData, 0, value.length);
            } else {
                System.arraycopy(value, 0, receivedData, receivedLen, value.length);
            }
            receivedLen += value.length;
            if (receivedData.length == receivedLen && iPrinterClient != null) {
                int cmd = Utility.getCmdId(receivedData);
                if (cmd == Constants.CMD_RESP_SN) {
                    iPrinterClient.onSnReceived(Utility.getSn(receivedData));
                } else if (cmd == Constants.CMD_RESP_GET_WIFI_ERROR) {
                    onResponseError(Utility.getErrorCode(receivedData));
                } else if (cmd == Constants.CMD_RESP_GET_WIFI_SUCCESS) {
                    iPrinterClient.routerFound(Utility.getRouter(receivedData));
                } else if (cmd == Constants.CMD_RESP_WIFI_AP_COMPLETELY) {// Wi-Fi信息接收完毕
                    iPrinterClient.onGetWifiListFinish();
                } else if (cmd == Constants.CMD_RESP_WIFI_CONNECTED) {
                    writeData(mClient, btAddress, cmd, Utility.cmdAlreadyConnectedWifi());
                    iPrinterClient.wifiConfigSuccess();
                }
            }
        }
    }

    private void onResponseError(int errorCode) {
        switch (errorCode) {
            case Constants.WIFI_START_ERROR:
            case Constants.WIFI_SCAN_ERROR:
                iPrinterClient.onGetWifiListFail();
                break;
            case Constants.WIFI_PAIRING_TIMEOUT:
            case Constants.WIFI_CONNECT_AP_ERROR:
            case Constants.WIFI_CONNECT_AP_TIMEOUT:
                iPrinterClient.onWifiConfigFail();
                break;
        }
    }

    public interface IPrinterClient {

        void onPrinterFount(PrinterDevice printerDevice);

        void sendDataFail(int code, String msg);

        void getSnRequestSuccess();

        void onSnReceived(String sn);

        void onGetWifiListFinish();

        void onGetWifiListFail();

        void onSetWifiSuccess();

        void wifiConfigSuccess();

        void onWifiConfigFail();

        void bindPrinterSuccess(int code, String msg, String data);

        void bindPrinterFail(int code, String msg, String data);

        void routerFound(Router router);

    }

}
