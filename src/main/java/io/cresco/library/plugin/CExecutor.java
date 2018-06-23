package io.cresco.library.plugin;

import io.cresco.library.messaging.MsgEvent;

/**
 * Cresco executor base
 * @author V.K. Cody Bumgardner
 * @author Caylin Hickey
 * @since 0.1.0
 */
public abstract class CExecutor {
    /** Plugin instance */
    protected CPlugin plugin;

    /**
     * Constructor
     * @param plugin        Plugin instance for this Cresco executor
     */
    public CExecutor(CPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Process incoming message
     * @param incoming      Incoming message
     * @return              Processed message
     */
    public MsgEvent execute(MsgEvent incoming) {
        String callId = incoming.getParam("callId-" + plugin.getRegion() + "-" +
                plugin.getAgent() + "-" + plugin.getPluginID());
        if (callId != null) {
            plugin.receiveRPC(callId, incoming);
            return null;
        }
        if (incoming.getParam("dst_region").equals(plugin.getRegion()) &&
                incoming.getParam("dst_agent").equals(plugin.getAgent()) &&
                incoming.getParam("dst_plugin").equals(plugin.getPluginID())) {
            if (incoming.getMsgType().equals(MsgEvent.Type.CONFIG)) {
                incoming = processConfig(incoming);
            } else if (incoming.getMsgType().equals(MsgEvent.Type.DISCOVER)) {
                incoming = processDiscover(incoming);
            } else if (incoming.getMsgType().equals(MsgEvent.Type.ERROR)) {
                incoming = processConfig(incoming);
            } else if (incoming.getMsgType().equals(MsgEvent.Type.EXEC)) {
                incoming = processExec(incoming);
            } else if (incoming.getMsgType().equals(MsgEvent.Type.INFO)) {
                incoming = processInfo(incoming);
            } else if (incoming.getMsgType().equals(MsgEvent.Type.WATCHDOG)) {
                incoming = processWatchDog(incoming);
            } else if (incoming.getMsgType().equals(MsgEvent.Type.KPI)) {
                incoming = processKPI(incoming);
            } else {
                incoming.setMsgBody("Message type [" + incoming.getMsgType().name() + "] unsupported by plugin [" +
                        plugin.getName() + ":" + plugin.getVersion() + "]");
            }
        }
        return incoming;
    }

    /**
     * Override to process Config messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processConfig(MsgEvent incoming) {
        return incoming;
    }

    /**
     * Override to process Discovier messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processDiscover(MsgEvent incoming) {
        return incoming;
    }

    /**
     * Override to process Error messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processError(MsgEvent incoming) {
        return incoming;
    }

    /**
     * Override to process Exec messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processExec(MsgEvent incoming) {
        return incoming;
    }

    /**
     * Override to process Info messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processInfo(MsgEvent incoming) {
        return incoming;
    }

    /**
     * Override to process WatchDog messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processWatchDog(MsgEvent incoming) {
        return incoming;
    }

    /**
     * Override to process KPI messages
     * @param incoming      Message to process
     * @return              Processed message
     */
    public MsgEvent processKPI(MsgEvent incoming) {
        return incoming;
    }
}
