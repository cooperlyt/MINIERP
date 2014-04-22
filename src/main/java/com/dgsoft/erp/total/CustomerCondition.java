package com.dgsoft.erp.total;

import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 15/04/14
 * Time: 09:38
 */
@Name("customerSearchCondition")
public class CustomerCondition {

    @In(create = true)
    private List<CustomerArea> mySaleArea;

    private String customerAreaId;

    private Integer levelFrom;

    private Integer levelTo;

    private String type;

    private Integer provinceCode;

    private String name;

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
    }

    public Integer getLevelFrom() {
        return levelFrom;
    }

    public void setLevelFrom(Integer levelFrom) {
        this.levelFrom = levelFrom;
    }

    public Integer getLevelTo() {
        return levelTo;
    }

    public void setLevelTo(Integer levelTo) {
        this.levelTo = levelTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getResultAcceptAreaIds(){
        if ((customerAreaId == null) || ("".equals(customerAreaId.trim()))){
            List<String> result = new ArrayList<String>(mySaleArea.size());
            for (CustomerArea customerArea: mySaleArea){
                result.add(customerArea.getId());
            }
            return result;
        }else{
            List<String> result = new ArrayList<String>(1);
            result.add(customerAreaId);
            return result;
        }
    }
}
