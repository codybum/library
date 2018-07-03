package io.cresco.library.messaging;



import io.cresco.library.plugin.PluginBuilder;
import io.cresco.library.utilities.CLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cresco remote procedure call helper
 * @author V.K. Cody Bumgardner
 * @author Caylin Hickey
 * @since 0.1.0
 */
public class RPC {
    /** Time between checks for RPC return message (in milliseconds) */
    private static final int CHECK_INTERVAL = 100;
    /** Maximum iterations to check for RPC return message */
    private static final int MAX_INTERVALS = 300;
    /** Cresco logger */
    private CLogger logger;
    /** Communication channel */
    private Map<String, MsgEvent> rpcMap = new ConcurrentHashMap<>();


    private PluginBuilder plugin;
    /**
     * Constructor
     * @param plugin        PluginBuilder
     */
    public RPC(PluginBuilder plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger(RPC.class.getName(),CLogger.Level.Info);
    }


    /**
     * Issues a remote procedure call
     * @param msg           Message to send
     * @return              The return message, null if no return is received
     */
    public MsgEvent call(MsgEvent msg) {
        try {
            String callId = java.util.UUID.randomUUID().toString();
            msg.setParam("callId-" + plugin.getRegion() + "-" + plugin.getAgent() + "-" + plugin.getPluginID(), callId);
            plugin.msgOut(msg);

            int count = 0;
            while (count++ < MAX_INTERVALS) {
                if (rpcMap.containsKey(callId)) {
                    MsgEvent callBack;
                    callBack = rpcMap.get(callId);
                    rpcMap.remove(callId);
                    return callBack;
                }
                Thread.sleep(CHECK_INTERVAL);
            }
        } catch (Exception ex) {
            logger.error("call {}", ex.getMessage());
        }
        return null;
    }

    /**
     * Places the return message for retrieval
     * @param callId            ID of the remote-procedural call
     * @param returnMessage     The return message
     */
    public void putReturnMessage(String callId, MsgEvent returnMessage) {
        rpcMap.put(callId, returnMessage);
    }

}
