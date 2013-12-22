package javax.faces.validator;


import org.jboss.seam.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/21/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@FacesValidator("javax.faces.validator.GtZeroNumberValidator")
public class GtZeroNumberValidator implements Validator {

    public static final String NOT_GT_ZERO_MESSAGE_ID =
            "javax.faces.validator.GtZeroNumberValidator.NOT_GT_ZERO";

    public static final String TYPE_MESSAGE_ID =
            "javax.faces.validator.GtZeroNumberValidator.TYPE";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        try {
            if (new BigDecimal(value.toString()).compareTo(BigDecimal.ZERO) <= 0) {


                FacesMessage msg =
                        new FacesMessage((String)((Map)Component.getInstance("org.jboss.seam.international.messages")).get(NOT_GT_ZERO_MESSAGE_ID),
                                (String)((Map)Component.getInstance("org.jboss.seam.international.messages")).get(NOT_GT_ZERO_MESSAGE_ID));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);

            }
        } catch (NumberFormatException ex) {
            FacesMessage msg =
                    new FacesMessage((String)((Map)Component.getInstance("org.jboss.seam.international.messages")).get(TYPE_MESSAGE_ID),
                            (String)((Map)Component.getInstance("org.jboss.seam.international.messages")).get(TYPE_MESSAGE_ID));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg, ex);
        }
    }
}
