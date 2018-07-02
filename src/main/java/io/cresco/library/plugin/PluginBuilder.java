package io.cresco.library.plugin;

import io.cresco.library.agent.AgentService;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.metrics.CrescoMeterRegistry;
import io.cresco.library.utilities.CLogger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.util.Map;

public class PluginBuilder {

    private AgentService agentService;
    private LogService logService;
    private Config config;
    private CrescoMeterRegistry crescoMeterRegistry;
    private String baseClassName;
    private Executor executor;
    private boolean isActive;

    public PluginBuilder(String className, BundleContext context, Map<String,Object> configMap) {
        this(null,className,context,configMap);
    }

    public PluginBuilder(AgentService agentService,String className, BundleContext context, Map<String,Object> configMap) {


        if(agentService == null) {
            //init agent services
            ServiceReference sr = context.getServiceReference(AgentService.class.getName());
            if (sr != null) {
                boolean assign = sr.isAssignableTo(context.getBundle(), AgentService.class.getName());

                if (assign) {
                    this.agentService = (AgentService) context.getService(sr);
                } else {
                    System.out.println("Could not assign AgentService!");
                }
            } else {
                System.out.println("Can't Find :" + AgentService.class.getName());
            }
        } else {
            this.agentService = agentService;
        }

        this.baseClassName = className.substring(0,className.lastIndexOf("."));

        //create config
        config = new Config(configMap);

        //metric registery
        crescoMeterRegistry = new CrescoMeterRegistry(getPluginID());


        ServiceReference ref = context.getServiceReference(LogService.class.getName());
        if (ref != null)
        {
            boolean assign = ref.isAssignableTo(context.getBundle(), LogService.class.getName());
            if(assign) {
                logService = (LogService) context.getService(ref);
            } else {
                System.out.println("Could not assign LogService!");
            }
        } else {
            System.out.println("Can't Find :" + LogService.class.getName());
        }



    }

    public AgentService getAgentService() {
        return agentService;
    }

    public LogService getLogService() { return logService; }
    public Config getConfig() { return config;}
    public void setConfig(Map<String,Object> configMap) {
        this.config = new Config(configMap);
    }
    public String getAgent() { return agentService.getAgentState().getAgent(); }
    public String getRegion() { return agentService.getAgentState().getRegion(); }
    public String getPluginID() { return config.getStringParam("pluginID"); }

    public void msgIn(MsgEvent message) {
        if ((message == null) || (executor == null)) return;
        new Thread(new MessageProcessor(message)).start();
    }

    public void msgOut(MsgEvent msg) { agentService.msgOut(getPluginID(), msg); }
    public CrescoMeterRegistry getCrescoMeterRegistry() { return crescoMeterRegistry; }

    public CLogger getLogger(String issuingClassName, CLogger.Level level) {
        return new CLogger(this,baseClassName,issuingClassName,level);
    }
    public boolean isIPv6() { return false; }
    public MsgEvent sendRPC(MsgEvent msg) {
        //msg.setParam("is_rpc", "true");
        //return this.rpc.call(msg);
        return msg;
    }

    public MsgEvent getGlobalControllerMsgEvent(MsgEvent.Type type) {
        return getMsgEvent(type, getRegion(),getAgent(), null,true,true);
    }

    public MsgEvent getGlobalAgentMsgEvent(MsgEvent.Type type, String dstRegion, String dstAgent) {
        return getMsgEvent(type, dstRegion,dstAgent, null,false,false);
    }

    public MsgEvent getGlobalPluginMsgEvent(MsgEvent.Type type, String dstRegion, String dstAgent, String dstPlugin) {
        return getMsgEvent(type, dstRegion,dstAgent, dstPlugin,false,false);
    }

    public MsgEvent getRegionalControllerMsgEvent(MsgEvent.Type type) {
        return getMsgEvent(type,getRegion(), getAgent(), null,true,false);
    }

    public MsgEvent getRegionalAgentMsgEvent(MsgEvent.Type type, String dstAgent) {
        return getMsgEvent(type,getRegion(), dstAgent, null,false,false);
    }

    public MsgEvent getRegionalPluginMsgEvent(MsgEvent.Type type, String dstAgent, String dstPlugin) {
        return getMsgEvent(type,getRegion(), dstAgent, dstPlugin,false,false);
    }

    public MsgEvent getAgentMsgEvent(MsgEvent.Type type) {
        return getMsgEvent(type,getRegion(), getAgent(), null,false,false);
    }

    public MsgEvent getPluginMsgEvent(MsgEvent.Type type, String dstPlugin) {
        return getMsgEvent(type,getRegion(),getAgent(),dstPlugin,false,false);
    }

    private MsgEvent getMsgEvent(MsgEvent.Type type, String dstRegion, String dstAgent, String dstPlugin, boolean isRegional, boolean isGlobal) {

        MsgEvent msg = null;
        try {
            msg = new MsgEvent(type, getRegion(),getAgent(),getPluginID(),dstRegion,dstAgent,dstPlugin,isRegional ,isGlobal);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return msg;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public boolean isActive() { return this.isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    public void sendMsgEvent(MsgEvent msg) {
        System.out.println("PLUGIN SEND MSGEVENT !");
    }

    protected class MessageProcessor implements Runnable {
        /** Incoming MsgEvent object */
        private MsgEvent msg;
        /** MessageProcessor Cresco Logger instance */
        private final CLogger logger;

        /**
         * Constructor
         * @param msg   MsgEvent to process
         */
        MessageProcessor(MsgEvent msg) {
            this.msg = msg;
            logger = getLogger(MessageProcessor.class.getName(), CLogger.Level.Info);
        }

        /**
         * Processing method
         */
        @Override
        public void run() {
            try {

                if(msg.dstIsLocal(getRegion(),getAgent(),getPluginID())) {

                    MsgEvent retMsg = null;


                    switch (msg.getMsgType().toString().toUpperCase()) {
                        case "CONFIG":
                            retMsg = executor.executeCONFIG(msg);
                            break;
                        case "DISCOVER":
                            retMsg = executor.executeDISCOVER(msg);
                            break;
                        case "ERROR":
                            retMsg = executor.executeERROR(msg);
                            break;
                        case "EXEC":
                            retMsg = executor.executeEXEC(msg);
                            break;
                        case "INFO":
                            retMsg = executor.executeINFO(msg);
                            break;
                        case "WATCHDOG":
                            retMsg = executor.executeWATCHDOG(msg);
                            break;
                        case "KPI":
                            retMsg = executor.executeKPI(msg);
                            break;

                        default:
                            logger.error("UNKNOWN MESSAGE TYPE! " + msg.getParams());
                            break;
                    }


                    if (retMsg != null && retMsg.getParams().keySet().contains("is_rpc")) {
                        retMsg.setReturn();
                        //msgOutQueue.add(retMsg);
                        msgIn(retMsg);
                    }
                }

            } catch (Exception e) {
                logger.error("Message Execution Exception: {}", e.getMessage());
            }
        }
    }


}
