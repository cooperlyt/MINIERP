package com.dgsoft.common.converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import biz.source_code.base64Coder.Base64Coder;
import org.jboss.seam.log.Logging;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.*;

/**
 * Created by cooper on 4/28/14.
 */

@Name(value = "serializableConverter")
@Converter
@BypassInterceptors
public class SerializableConverter implements javax.faces.convert.Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            byte[] data = Base64Coder.decode(value);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                    data));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (IOException e) {
            Logging.getLog(getClass()).error("convert Serializable to Object error:" + value, e);
            return null;
        } catch (ClassNotFoundException e) {
            Logging.getLog(getClass()).error("convert Serializable to Object error:" + value, e);
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.close();
            return new String(Base64Coder.encode(baos.toByteArray()));
        } catch (IOException e) {
            Logging.getLog(getClass()).error("convert Object to Serializable error:" + value, e);
            return null;
        }
    }
}
