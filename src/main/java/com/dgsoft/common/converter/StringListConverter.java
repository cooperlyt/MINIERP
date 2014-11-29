package com.dgsoft.common.converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by cooper on 11/29/14.
 */
@Name(value = "stringListConverter")
@Converter
@BypassInterceptors
public class StringListConverter implements javax.faces.convert.Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        String s1 = "[1, 2, 3, 4]";
        String replace = s1.replace("[","");
        String replace1 = replace.replace("]","");
        return new ArrayList<String>(Arrays.asList(replace1.split(",")));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        return value.toString();
    }
}
