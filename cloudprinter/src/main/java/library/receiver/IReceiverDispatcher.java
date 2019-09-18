package library.receiver;

import java.util.List;

import library.receiver.listener.BluetoothReceiverListener;

/**
 * Created by dingjikerbo on 16/11/26.
 */

public interface IReceiverDispatcher {

    List<BluetoothReceiverListener> getListeners(Class<?> clazz);
}
