package com.dgsoft.common.system.business;

import com.dgsoft.common.system.model.SimpleVarDefine;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/26/13
 * Time: 4:13 PM
 */
public interface BusinessOperator {

    public abstract Map<String,SimpleVarSubscribeStore> getSimpleVars(String businessKey);

    public abstract String saveSimpleVars(String category ,Map<SimpleVarDefine,String> vars);



}
