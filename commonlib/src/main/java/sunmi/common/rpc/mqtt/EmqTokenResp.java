package sunmi.common.rpc.mqtt;

/**
 * Description:
 * Created by bruce on 2019/5/8.
 */
public class EmqTokenResp {
    /**
     * username : WEB_6666
     * password : bc7e7387108fb633f45883d360af08a6
     * server_address : ws://47.96.240.44:35132
     */

    private String username;
    private String password;
    private String server_address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer_address() {
        return server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

}
