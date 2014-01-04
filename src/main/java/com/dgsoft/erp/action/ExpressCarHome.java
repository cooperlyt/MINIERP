package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.ExpressCar;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 1/4/14
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
@Name("expressCarHome")
public class ExpressCarHome extends ErpEntityHome<ExpressCar>{

    @In(create = true)
    private TransCorpHome transCorpHome;

    @Override
    protected boolean wire(){
        getInstance().setTransCorp(transCorpHome.getInstance());
        return true;
    }

}
