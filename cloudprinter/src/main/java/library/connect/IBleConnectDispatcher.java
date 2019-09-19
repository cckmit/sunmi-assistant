package library.connect;

import library.connect.request.BleRequest;

public interface IBleConnectDispatcher {

    void onRequestCompleted(BleRequest request);
}
