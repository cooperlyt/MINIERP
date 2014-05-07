package com.dgsoft.common.system;

import com.dgsoft.common.system.model.SystemParam;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.log.Logging;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/8/13
 * Time: 3:52 PM
 */
@Name("runParam")
@AutoCreate
@Scope(ScopeType.APPLICATION)
@Startup
public class RunParam {

    private static final String PARAM_RUN_COUNT_ID = "system_run_count";

    private int runCount;

    private Map<String, SystemParam> systemParams = new HashMap<String, SystemParam>();

    @Create
    @Transactional
    public void load() {

        EntityQuery<SystemParam> systemParamList = (EntityQuery<SystemParam>) Component.getInstance("systemParamList", true, true);

        Logging.getLog(getClass()).debug("systemParamList" + systemParamList);

        loadParams(systemParamList);

        SystemParam runCounterParam = systemParams.get(PARAM_RUN_COUNT_ID);

        if (runCounterParam != null){
            runCount = Integer.valueOf(runCounterParam.getValue());
            runCount++;
            runCounterParam.setValue(String.valueOf(runCount));
        }else{
            runCounterParam = new SystemParam(PARAM_RUN_COUNT_ID,SystemParam.ParamType.INTEGER,"1");
            runCount = 1;
            systemParamList.getEntityManager().persist(runCounterParam);
        }
        systemParamList.getEntityManager().flush();
    }

    private void loadParams(EntityQuery<SystemParam> systemParamList){
        systemParams.clear();

        for (SystemParam param : systemParamList.getResultList()) {
            systemParams.put(param.getId(), param);
        }

    }

    public void refresh(){
        loadParams((EntityQuery<SystemParam>) Component.getInstance("systemParamList", true, true));
    }



    public int getIntParamValue(String name){
        SystemParam systemParam = systemParams.get(name);
        if (systemParam == null){
            throw new IllegalArgumentException("not have system Param:" + name);
        }else if (!systemParam.getType().equals(SystemParam.ParamType.INTEGER)){
            throw new IllegalArgumentException("system Param:" + name + " type not a Integer");
        }else{
            return Integer.parseInt(systemParam.getValue());
        }
    }

    public float getFloatParamValue(String name){
        SystemParam systemParam = systemParams.get(name);
        if (systemParam == null){
            throw new IllegalArgumentException("not have system Param:" + name);
        }else if (!systemParam.getType().equals(SystemParam.ParamType.FLOAT)){
            throw new IllegalArgumentException("system Param:" + name + " type not a FLOAT");
        }else{
            return Float.parseFloat(systemParam.getValue());
        }
    }

    public String getStringParamValue(String name){
        SystemParam systemParam = systemParams.get(name);
        if (systemParam == null){
            throw new IllegalArgumentException("not have system Param:" + name);
        }else if (!systemParam.getType().equals(SystemParam.ParamType.STRING)){
            throw new IllegalArgumentException("system Param:" + name + " type not a STRING");
        }else{
            return systemParam.getValue();
        }
    }


    public double getDoubleParamValue(String name){
        SystemParam systemParam = systemParams.get(name);
        if (systemParam == null){
            throw new IllegalArgumentException("not have system Param:" + name);
        }else if (!systemParam.getType().equals(SystemParam.ParamType.DOUBLE)){
            throw new IllegalArgumentException("system Param:" + name + " type not a DOUBLE");
        }else{
            return Double.parseDouble(systemParam.getValue());
        }
    }

    public boolean getBooleanParamValue(String name){
        SystemParam systemParam = systemParams.get(name);
        if (systemParam == null){
            throw new IllegalArgumentException("not have system Param:" + name);
        }else if (!systemParam.getType().equals(SystemParam.ParamType.BOOLEAN)){
            throw new IllegalArgumentException("system Param:" + name + " type not a DOUBLE");
        }else{
            return systemParam.getValue().trim().toLowerCase().equals("true") || systemParam.getValue().trim().equals('1');
        }
    }

    public int getRunCount() {
        return runCount;
    }
}
