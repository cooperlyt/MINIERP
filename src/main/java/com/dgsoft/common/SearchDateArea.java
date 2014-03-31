package com.dgsoft.common;

import javax.persistence.Query;
import java.util.Date;

/**
 * Created by cooper on 3/4/14.
 */
public class SearchDateArea {

    public SearchDateArea() {
    }


    public SearchDateArea(Date dateFrom, Date dateTo) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    private Date dateFrom;

    private Date dateTo;


    public Date getSearchDateTo() {
        if (dateTo == null) {
            return null;
        }
        return new Date(dateTo.getTime() + 24 * 60 * 60 * 1000 - 1);
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String genConditionSQL(String path, boolean addAnd){
        String result = "";
        if (dateFrom != null){
            result = path + " >= :dateFrom";
        }
        if (dateTo != null){
            if (!result.trim().equals("")){
                result += " AND ";
            }
            result += path + " <= :dateTo";
        }
        if (addAnd && !result.trim().equals("")){
            result = " AND " + result;
        }
        return result;
    }

    public Query setQueryParam(Query query){
        if (dateFrom != null){
            query.setParameter("dateFrom", getDateFrom());
        }
        if (dateTo != null){
            query.setParameter("dateTo", getSearchDateTo());
        }
        return query;
    }
}
