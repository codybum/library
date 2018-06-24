package io.cresco.library.utilities;


import io.cresco.library.messaging.MsgEvent;

import java.util.concurrent.BlockingQueue;

/**
 * Cresco logger
 * @author V.K. Cody Bumgardner
 * @author Caylin Hickey
 * @since 0.1.0
 */
public class CLoggerBak {
    public enum Level {
        None(-1), Error(0), Warn(1), Info(2), Debug(4), Trace(8);
        private final int level;
        Level(int level) { this.level = level; }
        public int getValue() { return level; }
        public boolean toShow(Level check) {
            return check.getValue() <= this.getValue();
        }
    }
    private String region;
    private String agent;
    private String plugin;
    private Level level;
    private BlockingQueue<MsgEvent> msgOutQueue;
    private Class issuingClass;


    public CLoggerBak(BlockingQueue<MsgEvent> msgOutQueue, String region, String agent, String plugin) {
        this(msgOutQueue, region, agent, plugin, Level.Info);
    }

    public CLoggerBak(BlockingQueue<MsgEvent> msgOutQueue, String region, String agent, String plugin, Level level) {
        this.region = region;
        this.agent = agent;
        this.plugin = plugin;
        this.level = level;
        this.msgOutQueue = msgOutQueue;
    }

    public CLoggerBak(Class issuingClass, BlockingQueue<MsgEvent> msgOutQueue, String region, String agent, String plugin) {
        this(msgOutQueue, region, agent, plugin);
        this.issuingClass = issuingClass;
    }

    public CLoggerBak(Class issuingClass, BlockingQueue<MsgEvent> msgOutQueue, String region, String agent, String plugin, Level level) {
        this(msgOutQueue, region, agent, plugin, level);
        this.issuingClass = issuingClass;
    }

    public void error(String logMessage) {
        if (!level.toShow(Level.Error)) return;
        log(logMessage, Level.Error);
    }

    public void error(String logMessage, Object ... params) {
        if (!level.toShow(Level.Error)) return;
        error(replaceBrackets(logMessage, params));
    }

    public void warn(String logMessage) {
        if (!level.toShow(Level.Warn)) return;
        log(logMessage, Level.Warn);
    }

    public void warn(String logMessage, Object ... params) {
        if (!level.toShow(Level.Warn)) return;
        warn(replaceBrackets(logMessage, params));
    }

    public void info(String logMessage) {
        if (!level.toShow(Level.Info)) return;
        log(logMessage, Level.Info);
    }

    public void info(String logMessage, Object ... params) {
        if (!level.toShow(Level.Info)) return;
        info(replaceBrackets(logMessage, params));
    }

    public void debug(String logMessage) {
        if (!level.toShow(Level.Debug)) return;
        log(logMessage, Level.Debug);
    }

    public void debug(String logMessage, Object ... params) {
        if (!level.toShow(Level.Debug)) return;
        debug(replaceBrackets(logMessage, params));
    }

    public void trace(String logMessage) {
        if (!level.toShow(Level.Trace)) return;
        log(logMessage, Level.Trace);
    }

    public void trace(String logMessage, Object ... params) {
        if (!level.toShow(Level.Trace)) return;
        trace(replaceBrackets(logMessage, params));
    }

    public void log(String logMessage, Level level) {

        /*
        MsgEvent toSend = new MsgEvent(MsgEvent.Type.LOG, region, null, null, logMessage);
        toSend.setParam("src_region", region);
        if (agent != null) {
            toSend.setParam("src_agent", agent);
            if (plugin != null)
                toSend.setParam("src_plugin", plugin);
        }
        if (issuingClass != null) {
            toSend.setParam("class", issuingClass.getSimpleName());
            toSend.setParam("full_class", issuingClass.getCanonicalName());
        }
        toSend.setParam("ts", String.valueOf(new Date().getTime()));
        toSend.setParam("dst_region", region);
        toSend.setParam("log_level", level.name());
        log(toSend);
        */
    }

    public void log(MsgEvent logMessage) {
        //msgOutQueue.offer(logMessage);
    }

    public Level getLogLevel() {
        return level;
    }

    public void setLogLevel(Level level) {
        this.level = level;
    }

    private String replaceBrackets(String logMessage, Object ... params) {
        int replaced = 0;
        while (logMessage.contains("{}") && replaced < params.length) {
            logMessage = logMessage.replaceFirst("\\{\\}", String.valueOf(params[replaced]));
            replaced++;
        }
        return logMessage;
    }
}