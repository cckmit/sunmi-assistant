package com.sunmi.assistant.data.response;

public class CompanyInfoResponse {

    private int company_id;
    private String company_name;
    private String contact_person;
    private String contact_tel;
    private String contact_email;
    private long created_time;
    private long modified_time;

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
}
