package io.cresco.library.agent;

import io.cresco.library.messaging.MsgEvent;

import java.io.Serializable;
import java.util.Date;

public class AgentState implements Serializable {
    /**
     * Only needed for ECF generic transport
     */
    private static final long serialVersionUID = 5117254163782139591L;

    String agentName;
    String regionName;

    public String getRegion() { return regionName;};
    public String getAgent() { return agentName;}


    String id;
    String title;
    String description;
    Date dueDate;
    boolean finished;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getDueDate() {
        return dueDate;
    }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void sendMessage(String message) {
        System.out.println("Message From Agent:" + id + " remote_id:" + message);
    }

    public void msgIn(String msg) {

        //    logger.trace("msgIn : " + msg.getParams().toString());
        //    msgInProcessQueue.submit(new MsgRoute(this, msg));
    }
    public void msgIn(MsgEvent msg) {
        //    logger.trace("msgIn : " + msg.getParams().toString());
        //    msgInProcessQueue.submit(new MsgRoute(this, msg));
        System.out.println("MESSAGE IN AGENT!!! " + msg.getParams().toString());

    }

}