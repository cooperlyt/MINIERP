package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.model.TransCorp;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/12/13
 * Time: 10:48 AM
 */
@Name("transCorpHome")
public class TransCorpHome extends ErpSimpleEntityHome<TransCorp>{

    @Factory(value="transCorpTypes",scope = ScopeType.CONVERSATION)
    public TransCorp.TransCorpType[] getTransCorpTypes(){
        return TransCorp.TransCorpType.values();
    }

    @Override
    protected TransCorp createInstance(){
        return new TransCorp(true);
    }
}
