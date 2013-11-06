package com.dgsoft.common;

import com.dgsoft.common.utils.persistence.UniqueVerify;
import com.dgsoft.common.utils.persistence.UniqueVerifys;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.Id;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private Expressions.ValueExpression conflictMessage;


    @BypassInterceptors
    public String getLastState() {
        return lastState;
    }


    protected boolean verifyUpdateAvailable() {
        return true;
    }

    protected boolean verifyRemoveAvailable() {
        return true;
    }

    protected boolean verifyPersistAvailable() {
        return true;
    }

    private boolean verifyUnique(){

        List<UniqueVerify> uniques = new ArrayList<UniqueVerify>();
        if (getEntityClass().isAnnotationPresent(UniqueVerify.class)){
            uniques.add(getEntityClass().getAnnotation(UniqueVerify.class));
        }
        if (getEntityClass().isAnnotationPresent(UniqueVerifys.class)){
            UniqueVerifys uniqueVerifys = getEntityClass().getAnnotation(UniqueVerifys.class);
            uniques.addAll(Arrays.asList(uniqueVerifys.value()));
        }
        boolean result = true;
        if (!uniques.isEmpty()){
            for (UniqueVerify unique: uniques){
                Query query;
                if (unique.namedQueryName() == null || "".equals(unique.namedQueryName().trim())){
                    query = getEntityManager().createQuery(unique.query(),getEntityClass());
                }else{
                    query = getEntityManager().createNamedQuery(unique.namedQueryName(),getEntityClass());
                }
                for (String fieldName: unique.field()){
                    try {
                        query.setParameter(fieldName,getEntityClass().getField(fieldName).get(getInstance()));
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("UniqueVerify define field [" + fieldName + "] can't access");
                    } catch (NoSuchFieldException e) {
                        throw new IllegalArgumentException("UniqueVerify No Such field [" + fieldName + "]");
                    }
                }

                if (!query.getResultList().isEmpty()){
                    getStatusMessages().addFromResourceBundleOrDefault(unique.severity(),
                            getMessageKeyPrefix() + unique.name() + "_conflict", unique.name() + " conflict");
                    if (unique.severity().compareTo(StatusMessage.Severity.ERROR) >= 0){
                        result = false;
                    }
                }
            }

        }
        return result;

    }




    public void verifyIdAvailable(ValueChangeEvent e) {
        if (getEntityManager().find(getEntityClass(), e.getNewValue()) != null){
            getStatusMessages().addToControlFromResourceBundleOrDefault(e.getComponent().getId(),
                    StatusMessage.Severity.ERROR,getConflictMessageKey(),
                    getConflictMessage().getExpressionString());
        }
    }

    @Override
    protected void initDefaultMessages(){
        super.initDefaultMessages();
        Expressions expressions = new Expressions();
        if (conflictMessage == null) {
            conflictMessage = expressions.createValueExpression("Primary key conflict");
        }
    }


    public Expressions.ValueExpression getConflictMessage() {
        return conflictMessage;
    }

    public void setConflictMessage(Expressions.ValueExpression conflictMessage) {
        this.conflictMessage = conflictMessage;
    }

    protected String getConflictMessageKey()
    {
        return getMessageKeyPrefix() + "conflict";
    }

    protected String getHeadingRepeatMessageKey(){
        return getMessageKeyPrefix() + "headingRepeat";
    }

    protected void conflictMessage() {
        debug("conflict entity #0 #1", getEntityClass().getName(), getId());
        getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.ERROR,
                getConflictMessageKey(), getConflictMessage().getExpressionString() );
    }

    private Object getInstanceId() {

        for (Field field : getEntityClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return field.get(getInstance());
                } catch (IllegalAccessException e) {
                    log.warn("Accessible Id field value error", e);
                    return null;
                }
            }
        }
        for (Method method: getEntityClass().getDeclaredMethods()){
            if (method.isAnnotationPresent(Id.class)){
                method.setAccessible(true);
                try {
                    return method.invoke(getInstance());
                } catch (IllegalAccessException e) {
                    log.warn("Accessible Id method value error",e);
                    return null;
                } catch (InvocationTargetException e) {
                    log.warn("Accessible Id method value error",e);
                }
            }
        }
        log.warn("Id field not found");
        return null;
    }

    private boolean verifyPersist(){
        boolean result = verifyPersistAvailable() & verifyUnique();
        Object idValue = getInstanceId();
        if ((idValue != null) &&
                (getEntityManager().find(getEntityClass(), idValue) != null)) {
            conflictMessage();
            result = false;
        }

        return result;
    }

    private boolean verifyUpdate(){
        return verifyUpdateAvailable() & verifyUnique();
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
        if (wire() && verifyUpdate()) {
            lastState = super.update();
        } else
            return null;
        return lastState;
    }

    @Override
    public String persist() {
        lastState = "";
        if (wire() && verifyPersist())
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
