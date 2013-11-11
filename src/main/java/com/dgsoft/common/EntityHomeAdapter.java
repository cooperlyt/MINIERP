package com.dgsoft.common;

import com.dgsoft.common.utils.persistence.UniqueVerify;
import com.dgsoft.common.utils.persistence.UniqueVerifys;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import javax.faces.event.ValueChangeEvent;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/8/13
 * Time: 9:51 AM
 */
public class EntityHomeAdapter<E> extends EntityHome<E> {

    public static final String UNIQUE_VERIFY_ATTRIBUTE_NAME = "unique";

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

    protected CriteriaQuery<E> createQueryFieldsEq(Map<String, Object> params) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> result = getEntityManager().getCriteriaBuilder().createQuery(getEntityClass());
        Root<E> from = result.from(getEntityClass());

        EntityType<E> et = getEntityManager().getMetamodel().entity(getEntityClass());


        Predicate predicate = null;
        for (Map.Entry<String, Object> param : params.entrySet()) {
            Predicate eq = cb.equal(from.get(et.getDeclaredSingularAttribute(param.getKey())), param.getValue());
            if (predicate == null) {
                predicate = eq;
            } else {
                predicate = cb.and(eq);
            }
        }
        if (predicate != null)
            result.where(predicate);

        return result;
    }


    private boolean verifyUnique(List<UniqueVerify> uniques) {
        boolean result = true;
        if (!uniques.isEmpty()) {
            for (UniqueVerify unique : uniques) {

                try {
                    Map<String, Object> fields = new HashMap<String, Object>();
                    for (String fieldName : unique.field()) {
                        Field field = getEntityClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        fields.put(fieldName, field.get(getInstance()));
                    }

                    for (E record : getEntityManager().createQuery(createQueryFieldsEq(fields)).getResultList()) {
                        if (!isManaged() || !getInstanceId(record,true).equals(getInstanceId(getInstance(),true))) {
                            getStatusMessages().addFromResourceBundleOrDefault(unique.severity(),
                                    getMessageKeyPrefix() + unique.name() + "_conflict", unique.name() + " conflict");
                            if (unique.severity().compareTo(StatusMessage.Severity.ERROR) >= 0) {
                                result = false;
                            }
                            break;
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("UniqueVerify define field [" + unique.name() + "] can't access");
                } catch (NoSuchFieldException e) {
                    throw new IllegalArgumentException("UniqueVerify No Such field [" + unique.name() + "]");
                }
            }
        }
        return result;
    }

    private List<UniqueVerify> getUniqueVerifys() {
        List<UniqueVerify> uniques = new ArrayList<UniqueVerify>();
        if (getEntityClass().isAnnotationPresent(UniqueVerify.class)) {
            uniques.add(getEntityClass().getAnnotation(UniqueVerify.class));
        }
        if (getEntityClass().isAnnotationPresent(UniqueVerifys.class)) {
            UniqueVerifys uniqueVerifys = getEntityClass().getAnnotation(UniqueVerifys.class);
            uniques.addAll(Arrays.asList(uniqueVerifys.value()));
        }
        return uniques;
    }

    protected boolean verifyUnique() {
        return verifyUnique(getUniqueVerifys());
    }

    private List<UniqueVerify> getUniqueVerifys(String uniqueName) {
        List<UniqueVerify> uniques = new ArrayList<UniqueVerify>();
        for (UniqueVerify uniqueVerify : getUniqueVerifys()) {
            if (uniqueName.trim().equals(uniqueVerify.name().trim())) {
                uniques.add(uniqueVerify);
            }
        }
        return uniques;
    }

    public void verifyUniqueAvailable(ValueChangeEvent e) {
        String uniqueVerifyName = e.getComponent().getAttributes().get(UNIQUE_VERIFY_ATTRIBUTE_NAME).toString();
        if (uniqueVerifyName != null) {

            for (UniqueVerify unique : getUniqueVerifys(uniqueVerifyName)) {
                if (unique.field().length != 1) {
                    throw new IllegalArgumentException("unique cannot is union unique");
                }
                Map<String, Object> fields = new HashMap<String, Object>();
                fields.put(unique.field()[0], e.getNewValue());

                for (E record : getEntityManager().createQuery(createQueryFieldsEq(fields)).getResultList()) {
                    if (!isManaged() || !getInstanceId(record,true).equals(getInstanceId(getInstance(),true))) {
                        getStatusMessages().addToControlFromResourceBundleOrDefault(e.getComponent().getId(),
                                unique.severity(),
                                getMessageKeyPrefix() + unique.name() + "_conflict", unique.name() + " conflict");
                        return;
                    }
                }
            }
        } else {
            log.warn("verifyUniqueAvailable Listener unique not define");
        }
    }

    public void verifyIdAvailable(ValueChangeEvent e) {
        if (getEntityManager().find(getEntityClass(), e.getNewValue()) != null) {
            getStatusMessages().addToControlFromResourceBundleOrDefault(e.getComponent().getId(),
                    StatusMessage.Severity.ERROR, getConflictMessageKey(),
                    getConflictMessage().getExpressionString());
        }
    }

    @Override
    protected void initDefaultMessages() {
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

    protected String getConflictMessageKey() {
        return getMessageKeyPrefix() + "conflict";
    }

    protected String getHeadingRepeatMessageKey() {
        return getMessageKeyPrefix() + "headingRepeat";
    }

    protected void conflictMessage() {
        debug("conflict entity #0 #1", getEntityClass().getName(), getId());
        getStatusMessages().addFromResourceBundleOrDefault(StatusMessage.Severity.ERROR,
                getConflictMessageKey(), getConflictMessage().getExpressionString());
    }

    private Object getInstanceId(E entityObj,boolean generated) {
        for (Field field : getEntityClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(Id.class)
                    && (generated || !field.isAnnotationPresent(GeneratedValue.class))) {
                field.setAccessible(true);
                try {
                    return field.get(entityObj);
                } catch (IllegalAccessException e) {
                    log.warn("Accessible Id field value error", e);
                    return null;
                }
            }
        }
        for (Method method : getEntityClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Id.class) &&
                    (generated || !method.isAnnotationPresent(GeneratedValue.class))) {
                method.setAccessible(true);
                try {
                    return method.invoke(entityObj);
                } catch (IllegalAccessException e) {
                    log.warn("Accessible Id method value error", e);
                    return null;
                } catch (InvocationTargetException e) {
                    log.warn("Accessible Id method value error", e);
                }
            }
        }
        log.warn("Id field not found");
        return null;
    }

    private boolean verifyPersist() {
        boolean result = verifyPersistAvailable() & verifyUnique();
        Object idValue = getInstanceId(getInstance(),false);
        if ((idValue != null) &&
                (getEntityManager().find(getEntityClass(), idValue) != null)) {
            conflictMessage();
            result = false;
        }

        return result;
    }

    private boolean verifyUpdate() {
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
