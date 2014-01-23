package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 9:29 AM
 */
public class AllResTreeFilter extends ResManagerTreeFilter {

    @Override
    public boolean containDisable() {
        return false;
    }

    @Override
    public boolean expandedDefault() {
        return false;
    }

}
