package com.dgsoft.common.helper;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Logging;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/19/13
 * Time: 2:55 PM
 */

@Name("searchDateConverter")
@org.jboss.seam.annotations.faces.Converter
@BypassInterceptors
public class SearchDateConvert implements Converter {


    private String pattern = "yyyy-MM-dd";

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);

        try {
            return df.parse(s);
        } catch (ParseException e) {
            Logging.getLog(this.getClass()).warn("parse error:",e);
            return null;
        }

    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.format(o);
        } catch (IllegalArgumentException e) {
            Logging.getLog(this.getClass()).warn("format error:",e);
            return null;
        }

    }
}
