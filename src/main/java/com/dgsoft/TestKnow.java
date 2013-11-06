package com.dgsoft;

import com.dgsoft.common.utils.persistence.UniqueVerify;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.annotations.bpm.ResumeProcess;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesPage;

import javax.persistence.NamedQueries;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/7/13
 * Time: 4:57 PM
 */
@Name("testKnow")
public class TestKnow {



    public static class TestAnn{


        private String name;



        public String getName() {
            return name;
        }




        public void setName(String name) {
            this.name = name;
        }
    }


    public static void main(String[] args){



        TestAnn testAnn = new TestAnn();
        testAnn.setName("pppp");

        for (Field field : testAnn.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(UniqueVerify.class) ){
                field.setAccessible(true);
                try {
                    System.out.println("find UniqueVerify Field:" + field.getName() + "=" + field.get(testAnn));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        for (Method method: testAnn.getClass().getDeclaredMethods()){
            if (method.isAnnotationPresent(UniqueVerify.class)){
                method.setAccessible(true);
                try {
                    System.out.println("find UniqueVerify Method:" + method.getName() + "=" + method.invoke(testAnn));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }


















        System.out.println(new BigDecimal("10.0000000000").compareTo(new BigDecimal("10")));

        NumberFormat df = DecimalFormat.getCurrencyInstance(Locale.CHINA);
        //DecimalFormat df = new DecimalFormat("###,###,##0");

         //df.setRoundingMode(RoundingMode.HALF_UP);
        BigDecimal bd = new BigDecimal("-9999999.22222");


        //String vv = df.format(bd).replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()),"");

        try {
            System.out.println(df.format(bd));
            System.out.println(df.parse( df.format(bd)));

            System.out.println(df.format(new BigDecimal(df.parse( df.format(bd)).toString())));
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //df.format(bd);

        //df.format((new BigDecimal(df.format(bd))));

        //System.out.println(df.format((new BigDecimal(vv))));
    }

    private String v1;

    private String v2;

    @Logger
    private org.jboss.seam.log.Log log;

    public void printPageContext(){
        log.debug("print page Context-------------");
       for(String name:Contexts.getPageContext().getNames()){


          log.debug(name + ":" + Contexts.getPageContext().get(name));
       }


        org.jboss.seam.faces.FacesPage fp = (FacesPage) Contexts.getPageContext().get("org.jboss.seam.faces.facesPage");;
        log.debug("page context convertID:" +  fp.getConversationId());

        log.debug("print page Context end-------------");
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public String getV2() {

       return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }
}
