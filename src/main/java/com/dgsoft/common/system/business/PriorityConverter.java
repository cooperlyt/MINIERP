package com.dgsoft.common.system.business;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/27/13
 * Time: 4:20 PM
 */
@Name("priorityConverter")
@Converter
@BypassInterceptors
public class PriorityConverter implements javax.faces.convert.Converter, Serializable {

    public static String convertToString(int priority){
        String result;
        switch (priority){
            case 1:
                result = "highest";
                break;
            case 2:
                result = "high";
                break;
            case 3:
                result =  "normal";
                break;
            case 4:
                result = "low";
                break;
            case 5:
                result = "lowest";
                break;
            default:
                result = "normal";
        }
        ResourceBundle resourceBundle = (ResourceBundle)Component.getInstance("org.jboss.seam.core.resourceBundle",true,true);
        return resourceBundle.getString(result);
    }

    public static Integer convertToInteger(String s){
        ResourceBundle resourceBundle = (ResourceBundle)Component.getInstance("org.jboss.seam.core.resourceBundle",true,true);
        if (s == null || s.trim().equals("")){
            return new Integer(3);
        }else if (s.trim().toLowerCase().equals(resourceBundle.getString("highest"))){
            return new Integer(1);
        }else if (s.trim().toLowerCase().equals(resourceBundle.getString("high"))){
            return new Integer(2);
        }else if (s.trim().toLowerCase().equals(resourceBundle.getString("normal"))){
            return new Integer(3);
        }else if (s.trim().toLowerCase().equals(resourceBundle.getString("low"))){
            return new Integer(4);
        }else if (s.trim().toLowerCase().equals(resourceBundle.getString("lowest"))){
            return new Integer(5);
        }
        return new Integer(3);
    }


    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return convertToInteger(s);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {

        return convertToString((Integer)o);
    }
}
