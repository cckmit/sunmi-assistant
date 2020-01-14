package sunmi.common.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author yinhui
 * @date 2020-01-10
 */
public class ServiceEnableResp {

    /**
     * audit_security_status : 1
     */

    @SerializedName("audit_security_status")
    private int auditSecurityStatus;

    public int getAuditSecurityStatus() {
        return auditSecurityStatus;
    }

}
