package com.dgsoft.common.jbpm;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 6/6/13
 * Time: 9:41 AM
 */
@Name("bpmTaskChangePublish")
@AutoCreate
@Scope(ScopeType.APPLICATION)
@Install(precedence = Install.APPLICATION, dependencies = "org.jboss.seam.bpm.jbpm")
@Synchronized
@Startup(depends = "org.jboss.seam.bpm.jbpm")
public class BpmTaskChangePublish {

    @Logger
    private Log log;

    private List<TaskInstanceListener> subscribers = new ArrayList<TaskInstanceListener>();

    private List<TaskInstanceListener> invalidSubscribers = new ArrayList<TaskInstanceListener>();

    public BpmTaskChangePublish(){
        super();
    }

    public synchronized void subscribe(TaskInstanceListener subscriber) {
        log.debug("subscribe = Publish:" + this + "|subscriber:" + subscriber);
        subscribers.add(subscriber);
    }

    public synchronized void onBusinessTaskChange() {
        log.debug("onBusinessTaskChange listener count:" + subscribers.size());
        invalidSubscribers.clear();
        for (TaskInstanceListener listener : subscribers) {
            try {
                log.debug("onBusinessTaskChange publish:" + listener);
                listener.checkTask();
            } catch (Exception e) {
                invalidSubscribers.add(listener);
                log.error("publish fail subscriber be remove:" + listener, e);
            }
        }
        for (TaskInstanceListener listener : invalidSubscribers) {
            if (subscribers.contains(listener)) {
                subscribers.remove(listener);
            }
        }
    }

    public synchronized void unSubscribe(TaskInstanceListener subscriber) {
        log.debug("unSubscribe = Publish:" + this + "|subscriber:" + subscriber);
        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
        } else {
            log.warn("unSubscribe fail! subscriber not in list");
        }
    }

}
