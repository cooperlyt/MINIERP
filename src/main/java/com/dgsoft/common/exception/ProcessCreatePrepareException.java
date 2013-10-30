package com.dgsoft.common.exception;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/30/13
 * Time: 3:48 PM
 */
public class ProcessCreatePrepareException extends IllegalArgumentException {

    public ProcessCreatePrepareException(){
       super();
    }

    public ProcessCreatePrepareException(String msg){
        super(msg);
    }
}
