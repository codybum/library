package io.cresco.library.plugin;

import io.cresco.library.agent.Agent;
import io.cresco.library.agent.AgentService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class PluginBuilder {

    private AgentService agentService;

    public PluginBuilder(BundleContext context) {

        ServiceReference sr = context.getServiceReference(AgentService.class.getName());

        if(sr != null) {
            boolean assign = sr.isAssignableTo(context.getBundle(), AgentService.class.getName());

            if (assign) {
                agentService = (AgentService) context.getService(sr);
                //System.out.println("Agentid: " + agentService.getAgent().getId());
                //agentService.getAgent().sendMessage((String)map.get("pluginID"));
            }
        } else {
            System.out.println("Can't Find :" + AgentService.class.getName());
        }

    }

    public AgentService getAgentService() {
        return agentService;
    }

}
