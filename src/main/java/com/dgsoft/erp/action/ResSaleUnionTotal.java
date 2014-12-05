package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityLoader;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created by cooper on 12/4/14.
 */
@Name("resSaleUnionTotal")
public class ResSaleUnionTotal {

    private String customerName;

    private String saleAreaid;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSaleAreaid() {
        return saleAreaid;
    }

    public void setSaleAreaid(String saleAreaid) {
        this.saleAreaid = saleAreaid;
    }
}
