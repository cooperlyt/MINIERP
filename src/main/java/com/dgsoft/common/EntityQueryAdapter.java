package com.dgsoft.common;

import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.persistence.QueryParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/19/13
 * Time: 11:42 AM
 */
public class EntityQueryAdapter<E> extends EntityQuery<E> {


    private static final String COUNT_PATTERN = "select\\s*count\\([^\\)+]\\)";

    private Map<String,Map<String,Number>> totalResults = new HashMap<String, Map<String, Number>>();

    protected String getTotalEjbql(String logic, String patch) {
        String countEjbql = getCountEjbql();
        return countEjbql.replaceFirst(COUNT_PATTERN,"select " + logic + "(" + patch + ") ");
    }


    protected javax.persistence.Query createTotalQuery(String logic, String path)
    {
        parseEjbql();

        evaluateAllParameters();

        joinTransaction();

        javax.persistence.Query query = getEntityManager().createQuery(getTotalEjbql(logic, path));
        setParameters( query, getQueryParameterValues(), 0 );
        setParameters( query, getRestrictionParameterValues(), getQueryParameterValues().size() );
        return query;
    }


    public Number getResultTotalMin(String path){
        return getResultTotal("MIN",path);
    }

    public Number getResultTotalMax(String path){
        return getResultTotal("MAX",path);
    }


    public Number getResultTotalAvg(String path){
        return getResultTotal("AVG",path);
    }

    public Number getResultTotalSum(String path){
        return getResultTotal("SUM",path);
    }



    protected Number getResultTotal(String logic, String path){

        if (isAnyParameterDirty())
        {
            refresh();
            totalResults.clear();
        }

        Number result;
        Map<String,Number> logicTotals = totalResults.get(logic);
        if (logicTotals == null){
            logicTotals = new HashMap<String, Number>();


            javax.persistence.Query query = createTotalQuery(logic,path);
            result = query==null ?
                    null : (Number) query.getSingleResult();
            logicTotals.put(path,result);
            totalResults.put(logic,logicTotals);
        }else{
            result = logicTotals.get(path);
            if (result == null){
                javax.persistence.Query query = createTotalQuery(logic,path);
                result = query==null ?
                        null : (Number) query.getSingleResult();
                logicTotals.put(path,result);
            }
        }
        return result;
    }

    private void setParameters(javax.persistence.Query query, List<Object> parameters, int start)
    {
        for (int i=0; i<parameters.size(); i++)
        {
            Object parameterValue = parameters.get(i);
            if ( isRestrictionParameterSet(parameterValue) )
            {
                query.setParameter( QueryParser.getParameterName(start + i), parameterValue );
            }
        }
    }





}
