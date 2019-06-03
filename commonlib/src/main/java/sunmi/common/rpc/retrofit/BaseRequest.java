package sunmi.common.rpc.retrofit;

import java.io.Serializable;

/**
 * Description:请求基类
 * Created by bruce on 2019/2/26.
 */
public class BaseRequest implements Serializable {

    private String timeStamp;
    private String randomNum;
    private String isEncrypted;
    private String params;
    private String sign;
    private String lang;

    private BaseRequest(String timeStamp, String randomNum, String isEncrypted,
                        String params, String sign, String lang) {
        this.timeStamp = timeStamp;
        this.randomNum = randomNum;
        this.isEncrypted = isEncrypted;
        this.params = params;
        this.sign = sign;
        this.lang = lang;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRandomNum() {
        return randomNum;
    }

    public void setRandomNum(String randomNum) {
        this.randomNum = randomNum;
    }

    public String getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(String isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public static final class Builder {
        private String timeStamp;
        private String randomNum;
        private String isEncrypted;
        private String params;
        private String sign;
        private String lang;

        public Builder setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder setRandomNum(String randomNum) {
            this.randomNum = randomNum;
            return this;
        }

        public Builder setIsEncrypted(String isEncrypted) {
            this.isEncrypted = isEncrypted;
            return this;
        }

        public Builder setParams(String params) {
            this.params = params;
            return this;
        }

        public Builder setSign(String sign) {
            this.sign = sign;
            return this;
        }

        public Builder setLang(String lang) {
            this.lang = lang;
            return this;
        }

        public BaseRequest createBaseRequest() {
            return new BaseRequest(timeStamp, randomNum, isEncrypted, params, sign, lang);
        }
    }

}
