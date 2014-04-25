package com.dgsoft.common.jbpm;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
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
@Name("pooledTaskInstanceCacheList")
@Scope(ScopeType.SESSION)
@AutoCreate
public class PooledTaskInstanceCacheList extends TaskInstanceListCache{


    @Override
    protected Set<TaskInstance> searchTaskInstances() {
        List<TaskInstance> pooledTaskInstanceList = (List<TaskInstance>) Component.getInstance("org.jboss.seam.bpm.pooledTaskInstanceList");
        return new HashSet<TaskInstance>(pooledTaskInstanceList);
    }


}
