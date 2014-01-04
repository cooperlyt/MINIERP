package com.dgsoft.common.exception;

import org.jboss.seam.annotations.ApplicationException;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/30/13
 * Time: 3:48 PM
 */
@ApplicationException()
public class ProcessCreatePrepareException extends IllegalArgumentException {

    public ProcessCreatePrepareException(){
       super();
    }

    public ProcessCreatePrepareException(String msg){
        super(msg);
    }
}
