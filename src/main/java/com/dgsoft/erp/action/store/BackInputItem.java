package com.dgsoft.erp.action.store;

import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;

import java.util.List;

/**
 * Created by cooper on 2/23/14.
 */
public class BackInputItem extends StoreChangeItem{


    public BackInputItem(Res res, ResUnit useUnit) {
        super(res, useUnit);
    }

    @Override
    public List<Format> getFormats() {
        return null;
    }
}
