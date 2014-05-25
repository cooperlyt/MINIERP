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
import java.util.Calendar;
import java.util.Locale;

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
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.set(Calendar.YEAR,2014);
        c.set(Calendar.MONTH,0);
        c.set(Calendar.DATE,0);


        System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));


    }

}
