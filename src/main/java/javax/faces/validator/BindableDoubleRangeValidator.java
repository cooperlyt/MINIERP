package javax.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * Created by cooper on 3/12/14.
 */
@FacesValidator("javax.faces.validator.bindableDoubleRangeValidator")
public class BindableDoubleRangeValidator extends DoubleRangeValidator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Object minimumValue = component.getAttributes().get("minimum");
        if (minimumValue != null){
            if (minimumValue instanceof Number){
                setMinimum(((Number) minimumValue).doubleValue());
            }else {
                setMinimum(Double.parseDouble(minimumValue.toString()));
            }

        }

        Object maximumValue = component.getAttributes().get("maximum");

        if (maximumValue != null){
            if (maximumValue instanceof Number){
                setMaximum(((Number) maximumValue).doubleValue());
            }else {
                setMaximum(Double.parseDouble(maximumValue.toString()));
            }

        }

        super.validate(context, component, value);
    }
}
