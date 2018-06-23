package io.cresco.library.plugin;

import io.cresco.library.core.Config;
import io.cresco.library.core.WatchDog;
import io.cresco.library.messaging.MsgEvent;
import io.cresco.library.messaging.RPC;
import io.cresco.library.utilities.CLogger;
import org.apache.commons.configuration.SubnodeConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Cresco plugin base
 * @author V.K. Cody Bumgardner
 * @author Caylin Hickey
 * @since 0.1.0
 */
public class CPlugin {
    /** Region of the plugin instance */
    protected String region;
    /** Agent of the plugin instance */
    protected String agent;
    /** PluginID of the plugin instance */
    protected String pluginID;

    /** Name of the plugin class */
    protected String name;
    /** Version of the plugin class */
    protected String version;

    /** Status of the plugin */
    protected boolean isActive;
    /** Configuration (INI) of the plugin */
    protected Config config;

    /** Executor of the plugin */
    protected CExecutor exec;
    /** Logger of the plugin */
    protected CLogger logger;
    /** Message queue from plugin to agent */
    protected BlockingQueue<MsgEvent> msgOutQueue;
    /** Remote procedural call class of the plugin */
    protected RPC rpc;
    /** WatchDog timer of the plugin */
    protected WatchDog watchDog;

    /**
     * Default constructor
     */

    public CPlugin() {
        this("unknown", "unknown");
    }

    /**
     * Parameterized constructor
     * @param name          Name of the plugin
     * @param version       Version of the plugin
     */
    public CPlugin(String name, String version) {
        setName(name);
        setVersion(version);
        setRegion("init");
        setAgent("init");
        setPluginID("init");
    }

    /**
     * Initialization method called when the plugin is loaded into the Cresco agent
     * @param msgOutQueue   Linked communication channel from plugin to agent
     * @param config        Subnodeconfiguration object of plugin settings
     * @param region        Region of the plugin
     * @param agent         Agent of the plugin
     * @param pluginID      Internal agent ID for the plugin
     * @return              Whether the initialization was successful
     */
    public boolean initialize(BlockingQueue<MsgEvent> msgOutQueue, SubnodeConfiguration config, String region, String agent, String pluginID) {
        setMsgOutQueue(msgOutQueue);
        setExecutor();
        setConfig(new Config(config));
        setRegion(region);
        setAgent(agent);
        setPluginID(pluginID);
        setLogger(new CLogger(this.msgOutQueue, this.region, this.agent, this.pluginID));
        setRPC(new RPC(this.msgOutQueue, this.region, this.agent, this.pluginID, logger));
        setWatchDog(new WatchDog(this.region, this.agent, this.pluginID, logger, this.config));
        try {
            start();
        } catch (Exception e) {
            if (logger != null)
                logger.error("Initialization failed. [Exception: {}]", e.getMessage());
            return false;
        }
        startWatchDog();
        setActive(true);
        return true;
    }

    public boolean initialize(String region, String agent, String pluginID) {
        setRegion(region);
        setAgent(agent);
        setPluginID(pluginID);
        setActive(true);
        return true;
    }

    /**
     * Method called before agent plugin initialization
     */
    public void preStart() { }

    /**
     * Main entry point for plugin implementation class
     */
    public void start() {}

    /**
     * Method called after plugin has been successfully initialized
     */
    public void postStart() { }

    /**
     * Method called before agent plugin shutdown
     */
    public void preShutdown() { }

    /**
     * Shutdown method called when the plugin is unloaded from the Cresco agent
     * @return              Whether cleanUp was completed successfully
     */
    public boolean shutdown() {
        stopWatchDog();
        setActive(false);
        try {
            cleanUp();
            return true;
        } catch (Exception e) {
            logger.error("Shutdown error encountered. [Exception: {}]", e.getMessage());
            return false;
        }
    }

    /**
     * Override this method for any last minute cleanup
     */
    protected void cleanUp() { }

    /**
     * Override this method to use your own CExecutor class for MsgEvent processing
     */
    protected void setExecutor() {
        this.exec = new SimpleExecutor(this);
    }

    /**
     * Submits a MsgEvent to the CExecutor
     * @param message       Message for the executor
     * @return              The MsgEvent returned by the Executor
     */
    public MsgEvent execute(MsgEvent message) {
        return exec.execute(message);
    }

    /**
     * Incoming messages from the agent to the plugin
     * @param message       Message for the plugin
     */
    public void msgIn(MsgEvent message) {
        if (message == null) return;
        new Thread(new MessageProcessor(message)).start();
    }

    /**
     * Sends messages to the Cresco agent
     * @param msg           MsgEvent object to send
     */
    public void sendMsgEvent(MsgEvent msg) {
        msgOutQueue.add(msg);
    }

    /**
     * Issues a remote procedure call to the agent
     * @param msg           MsgEvent object to issue
     * @return              The remote procedure call return MsgEvent
     */
    public MsgEvent sendRPC(MsgEvent msg) {
        msg.setParam("is_rpc", "true");
        return rpc.call(msg);
    }

    /**
     * Receives a remote procedure call from the agent
     * @param callId        Remote procedure call id
     * @param msg           Return message
     */
    public void receiveRPC(String callId, MsgEvent msg) {
        rpc.putReturnMessage(callId, msg);
    }

    /**
     * Starts the WatchDog timer
     */
    public void startWatchDog() {
        if (watchDog != null)
            watchDog.start();
    }

    /**
     * Updates the WatchDog message parameters
     */
    public void updateWatchDog() {
        if (watchDog != null)
            watchDog.update(region, agent, pluginID, logger, config);
    }

    /**
     * Restarts the WatchDog timer
     */
    public void restartWatchDog() {
        if (this.watchDog != null)
            this.watchDog.restart();
    }

    /**
     * Stops the WatchDog timer
     */
    public void stopWatchDog() {
        if (this.watchDog != null)
            this.watchDog.stop();
    }

    /**
     * Stops this running plugin after one (1) second delay
     */
    public void quit() {
        new Thread(new DelayedQuit(1000L)).start();
    }

    /**
     * Stops this running plugin immediately
     */
    public void quitNow() {
        new Thread(new DelayedQuit(0L)).start();
    }

    public String getName() {
        return name;
    }
    protected void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }
    protected void setVersion(String version) {
        this.version = version;
    }

    public String getRegion() {
        return region;
    }
    protected void setRegion(String region) {
        this.region = region;
    }

    public String getAgent() {
        return agent;
    }
    protected void setAgent(String agent) {
        this.agent = agent;
    }

    public String getPluginID() {
        return pluginID;
    }
    protected void setPluginID(String plugin) {
        this.pluginID = plugin;
    }

    public Config getConfig() {
        return config;
    }
    protected void setConfig(Config config) {
        this.config = config;
    }

    public CExecutor getExec() {
        return exec;
    }
    protected void setExec(CExecutor exec) {
        this.exec = exec;
    }

    public Boolean isActive() { return isActive; }
    protected void setActive(Boolean state) {
        this.isActive = state;
    }

    public CLogger getLogger() {
        return logger;
    }
    protected void setLogger(CLogger logger) {
        this.logger = logger;
    }

    public BlockingQueue<MsgEvent> getMsgOutQueue() {
        return msgOutQueue;
    }
    protected void setMsgOutQueue(BlockingQueue<MsgEvent> msgOutQueue) {
        this.msgOutQueue = msgOutQueue;
    }

    public RPC getRPC() {
        return rpc;
    }
    protected void setRPC(RPC rpc) {
        this.rpc = rpc;
    }

    public WatchDog getWatchDog() {
        return watchDog;
    }
    protected void setWatchDog(WatchDog watchDog) {
        this.watchDog = watchDog;
    }

    /**
     * Processing agent for incoming messages
     * @author V.K. Cody Bumgardner
     * @author Caylin Hickey
     * @since 0.1.0
     */
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
            logger = new CLogger(MessageProcessor.class, msgOutQueue, region, agent, pluginID);
        }

        /**
         * Processing method
         */
        @Override
        public void run() {
            try {
                MsgEvent retMsg = exec.execute(msg);
                if (retMsg != null && retMsg.getParams().keySet().contains("is_rpc")) {
                    retMsg.setReturn();
                    msgOutQueue.add(retMsg);
                }
            } catch (Exception e) {
                logger.error("Message Execution Exception: {}", e.getMessage());
            }
        }
    }

    /**
     * Method to request termination of running plugin
     * @author Caylin Hickey
     * @author V.K. Cody Bumgardner
     * @since 0.5.3
     */
    protected class DelayedQuit implements Runnable {
        private long wait_time = 1000L;

        public DelayedQuit() { }

        public DelayedQuit(long wait_time) {
            this.wait_time = wait_time;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // Ignore
            }
            Map<String, String> params = new HashMap<>();
            params.put("src_region", getRegion());
            params.put("src_agent", getAgent());
            params.put("src_plugin", getPluginID());
            params.put("dst_region", getRegion());
            params.put("dst_agent", getAgent());
            params.put("action", "pluginremove");
            params.put("io/cresco/library/plugin", getPluginID());
            sendMsgEvent(new MsgEvent(MsgEvent.Type.CONFIG, getRegion(),
                    getAgent(), getPluginID(), params));
        }
    }
}