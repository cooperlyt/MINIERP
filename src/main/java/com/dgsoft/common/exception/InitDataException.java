package com.dgsoft.common.exception;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.exception.Redirect;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-25
 * Time: 上午9:30
 * To change this template use File | Settings | File Templates.
 */
@ApplicationException()
public class InitDataException extends IllegalStateException {

    public InitDataException() {
        super();
    }

    public InitDataException(String msg) {
        super(msg);
    }

}
