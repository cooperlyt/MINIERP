package com.dgsoft.common.jbpm;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 2/11/14
 * Time: 11:27 PM
 */
@Name("ownerTaskInstanceCacheList")
@Scope(ScopeType.SESSION)
@AutoCreate
public class OwnerTaskInstanceCacheList extends TaskInstanceListCache{

    @Override
    protected Set<TaskInstance> searchTaskInstances() {
        List<TaskInstance> taskInstanceList = (List<TaskInstance>) Component.getInstance("org.jboss.seam.bpm.taskInstanceList");
        return new HashSet<TaskInstance>(taskInstanceList);
    }
}
