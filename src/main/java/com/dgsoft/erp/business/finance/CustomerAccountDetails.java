package com.dgsoft.erp.business.finance;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-19
 * Time: 下午4:20
 */

@Name("customerAccountDetails")
public class CustomerAccountDetails {

    @In
    private EntityManager erpEntityManager;





}
