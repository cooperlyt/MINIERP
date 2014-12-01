package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import org.jboss.seam.annotations.Name;

/**
 * Created by cooper on 12/1/14.
 */
@Name("customerStoresTotalConditions")
public class CustomerStoresTotalConditions {

    private SearchDateArea searchDateArea = new SearchDateArea();

    private boolean searchSale = true;

    private boolean containStoreNoChange = false;

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public boolean isSearchSale() {
        return searchSale;
    }

    public void setSearchSale(boolean searchSale) {
        this.searchSale = searchSale;
    }

    public boolean isContainStoreNoChange() {
        return containStoreNoChange;
    }

    public void setContainStoreNoChange(boolean containStoreNoChange) {
        this.containStoreNoChange = containStoreNoChange;
    }

    public Boolean getStoreChangeCondition(){
        return  containStoreNoChange ? null : true;
    }
}
