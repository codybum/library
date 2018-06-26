package io.cresco.library.plugin;

import io.cresco.library.agent.AgentService;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.metrics.CrescoMeterRegistry;
import io.cresco.library.utilities.CLogger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import java.util.Map;

public class PluginBuilder {

    private AgentService agentService;
    private LogService logService;
    private Config config;
    private CrescoMeterRegistry crescoMeterRegistry;
    private String baseClassName;

    public PluginBuilder(String className, BundleContext context, Map<String,Object> configMap) {

        this.baseClassName = className.substring(0,className.lastIndexOf("."));

        //create config
        config = new Config(configMap);

        //metric registery
        crescoMeterRegistry = new CrescoMeterRegistry(getPluginID());

        /*
        t = Timer
                .builder("my.timer")
                .description("a description of what this timer does") // optional
                .tags("region", "test") // optional
                .register(crescoMeterRegistry);
        */

        //init agent services
        ServiceReference sr = context.getServiceReference(AgentService.class.getName());
        if(sr != null) {
            boolean assign = sr.isAssignableTo(context.getBundle(), AgentService.class.getName());

            if (assign) {
                agentService = (AgentService) context.getService(sr);
            } else {
                System.out.println("Could not assign AgentService!");
            }
        } else {
            System.out.println("Can't Find :" + AgentService.class.getName());
        }

        //init log service

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
    public void msgIn(MsgEvent msg) { agentService.getAgentState().msgIn(msg); }
    public void msgIn(String msg) { agentService.msgIn(getPluginID(), msg); }
    public CrescoMeterRegistry getCrescoMeterRegistry() { return crescoMeterRegistry; }

    public CLogger getLogger(String issuingClassName, CLogger.Level level) {
        return new CLogger(this,baseClassName,issuingClassName,level);
    }

}
