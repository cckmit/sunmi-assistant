package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yangShiJie
 * @date 2019/8/21
 */
public class SsoTokenResp {

    /**
     * sso_token : 0b629dc4cf8c589b60c61d5368ff1e65
     */

    @SerializedName("sso_token")
    private String ssoToken;

    public String getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
    }
}
