package com.dgsoft.common;

import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import java.lang.annotation.Annotation;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/8/13
 * Time: 9:51 AM
 */
public class EntityHomeAdapter<E> extends EntityHome<E> {

    @Logger
    protected Log log;

    private String lastState = "";

    @BypassInterceptors
    public String getLastState() {
        return lastState;
    }

    protected boolean verifyUpdateAvailable() {
        return true;
    }

    protected boolean verifyPersistAvailable() {
        return true;
    }

    protected boolean verifyRemoveAvailable() {
        return true;
    }

    protected boolean wire() {
        return true;
    }

    public E getReadyInstance() {
        if (wire()) {
            return getInstance();
        } else {
            return null;
        }
    }

    @Transactional
    @End
    public String updateEnd() {
        return this.update();
    }

    @Transactional
    @End
    public String persistEnd() {
        return this.persist();
    }

    @Transactional
    @End
    public String removeEnd() {
        return this.remove();
    }

    @Override
    public void create() {
        super.create();
        lastState = "";
    }

    @Override
    public String update() {
        lastState = "";
        if (wire() && verifyUpdateAvailable()) {
            lastState = super.update();
        } else
            return null;
        return lastState;
    }

    @Override
    public String persist() {
        lastState = "";
        if (wire() && verifyPersistAvailable())
            lastState = super.persist();
        else
            return null;

        return lastState;
    }

    public String removeAndClear() {
        String result = remove();
        if ("removed".equals(result)) {
            clearInstance();
        }
        return result;
    }

    @Override
    public String remove() {
        lastState = "";
        if (verifyRemoveAvailable())
            lastState = super.remove();
        else
            return null;
        return lastState;
    }

    @Override
    public E find() {
        lastState = "";
        return super.find();
    }

    @Override
    protected E loadInstance() {
        lastState = "";
        return super.loadInstance();
    }

    @Override
    protected E createInstance() {
        lastState = "";
        return super.createInstance();
    }

    public void refresh() {
        if (isIdDefined() && isManaged()) {
            getEntityManager().refresh(getInstance());
        } else {
            clearInstance();
        }
    }

}
