package library.connect.listener;

import library.model.BleGattProfile;

/**
 * Created by dingjikerbo on 2016/8/25.
 */
public interface ServiceDiscoverListener extends GattResponseListener {
    void onServicesDiscovered(int status, BleGattProfile profile);
}
