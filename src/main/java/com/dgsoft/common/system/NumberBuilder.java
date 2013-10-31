package com.dgsoft.common.system;

import com.dgsoft.common.system.model.NumberPool;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.log.Logging;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/8/13
 * Time: 4:56 PM
 */
@Name("numberBuilder")
@Scope(ScopeType.APPLICATION)
@AutoCreate
@Startup
@Synchronized
public class NumberBuilder {

    private static final int DEFAULT_STEP = 10;

    private class Numbers {
        long maxNumber;
        long nextNumber;

        Numbers() {
            maxNumber = DEFAULT_STEP + 1;
            nextNumber = 1;
        }

        Numbers(long nextNumber, long maxNumber){
            this.maxNumber = maxNumber;
            this.nextNumber = nextNumber;
        }

    }

    private Map<String, Numbers> numbers = new HashMap<String, Numbers>();


    private EntityManager getSystemEntityManger(){
        return (EntityManager) Component.getInstance("systemEntityManager", ScopeType.APPLICATION, true);
    }

    @Destroy
    public void onDestroy(){
        EntityManager entityManager = getSystemEntityManger();
        for(Map.Entry<String, Numbers> entry: numbers.entrySet()){
            NumberPool numberPool = entityManager.find(NumberPool.class,entry.getKey());
            numberPool.setNumber(entry.getValue().nextNumber);
        }
        entityManager.flush();
    }

    @In
    private RunParam runParam;

    @Transactional
    public long getNumber(String type) {

        Numbers result = numbers.get(type);
        long resultNumber;
        if (result == null) {

            EntityManager entityManager = getSystemEntityManger();
            NumberPool numberPool = entityManager.find(NumberPool.class,type);
            Numbers newNumber;


            if (numberPool == null){
                newNumber = new Numbers();
                entityManager.persist(new NumberPool(type,newNumber.maxNumber,DEFAULT_STEP));
                entityManager.flush();
                Logging.getLog(this.getClass()).debug("flush entityManager from numberBuild");
            }else{
                newNumber = new Numbers(numberPool.getNumber(),
                        numberPool.getPoolSize() + numberPool.getNumber());
                numberPool.setNumber(newNumber.maxNumber);
                entityManager.flush();
                Logging.getLog(this.getClass()).debug("flush entityManager from numberBuild");
            }
            numbers.put(type,newNumber);
            resultNumber = newNumber.nextNumber;
            newNumber.nextNumber = newNumber.nextNumber + 1;

        } else {
            resultNumber = result.nextNumber;
            result.nextNumber = result.nextNumber + 1;
            if (result.nextNumber >= result.maxNumber){
                EntityManager entityManager = getSystemEntityManger();
                NumberPool pool = entityManager.find(NumberPool.class,type);
                pool.setNumber(result.nextNumber + pool.getPoolSize());
                entityManager.flush();
                Logging.getLog(this.getClass()).debug("flush entityManager from numberBuild");
                result.maxNumber = pool.getNumber();
            }
        }
        return resultNumber;
    }

    public synchronized String getSampleNumber(String type) {
        return runParam.getRunCount() + "-" + getNumber(type);
    }


    public synchronized String getDateNumber(String type) {
        long result = getNumber(type);
        SimpleDateFormat numberDateformat = new SimpleDateFormat("yyyyMMdd");
        String datePart = numberDateformat.format(new Date());
        return datePart + "-" + result;
    }

}
