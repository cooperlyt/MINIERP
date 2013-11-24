package com.dgsoft.common.system;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ui.JpaEntityLoader;

import javax.persistence.EntityManager;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/13/13
 * Time: 4:26 PM
 */
@Name("systemEntityLoader")
@Scope(STATELESS)
public class SystemEntityLoader extends JpaEntityLoader {

    @Override
    protected String getPersistenceContextName()
    {
        return "systemEntityManager";
    }

    @In
    private EntityManager systemEntityManager;

    //TODO entityConverter saveing EntityLoader why?
    @Override
    public EntityManager getPersistenceContext(){

        return systemEntityManager;
    }
}