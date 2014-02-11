package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
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
public class OwnerTaskInstanceCacheList extends TaskInstanceListCache{

    @In
    private List<TaskInstance> taskInstanceList;

    @Override
    protected Set<TaskInstance> getCurrTaskInstances() {
        return new HashSet<TaskInstance>(taskInstanceList);
    }
}
