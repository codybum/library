package io.cresco.library.agent;

import io.cresco.library.messaging.MsgEvent;

import java.io.Serializable;
import java.util.Date;

public class AgentState implements Serializable {
    /**
     * Only needed for ECF generic transport
     */
    private static final long serialVersionUID = 5117254163782139591L;


    public String getRegion() { return agentStateEngine.getRegion();};
    public String getAgent() { return agentStateEngine.getAgent();}

    //public String getRegion() { return "agent";};
    //public String getAgent() { return "region";}


    String id;
    String title;
    String description;
    Date dueDate;
    boolean finished;


    private AgentStateEngine agentStateEngine;

    public AgentState(AgentStateEngine agentStateEngine) {
        this.agentStateEngine = agentStateEngine;
    }

    /*
    public void setAgentStateEngine(AgentStateEngine agentStateEngine) {
        this.agentStateEngine = agentStateEngine;
    }
    */

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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