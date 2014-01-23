package com.dgsoft.erp.tools;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:05 AM
 */
public class AllResCategoryTreeFilter extends ResCategoryManagerTreeFilter{

    @Override
    public boolean containDisable() {
        return false;
    }

    @Override
    public boolean expandedDefault() {
        return false;
    }

}
