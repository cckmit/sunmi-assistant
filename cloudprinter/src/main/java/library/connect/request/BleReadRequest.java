package library.connect.request;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import library.Code;
import library.Constants;
import library.connect.listener.ReadCharacterListener;
import library.connect.response.BleGeneralResponse;


public class BleReadRequest extends BleRequest implements ReadCharacterListener {

    private UUID mServiceUUID;
    private UUID mCharacterUUID;

    public BleReadRequest(UUID service, UUID character, BleGeneralResponse response) {
        super(response);
        mServiceUUID = service;
        mCharacterUUID = character;
    }

    @Override
    public void processRequest() {
        switch (getCurrentStatus()) {
            case Constants.STATUS_DEVICE_DISCONNECTED:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;

            case Constants.STATUS_DEVICE_CONNECTED:
                startRead();
                break;

            case Constants.STATUS_DEVICE_SERVICE_READY:
                startRead();
                break;

            default:
                onRequestCompleted(Code.REQUEST_FAILED);
                break;
        }
    }

    private void startRead() {
        if (!readCharacteristic(mServiceUUID, mCharacterUUID)) {
            onRequestCompleted(Code.REQUEST_FAILED);
        } else {
            startRequestTiming();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic, int status, byte[] value) {
        stopRequestTiming();

        if (status == BluetoothGatt.GATT_SUCCESS) {
            putByteArray(Constants.EXTRA_BYTE_VALUE, value);
            onRequestCompleted(Code.REQUEST_SUCCESS);
        } else {
            onRequestCompleted(Code.REQUEST_FAILED);
        }
    }
}
