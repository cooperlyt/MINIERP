package com.dgsoft;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.AccountOper;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesPage;
import org.jboss.seam.log.Logging;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/7/13
 * Time: 4:57 PM
 */
@Name("testKnow")
public class TestKnow {


    private static void testDecimal(BigDecimal m){
        m = m.add(new BigDecimal("1"));

    }

    public static void main(String[] args){

        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");
        System.out.println(value.toString());

        String s1 = "[1, 2, 3, 4]";
        String replace = s1.replace("[","");
        System.out.println(replace);
        String replace1 = replace.replace("]","");
        System.out.println(replace1);
        List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
        System.out.println(myList.toString());

    }

}
