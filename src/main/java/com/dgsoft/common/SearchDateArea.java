package com.dgsoft.common;

import org.jboss.seam.annotations.Name;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;

/**
 * Created by cooper on 3/4/14.
 */
@Name("searchDateArea")
public class SearchDateArea {

    public SearchDateArea() {
        this.dateFrom = new Date();
        this.dateTo = new Date();
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
        this.dateTo = DataFormat.halfTime(dateTo);
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = DataFormat.halfTime(dateFrom);
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

    public <X> TypedQuery<X> setQueryParam(TypedQuery<X> query){
        setQueryParam((Query)query);
        return query;
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
