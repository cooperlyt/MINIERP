package com.dgsoft.common.system;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;

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

    private Map<String,Integer> numbers = new HashMap<String, Integer>();

    @In
    private RunParam runParam;

    private int getNumber(String type){
        Integer result = numbers.get(type);
        if (result == null){
            result = 1;
        }else{
            result ++;
        }
        numbers.put(type,result);
        return result;
    }

    public String getSampleNumber(String type){
        return runParam.getRunCount() + "-" + getNumber(type);
    }


    public String getDateNumber(String type){
        Integer result = getNumber(type);
        SimpleDateFormat numberDateformat=new SimpleDateFormat("yyyyMMdd");
        String datePart = numberDateformat.format(new Date());
        return datePart + "-" + result + "-" + runParam.getRunCount();
    }

}
