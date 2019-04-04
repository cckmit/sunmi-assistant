package com.sunmi.ipc.rpc;

import java.util.Map;

import sunmi.common.rpc.sunmicall.BaseLocalApi;
import sunmi.common.rpc.sunmicall.ResponseBean;

/**
 * Description:
 * Created by bruce on 2019/3/29.
 */
public class IPCLocalApi extends BaseLocalApi {
    @Override
    public String getBaseUrl() {
        return IpcConstants.IPC_IP;
    }

//    @Override
//    protected SSLSocketFactory getSSLSocketFactory() {
//        return super.getSSLSocketFactory();
//        SSLSocketFactory sslSocketFactory = null;
//        try {
//            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
//            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);// 客户端信任的服务器端证书
//            KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);//读取证书
//            InputStream ksIn = BaseApplication.getContext().getAssets().open(CLIENT_PRI_KEY);
//            InputStream tsIn = BaseApplication.getContext().getAssets().open(TRUSTSTORE_PUB_KEY);//加载证书
//            keyStore.load(ksIn, CLIENT_BKS_PASSWORD.toCharArray());
//            trustStore.load(tsIn, TRUSTSTORE_BKS_PASSWORD.toCharArray());
//            ksIn.close();
//            tsIn.close();
//            //初始化SSLContext
//            SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TYPE);
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERTIFICATE_FORMAT);
//            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(CERTIFICATE_FORMAT);
//            trustManagerFactory.init(trustStore);
//            keyManagerFactory.init(keyStore, CLIENT_BKS_PASSWORD.toCharArray());
//            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
//
//            sslSocketFactory = sslContext.getSocketFactory();
//        } catch (KeyStoreException e) {
//        }//省略各种异常处理，请自行添加
//        return sslSocketFactory;
//    }

    @Override
    public Map<String, String> getHeader() {
        return null;
    }

    @Override
    public void onFail(ResponseBean res) {

    }

    @Override
    public void onSuccess(String result, String sn) {

    }

}
