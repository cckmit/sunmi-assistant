package com.sunmi.ipc.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import sunmi.common.base.BaseApplication;
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

    @Override
    protected SSLSocketFactory getSSLSocketFactory() {
        return generate();
//        SSLSocketFactory sslSocketFactory = null;
//        try {
////            // load client private key
////            PEMParser pemParser = new PEMParser(new InputStreamReader(clientKeyFile));
////            Object object = pemParser.readObject();
////            PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
////                    .build(password.toCharArray());
////            JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
////                    .setProvider("BC");
////            KeyPair key;
////            if (object instanceof PEMEncryptedKeyPair) {
////                LogUtils.e("Encrypted key - we will use provided password");
////                key = converter.getKeyPair(((PEMEncryptedKeyPair) object)
////                        .decryptKeyPair(decProv));
////            } else {
////                LogUtils.e("Unencrypted key - no password needed");
////                key = converter.getKeyPair((PEMKeyPair) object);
////            }
////            pemParser.close();
//
//
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
    }

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

    private static final String PROTOCOL = "TLS";

    private static final String KEY_STORE_FILE = "client.bks";

    private static final String TRUST_KEY_STORE_FILE = "server.bks";

    private String storePass = "sunmi388";

    private String keyPass = "sunmi388";

    private SSLSocketFactory sslSocketFactory;

    public synchronized SSLSocketFactory generate() {
        if (sslSocketFactory != null) {
            return sslSocketFactory;
        }
        try {
            KeyManager[] keyManagers = createKeyManagers();
            TrustManager[] trustManagers = createTrustManagers();
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
            sslContext.init(keyManagers, trustManagers, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }

    private KeyManager[] createKeyManagers() {
        KeyManager[] result;
        InputStream inputStream = null;
        try {
            inputStream = BaseApplication.getContext().getAssets().open(KEY_STORE_FILE);
//                    SSLSocketFactoryGenerator.class.getResourceAsStream(KEY_STORE_FILE);
            if (inputStream == null) {
                throw new NullPointerException("can not read jks file:" + KEY_STORE_FILE);
            }
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(inputStream, storePass.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyPass.toCharArray());
            result = keyManagerFactory.getKeyManagers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private TrustManager[] createTrustManagers() {
        TrustManager[] result;
        InputStream inputStream = null;
        try {
            inputStream = BaseApplication.getContext().getAssets().open(TRUST_KEY_STORE_FILE);
//            inputStream = SSLSocketFactoryGenerator.class.getResourceAsStream(TRUST_KEY_STORE_FILE);
            if (inputStream == null) {
                throw new NullPointerException("can not read jks file:" + TRUST_KEY_STORE_FILE);
            }
            KeyStore keyStore = KeyStore.getInstance("BKS");
            keyStore.load(inputStream, storePass.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            result = trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
