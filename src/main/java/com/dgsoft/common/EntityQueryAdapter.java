package com.dgsoft.common;

import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Logging;
import org.jboss.seam.persistence.QueryParser;

import java.util.ArrayList;
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

    public List<Long> getShowPageNumbers(Long maxPageCount) {
        long halfCount = (maxPageCount.longValue() - 1) / 2;
        long beginPage = getPage().longValue() - halfCount;
        if (beginPage <= 0) {
            beginPage = 1;
        }
        if ((getPageCount().longValue() - beginPage + 1) < maxPageCount) {
            beginPage = getPageCount().longValue() - (maxPageCount - 1);
        }
        if (beginPage <= 0) {
            beginPage = 1;
        }


        List<Long> result = new ArrayList<Long>(maxPageCount.intValue());
        for (int i = 0; i < maxPageCount; i++) {
            if (beginPage > getPageCount()) {
                break;
            }
            result.add(Long.valueOf(beginPage));

            beginPage++;
        }
        return result;
    }

    private String orderExpress;

    private String order;

    public String getOrderExpress() {
        return orderExpress;
    }

    public void setOrderExpress(String orderExpress) {
        this.orderExpress = orderExpress;
    }

    @Override
    public void setOrder(String order)
    {
        this.order = order;
        refresh();
    }

    @Override
    public String getOrder() {
        String column = getOrderColumn();

        if (column == null){
            column = getOrderExpress();
        }

        if (column == null) {
            return order;
        }

        String direction = getOrderDirection();

        if (direction == null) {
            return column;
        } else {
            return column + ' ' + direction;
        }
    }

    public Long getPage() {

        if ((getFirstResult() == null) || getFirstResult().equals(0)) {
            return Long.valueOf(1);
        }

        return getFirstResult().longValue() / getMaxResults().longValue() + 1;
    }


//    @Override
//    public void setFirstResult(Integer firstResult){
//        super.setFirstResult(firstResult);
//        Logging.getLog(getClass()).debug("setFirstResult: " + firstResult);
//    }

    private Map<String, Map<String, Number>> totalResults = new HashMap<String, Map<String, Number>>();

    protected String getTotalEjbql(String logic, String patch) {
        String countEjbql = getCountEjbql();
        return countEjbql.replaceFirst(COUNT_PATTERN, "select " + logic + "(" + patch + ") ");
    }


    protected javax.persistence.Query createTotalQuery(String logic, String path) {
        parseEjbql();

        evaluateAllParameters();

        joinTransaction();

        javax.persistence.Query query = getEntityManager().createQuery(getTotalEjbql(logic, path));
        setParameters(query, getQueryParameterValues(), 0);
        setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
        return query;
    }


    public Number getResultTotalMin(String path) {
        return getResultTotal("MIN", path);
    }

    public Number getResultTotalMax(String path) {
        return getResultTotal("MAX", path);
    }


    public Number getResultTotalAvg(String path) {
        return getResultTotal("AVG", path);
    }

    public Number getResultTotalSum(String path) {
        return getResultTotal("SUM", path);
    }


    protected Number getResultTotal(String logic, String path) {

        if (isAnyParameterDirty()) {
            refresh();
            totalResults.clear();
        }

        Number result;
        Map<String, Number> logicTotals = totalResults.get(logic);
        if (logicTotals == null) {
            logicTotals = new HashMap<String, Number>();


            javax.persistence.Query query = createTotalQuery(logic, path);
            result = query == null ?
                    null : (Number) query.getSingleResult();
            logicTotals.put(path, result);
            totalResults.put(logic, logicTotals);
        } else {
            result = logicTotals.get(path);
            if (result == null) {
                javax.persistence.Query query = createTotalQuery(logic, path);
                result = query == null ?
                        null : (Number) query.getSingleResult();
                logicTotals.put(path, result);
            }
        }
        return result;
    }

    private void setParameters(javax.persistence.Query query, List<Object> parameters, int start) {
        for (int i = 0; i < parameters.size(); i++) {
            Object parameterValue = parameters.get(i);
            if (isRestrictionParameterSet(parameterValue)) {
                query.setParameter(QueryParser.getParameterName(start + i), parameterValue);
            }
        }
    }


}
