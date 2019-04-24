package sunmi.common.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class OKHttpUtils {

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
