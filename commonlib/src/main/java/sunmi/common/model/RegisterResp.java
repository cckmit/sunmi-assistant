package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 * Created by bruce on 2019/7/30.
 */
public class RegisterResp implements Parcelable {

    /**
     * token : eyJhbGc...
     * id : 245
     * username :
     * email :
     * phone : 15021550047
     */

    private String token;
    private int id;
    private String username;
    private String email;
    private String phone;

    protected RegisterResp(Parcel in) {
        token = in.readString();
        id = in.readInt();
        username = in.readString();
        email = in.readString();
        phone = in.readString();
    }

    public static final Creator<RegisterResp> CREATOR = new Creator<RegisterResp>() {
        @Override
        public RegisterResp createFromParcel(Parcel in) {
            return new RegisterResp(in);
        }

        @Override
        public RegisterResp[] newArray(int size) {
            return new RegisterResp[size];
        }
    };

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
    }

}
