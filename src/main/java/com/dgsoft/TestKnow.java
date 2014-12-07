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


    public List<TopLevel> getTopLevelDatas(){
        List<TopLevel> result = new ArrayList<TopLevel>(2);
        result.add(new TopLevel( "test Top level"));
        return result;
    }

    public static class TopLevel {

        private String title;

        private List<SecondLevel> secondLevels = new ArrayList<SecondLevel>(3);

        public TopLevel(String title) {
            this.title =  title;
            secondLevels.add(new SecondLevel("test second Level"));
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<SecondLevel> getSecondLevels() {
            return secondLevels;
        }

        public void setSecondLevels(List<SecondLevel> secondLevels) {
            this.secondLevels = secondLevels;
        }
    }

    public static class SecondLevel {
        private String title;

        private List<ThirdLevel> thirdLevels = new ArrayList<ThirdLevel>(3);


        public SecondLevel(String title) {
            this.title = title;
            thirdLevels.add(new ThirdLevel("test third Level"));
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<ThirdLevel> getThirdLevels() {
            return thirdLevels;
        }


    }

    public static class ThirdLevel{
        private String title;

        private List<String> fourthLevels = new ArrayList<String>(3);

        public ThirdLevel(String title) {
            this.title = title;
            fourthLevels.add("1");
            fourthLevels.add("2");
            fourthLevels.add("3");
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<String> getFourthLevels() {
            return fourthLevels;
        }

        public void setFourthLevels(List<String> fourthLevels) {
            this.fourthLevels = fourthLevels;
        }
    }

    private static void testDecimal(BigDecimal m){
        m = m.add(new BigDecimal("1"));

    }

    public static int testADD(){
        int i = 0;
        return i++;
    }

    public static void main(String[] args){


        System.out.println(testADD());

//        List<String> value = new ArrayList<String>();
//        value.add("1");
//        value.add("2");
//        value.add("3");
//        value.add("4");
//        System.out.println(value.toString());
//
//        String s1 = "[1, 2, 3, 4]";
//        String replace = s1.replace("[","");
//        System.out.println(replace);
//        String replace1 = replace.replace("]","");
//        System.out.println(replace1);
//        List<String> myList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
//        System.out.println(myList.toString());

    }

}
