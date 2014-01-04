package com.dgsoft.common.system;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 8/20/13
 * Time: 8:24 AM
 */
@Name("dictionaryConverter")
@Scope(CONVERSATION)
@Install(precedence = BUILT_IN)
@Converter
@BypassInterceptors
public class DictionaryConverter implements
        javax.faces.convert.Converter, Serializable {

    private DictionaryWord getDictionary() {
        return (DictionaryWord) Component.getInstance("dictionary", ScopeType.APPLICATION, true, true);
    }


    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        String wordId = (String)o;
        if (wordId == null || "".equals(wordId.trim()))
            return "";

        return  getDictionary().getWordValue(wordId);
    }
}
