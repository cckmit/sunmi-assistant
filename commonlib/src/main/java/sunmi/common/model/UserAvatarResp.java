package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2019-08-05
 */
public class UserAvatarResp {

    @SerializedName("origin_icon")
    private String originIcon;
    @SerializedName("resize_icon")
    private String resizeIcon;

    public String getOriginIcon() {
        return originIcon;
    }

    public String getResizeIcon() {
        return resizeIcon;
    }

}
