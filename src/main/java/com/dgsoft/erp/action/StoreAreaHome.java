package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.StoreArea;
import org.jboss.seam.annotations.Name;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/1/13
 * Time: 8:53 PM
 */
@Name("storeAreaHome")
public class StoreAreaHome extends ErpEntityHome<StoreArea> {

    private Set<String> allStoreAreaIds = null;

    public String getStoreAreaTitle() {
        String result = "";
        if (isIdDefined()) {

            StoreArea storeArea = getInstance();
            result = storeArea.getName();
            while (storeArea.getStoreArea() != null) {
                storeArea = storeArea.getStoreArea();
                result = storeArea.getName() + ">" + result;
            }
        }
        return result;
    }

    private Set<String> subStoreAreaIds(StoreArea storeArea) {
        Set<String> result = new HashSet<String>();
        result.add(storeArea.getId());
        if (!storeArea.getStoreAreas().isEmpty()) {
            for (StoreArea subStoreArea : storeArea.getStoreAreas()) {
                result.addAll(subStoreAreaIds(subStoreArea));
            }
        }


        return result;
    }

    @Override
    protected void initInstance() {
        super.initInstance();
        if (isIdDefined()) {
            allStoreAreaIds = subStoreAreaIds(getInstance());
        } else {
            allStoreAreaIds = null;
        }
    }

    public Set<String> getAllStoreAreaIds() {
        if (isIdDefined()) {
            return allStoreAreaIds;
        } else return null;
    }

}
