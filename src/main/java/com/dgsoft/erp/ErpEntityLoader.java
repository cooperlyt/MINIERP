package com.dgsoft.erp;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ui.JpaEntityLoader;

import static org.jboss.seam.ScopeType.STATELESS;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/23/13
 * Time: 10:18 PM
 */
@Name("erpEntityLoader")
@Scope(STATELESS)
public class ErpEntityLoader extends JpaEntityLoader {

    @Override
    protected String getPersistenceContextName()
    {
        return "erpEntityManager";
    }
}
