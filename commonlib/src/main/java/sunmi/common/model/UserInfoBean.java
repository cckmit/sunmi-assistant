package sunmi.common.model;

import java.io.Serializable;

/**
 * Description:
 * Created by bruce on 2019/7/30.
 */
public class UserInfoBean  implements Serializable {

    /**
     * id : 105396
     * email :
     * phone : 13524418653
     * merchant_id : 698711
     * username : 用户_ToThKc
     * origin_icon :
     * resize_icon :
     */

    private int id;
    private String email;
    private String phone;
    private int merchant_id;
    private String username;
    private String origin_icon;
    private String resize_icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrigin_icon() {
        return origin_icon;
    }

    public void setOrigin_icon(String origin_icon) {
        this.origin_icon = origin_icon;
    }

    public String getResize_icon() {
        return resize_icon;
    }

    public void setResize_icon(String resize_icon) {
        this.resize_icon = resize_icon;
    }

}
