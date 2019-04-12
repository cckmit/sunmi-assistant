package com.sunmi.ipc.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import sunmi.common.base.BaseApplication;

/**
 * Description:
 * Created by bruce on 2019/4/11.
 */
public class SSLSocketFactoryGenerator {

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
