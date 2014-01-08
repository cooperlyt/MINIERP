package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.ExpressInfo;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/8/14
 * Time: 4:19 PM
 */
@Name("expressInfoHome")
public class ExpressInfoHome extends ErpEntityHome<ExpressInfo>{

    @In(create = true)
    private TransCorpHome transCorpHome;

    @Override
    protected boolean wire(){
        getInstance().setTransCorp(transCorpHome.getInstance());
        return true;
    }

}
