package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.BackDispatch;
import com.dgsoft.erp.model.BackItem;
import org.jboss.seam.annotations.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 3/1/14.
 */
@Name("backDispatchHome")
public class BackDispatchHome extends ErpEntityHome<BackDispatch> {

    protected List<BackItem> backItems = new ArrayList<BackItem>(0);


    public List<BackItem> getBackItems() {
        return backItems;
    }


    @Override
    protected void initInstance(){
        super.initInstance();
        backItems = new SetLinkList<BackItem>(getInstance().getBackItems());
    }
}
