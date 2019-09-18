package library;

import java.util.HashMap;
import java.util.List;

import library.connect.listener.BleConnectStatusListener;
import library.connect.listener.BluetoothStateListener;
import library.connect.response.BleNotifyResponse;
import library.receiver.listener.BluetoothBondListener;

/**
 * Created by liwentian on 2017/1/13.
 */

public class BluetoothClientReceiver {

    private HashMap<String, HashMap<String, List<BleNotifyResponse>>> mNotifyResponses;
    private HashMap<String, List<BleConnectStatusListener>> mConnectStatusListeners;
    private List<BluetoothStateListener> mBluetoothStateListeners;
    private List<BluetoothBondListener> mBluetoothBondListeners;
}
