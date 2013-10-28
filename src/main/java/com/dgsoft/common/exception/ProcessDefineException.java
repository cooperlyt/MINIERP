package com.dgsoft.common.exception;

import org.jboss.seam.annotations.ApplicationException;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/28/13
 * Time: 5:41 PM
 */
@ApplicationException()
public class ProcessDefineException extends IllegalStateException{


    public ProcessDefineException(String msg) {
        super(msg);
    }

    public ProcessDefineException(){
        super();
    }
}
