package com.dgsoft.erp.business.finance;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-14
 * Time: 下午4:42
 */
@Name("accountDateHelper")
@AutoCreate
@Scope(ScopeType.APPLICATION)
@Startup
public class AccountDateHelper {

    @In
    private EntityManager erpEntityManager;

    public Date getBeginDate(){
        erpEntityManager.createQuery("select max(ID) ")
    }

    public Date getNextCloseDate(){

    }



}
