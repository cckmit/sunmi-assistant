package sunmi.common.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CompanyInfoResp implements Parcelable {

    private int company_id;
    private String company_name;
    private String contact_tel;
    private String contact_person;
    private String contact_email;
    private long created_time;
    private long modified_time;
    private int saas_exist;

    protected CompanyInfoResp(Parcel in) {
        company_id = in.readInt();
        company_name = in.readString();
        contact_tel = in.readString();
        contact_person = in.readString();
        contact_email = in.readString();
        created_time = in.readLong();
        modified_time = in.readLong();
        saas_exist = in.readInt();
    }

    public static final Creator<CompanyInfoResp> CREATOR = new Creator<CompanyInfoResp>() {
        @Override
        public CompanyInfoResp createFromParcel(Parcel in) {
            return new CompanyInfoResp(in);
        }

        @Override
        public CompanyInfoResp[] newArray(int size) {
            return new CompanyInfoResp[size];
        }
    };

    public int getCompany_id() {
        return company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getContact_person() {
        return contact_person;
    }

    public String getContact_tel() {
        return contact_tel;
    }

    public String getContact_email() {
        return contact_email;
    }

    public long getCreated_time() {
        return created_time;
    }

    public long getModified_time() {
        return modified_time;
    }

    public int getSaas_exist() {
        return saas_exist;
    }

    public void setSaas_exist(int saas_exist) {
        this.saas_exist = saas_exist;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setContact_person(String contact_person) {
        this.contact_person = contact_person;
    }

    public void setContact_tel(String contact_tel) {
        this.contact_tel = contact_tel;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    public void setModified_time(long modified_time) {
        this.modified_time = modified_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(company_id);
        dest.writeString(company_name);
        dest.writeString(contact_tel);
        dest.writeString(contact_person);
        dest.writeString(contact_email);
        dest.writeLong(created_time);
        dest.writeLong(modified_time);
        dest.writeInt(saas_exist);
    }
}
