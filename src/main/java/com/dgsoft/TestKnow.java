package com.dgsoft;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesPage;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/7/13
 * Time: 4:57 PM
 */
@Name("testKnow")
public class TestKnow {

    private String v1;

    private String v2;

    @Logger
    private org.jboss.seam.log.Log log;

    public void printPageContext(){
        log.debug("print page Context-------------");
       for(String name:Contexts.getPageContext().getNames()){


          log.debug(name + ":" + Contexts.getPageContext().get(name));
       }


        org.jboss.seam.faces.FacesPage fp = (FacesPage) Contexts.getPageContext().get("org.jboss.seam.faces.facesPage");;
        log.debug("page context convertID:" +  fp.getConversationId());

        log.debug("print page Context end-------------");
    }

    public String getV1() {
        return v1;
    }

    public void setV1(String v1) {
        this.v1 = v1;
    }

    public String getV2() {

       return v2;
    }

    public void setV2(String v2) {
        this.v2 = v2;
    }
}
