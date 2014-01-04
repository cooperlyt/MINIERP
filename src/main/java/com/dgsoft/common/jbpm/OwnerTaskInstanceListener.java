package com.dgsoft.common.jbpm;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/3/13
 * Time: 10:32 AM
 */

@Name("ownerTaskInstanceListener")
@Scope(ScopeType.SESSION)
@AutoCreate
@Install(precedence = Install.APPLICATION, dependencies = "org.jboss.seam.bpm.jbpm")
public class OwnerTaskInstanceListener extends TaskInstanceListener {

    public static final String PUSH_TASK_CHANGE_TOPIC = "pushTaskChange";

    public static final String PUSH_TASK_COME_TOPIC = "pushTaskCome";


//    private Jbpm jbpm;
//
//    private Actor actor;

    public OwnerTaskInstanceListener() {
        super();
//        actor = Actor.instance();
//        jbpm = Jbpm.instance();
    }


    @Override
    protected List<TaskInstance> queryTaskList() {

//        JbpmContext jbpmContext = jbpm.getJbpmConfiguration().createJbpmContext();
//        try {
//            return jbpmContext.getTaskList(actor.getId());
//        } finally {
//            jbpmContext.close();
//        }
        return (List<TaskInstance>) Component.getInstance("org.jboss.seam.bpm.taskInstanceList", true, true);
    }


    @Override
    protected void sendNewTaskMessage() {
        try {
            TopicKey topicKey = new TopicKey(PUSH_TASK_COME_TOPIC);
            TopicsContext topicsContext = TopicsContext.lookup();
            topicsContext.publish(topicKey, "message");
        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
    }


    @Override
    protected void sendTaskChangeMessage() {
        try {
            TopicKey topicKey = new TopicKey(PUSH_TASK_CHANGE_TOPIC);
            TopicsContext topicsContext = TopicsContext.lookup();
            topicsContext.publish(topicKey, "message");
        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
    }


}
