package com.dgsoft.common.system.action;

import com.dgsoft.common.system.SystemEntityQuery;
import com.dgsoft.common.system.model.City;
import com.dgsoft.common.system.model.Person;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/2/13
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("cityList")
@Scope(ScopeType.CONVERSATION)
public class CityList extends SystemEntityQuery<City>{


    private static final String EJBQL = "select city from City city ";

    private static final String[] RESTRICTIONS = {"city.province.id = #{cityList.provinceId}"};

    private Integer provinceId;

    public CityList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }
}
