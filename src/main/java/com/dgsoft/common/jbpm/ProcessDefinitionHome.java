package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.Log;
import org.jbpm.graph.def.ProcessDefinition;
import org.richfaces.event.FileUploadEvent;

import java.io.OutputStream;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/28/13
 * Time: 10:41 AM
 */
@Name("processDefinitionHome")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.APPLICATION, dependencies = "org.jboss.seam.bpm.jbpm")
public class ProcessDefinitionHome {

    @Logger
    private Log log;

    @Transactional
    public void jpdlUploadListener(FileUploadEvent event) throws Exception {
        ProcessDefinition newProcessDefinition = ProcessDefinition.parseParZipInputStream(new ZipInputStream(event.getUploadedFile().getInputStream()));
        ManagedJbpmContext.instance().deployProcessDefinition(newProcessDefinition);
    }

    private Long id = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        log.debug("set id:" + id);
    }

    public boolean isDefine() {
        return id != null;
    }

    @Transactional
    public void processDefinitionImg(OutputStream outStream, Object data) throws Exception {
        log.debug("processDefinitionImg ---------:" + data + ":" + data == null ? "null" : (Long) data);
        if (data != null && data instanceof Long)
            outStream.write(ManagedJbpmContext.instance().getGraphSession().getProcessDefinition((Long) data).getFileDefinition().getBytes("processimage.jpg"));
    }


    public void removeDeploy() {
        if (isDefine())
            ManagedJbpmContext.instance().getGraphSession().deleteProcessDefinition(id);
    }


}
