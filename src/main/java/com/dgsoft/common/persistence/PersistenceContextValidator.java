package com.dgsoft.common.persistence;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.core.Expressions;

import javax.persistence.EntityManager;

@Name("persistenceContextValidator")
@Scope(ScopeType.APPLICATION)
@Startup
public class PersistenceContextValidator {

    private Expressions.ValueExpression<EntityManager> systemEntityManager;

    @Create
    public void onStartup(){
        if (systemEntityManager != null){
            try {
                EntityManager em = systemEntityManager.getValue();
                systemEntityManager.setValue(null);
            }catch (Exception e){
                throw new RuntimeException("The persistence context " + systemEntityManager.getExpressionString() + "is not properly configured.",e);
            }
        }
    }


    public void setEntityManager(Expressions.ValueExpression<EntityManager> entityManager){
        this.systemEntityManager = entityManager;
    }

}
