package com.sunmi.cloudprinter.presenter;

import android.os.Handler;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.sunmi.cloudprinter.constant.BtBleContract;
import com.sunmi.cloudprinter.constant.Constants;
import com.sunmi.cloudprinter.rpc.IOTCloudApi;
import com.sunmi.cloudprinter.utils.Utility;

import java.util.UUID;

import sunmi.common.base.BasePresenter;
import sunmi.common.rpc.http.HttpCallback;
import sunmi.common.utils.ByteUtils;

/**
 * Description:
 * Created by bruce on 2019/1/11.
 */
public class BtBlePresenter extends BasePresenter<BtBleContract.View>
        implements BtBleContract.Presenter {

    private byte[] receivedData;
    private int receivedLen;
    private int retryCount;

    @Override
    public void bindPrinter(int shopId, String sn) {
        mView.showLoadingDialog();
        IOTCloudApi.bindPrinter(shopId, sn, new HttpCallback<String>(null) {
            @Override
            public void onSuccess(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.bindSuccess(code, msg, data);
                }
            }

            @Override
            public void onFail(int code, String msg, String data) {
                if (isViewAttached()) {
                    mView.hideLoadingDialog();
                    mView.bindFail(code, msg, data);
                }
            }
        });
    }

    @Override
    public void sendData(final BluetoothClient client, final String bleAddress, final byte[] data) {
        connectBle(client, bleAddress, data);
    }

    private void connectBle(final BluetoothClient mClient, final String btAddress, final byte[] data) {
        if (isViewAttached()) mView.showLoadingDialog();
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
        mClient.notify(btAddress, Constants.SERVICE_UUID, Constants.CHARACTER_UUID,
                new BleNotifyResponse() {
                    @Override
                    public void onNotify(UUID service, UUID character, byte[] value) {
                        Log.e("BtBlePresenter", "555555 onNotify value = " + value.length);
                        onDataReceived(value);
                    }

                    @Override
                    public void onResponse(int code) {
                        Log.e("BtBlePresenter", "initNotify onResponse, code = " + code);
                        if (code == 0) {
                            retryCount = 0;
                            sendCmd(mClient, btAddress, data);
                        } else {
                            if (retryCount > 10) {
                                retryCount = 0;
                                if (isViewAttached()) {
                                    mView.hideLoadingDialog();
                                    mView.shortTip("配置失败，请重试");
                                }
                                return;
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    retryCount++;
                                    initNotify(mClient, btAddress, data);
                                }
                            }, 2000);
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
                        if (code == 0) {

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
                if (isViewAttached()) mView.onResponse(receivedData);
            }
        }
    }

}
