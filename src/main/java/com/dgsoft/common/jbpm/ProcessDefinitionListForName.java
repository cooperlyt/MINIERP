package com.dgsoft.common.jbpm;

import org.jboss.seam.annotations.*;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.jboss.seam.ScopeType.APPLICATION;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/30/13
 * Time: 9:53 AM
 */
@Name("processDefinitionListForName")
@Scope(APPLICATION)
@Install(dependencies = "org.jboss.seam.bpm.jbpm")
public class ProcessDefinitionListForName {

    public class ProcessDefinitionGroup {

        private String name;

        private String description;

        public ProcessDefinitionGroup(String name, String description) {
            this.name = name;
            this.description = description;
        }

        private List<ProcessDefinition> processDefinitions;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<ProcessDefinition> getProcessDefinitions() {
            return processDefinitions;
        }

        public void setProcessDefinitions(List<ProcessDefinition> processDefinitions) {
            this.processDefinitions = processDefinitions;
        }

        public void sort() {
            if (processDefinitions != null) {
                Collections.sort(processDefinitions, new Comparator<ProcessDefinition>() {
                    @Override
                    public int compare(ProcessDefinition o1, ProcessDefinition o2) {
                        return new Integer(o2.getVersion()).compareTo(o1.getVersion());
                    }
                });
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ProcessDefinitionGroup)) {
                return false;
            }
            return ((ProcessDefinitionGroup) obj).getName().equals(name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }


    @Unwrap
    @Transactional
    public List<ProcessDefinitionGroup> getProcessDefinitionList() {
        List<ProcessDefinitionGroup> result = new ArrayList<ProcessDefinitionGroup>();
        List<ProcessDefinition> processDefinitions = ManagedJbpmContext.instance().getGraphSession().findAllProcessDefinitions();
        for (ProcessDefinition processDefinition : processDefinitions) {

            ProcessDefinitionGroup group = new ProcessDefinitionGroup(processDefinition.getName(), processDefinition.getDescription());
            int index = result.indexOf(group);
            if (index < 0) {
                group.setProcessDefinitions(new ArrayList<ProcessDefinition>());
                result.add(group);
            } else {
                group = result.get(index);
            }
            group.getProcessDefinitions().add(processDefinition);
        }
        for (ProcessDefinitionGroup processDefinitionGroup : result)
            processDefinitionGroup.sort();
        return result;
    }

}
