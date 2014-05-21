package com.dgsoft.erp.business.finance;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Validator;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by cooper on 5/21/14.
 */
@Name(value = "operDateValidator")
@Validator
@BypassInterceptors
public class OperDateValidator  implements javax.faces.validator.Validator,
        Serializable {
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if(value == null){
            return;
        }

        if (!(value instanceof Date)){
            ((UIInput) component).setValid(false);
            FacesMessage message = new FacesMessage();

            message.setDetail((String)((Map) Component.getInstance("org.jboss.seam.international.messages")).get("com.dgsoft.validator.OperDateValidator.TYPE"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        if (((Date)value).compareTo(AccountDateHelper.instance().getNextBeginDate()) >= 0) {
             ((UIInput) component).setValid(true);

        } else {
            ((UIInput) component).setValid(false);
            FacesMessage message = new FacesMessage();
            message.setDetail((String)((Map) Component.getInstance("org.jboss.seam.international.messages")).get("com.dgsoft.validator.OperDateValidator.CLOSED"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

    }
}
