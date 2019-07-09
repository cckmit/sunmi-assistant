package com.sunmi.assistant.data.response;

import java.util.List;

/**
 * Description:
 * Created by bruce on 2019/7/2.
 */
public class CompanyListResp {

    /**
     * company_list : [{"company_id":53,"company_name":"98765432","contact_person":"person","contact_tel":"tel","contact_email":"email","created_time":1555852865,"modified_time":1555852865},{"company_id":55,"company_name":"6666666","contact_person":"","contact_tel":"","contact_email":"","created_time":1555666107,"modified_time":1555666107}]
     * total_count : 0
     * return_count : 0
     */

    private int total_count;
    private int return_count;
    private List<CompanyInfoResp> company_list;

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getReturn_count() {
        return return_count;
    }

    public void setReturn_count(int return_count) {
        this.return_count = return_count;
    }

    public List<CompanyInfoResp> getCompany_list() {
        return company_list;
    }

    public void setCompany_list(List<CompanyInfoResp> company_list) {
        this.company_list = company_list;
    }

}
