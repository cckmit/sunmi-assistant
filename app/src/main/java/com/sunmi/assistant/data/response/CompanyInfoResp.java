package com.sunmi.assistant.data.response;

public class CompanyInfoResp {

    private int company_id;
    private String company_name;
    private String contact_tel;
    private String contact_person;
    private String contact_email;
    private long created_time;
    private long modified_time;
    private int saas_exist;

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
}
