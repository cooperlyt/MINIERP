package com.dgsoft.common.system.business;

import com.dgsoft.common.BuiltInFilterBean;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;

import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/31/13
 * Time: 9:43 AM
 */
@Name("taskSortFilter")
@Scope(ScopeType.PAGE)
@Role(name="pooledTaskSortFilter",scope=ScopeType.PAGE)
public class TaskSortFilter extends BuiltInFilterBean {



    private String taskNameFilter;

    private String descripitionFilter;

    private String stateVar;

    private List<String> taskSortPriorities;

    private boolean multiSort = false;

    public TaskSortFilter(){
        taskSortPriorities = new ArrayList<String>();
    }


    public String getTaskNameFilter() {
        return taskNameFilter;
    }

    public void setTaskNameFilter(String taskNameFilter) {
        this.taskNameFilter = taskNameFilter;
    }

    public String getDescripitionFilter() {
        return descripitionFilter;
    }

    public void setDescripitionFilter(String descripitionFilter) {
        this.descripitionFilter = descripitionFilter;
    }

    public String getStateVar() {
        return stateVar;
    }

    public void setStateVar(String stateVar) {
        this.stateVar = stateVar;
    }

    public List<String> getTaskSortPriorities() {
        return taskSortPriorities;
    }

    public boolean isMultiSort() {
        return multiSort;
    }

    public void setMultiSort(boolean multiSort) {
        this.multiSort = multiSort;
    }

    public String getSortMode() {
        if (multiSort){
            return "multi";
        }else
            return "single";
    }

    @Override
    public void reset(){
        super.reset();
        taskSortPriorities.clear();
        taskNameFilter = "";
        descripitionFilter = "";
        stateVar = "";
    }

    public void modeChanged(ValueChangeEvent event){
        taskSortPriorities.clear();
    }
}


