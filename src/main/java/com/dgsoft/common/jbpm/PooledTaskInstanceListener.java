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
 * Time: 10:31 AM
 */
@Name("pooledTaskInstanceListener")
@Scope(ScopeType.SESSION)
@AutoCreate
@Install(precedence = Install.APPLICATION, dependencies = "org.jboss.seam.bpm.jbpm")
public class PooledTaskInstanceListener extends TaskInstanceListener {

    public static final String PUSH_POOLEDTASK_CHANGE_TOPIC = "pushPooledTaskChange";

    public static final String PUSH_POOLEDTASK_COME_TOPIC ="pushPooledTaskCome";


//    private Actor actor;
//    private Jbpm jbpm;

    public PooledTaskInstanceListener() {
        super();
//        actor = Actor.instance();
//        jbpm = Jbpm.instance();
    }

    @Override
    protected List<TaskInstance> queryTaskList() {


//        String actorId = actor.getId();
//        if (actorId == null) return null;
//        ArrayList groupIds = new ArrayList(actor.getGroupActorIds());
//        groupIds.add(actorId);
//        JbpmContext jbpmContext = jbpm.getJbpmConfiguration().createJbpmContext();
//        try {
//            return jbpmContext.getGroupTaskList(groupIds);
//        } finally {
//            jbpmContext.close();
//        }
        return (List<TaskInstance>) Component.getInstance("org.jboss.seam.bpm.pooledTaskInstanceList", true, true);
    }

    @Override
    protected void sendNewTaskMessage() {
        try {
            TopicKey topicKey = new TopicKey(PUSH_POOLEDTASK_COME_TOPIC);
            TopicsContext topicsContext = TopicsContext.lookup();
            topicsContext.publish(topicKey, "message");
        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
    }


    @Override
    protected void sendTaskChangeMessage() {
        try {
            TopicKey topicKey = new TopicKey(PUSH_POOLEDTASK_CHANGE_TOPIC);
            TopicsContext topicsContext = TopicsContext.lookup();
            topicsContext.publish(topicKey, "message");
        } catch (Exception e) {
            log.error("sendMessage error", e);
        }
    }


}
