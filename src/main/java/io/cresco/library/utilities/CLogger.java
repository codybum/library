package io.cresco.library.utilities;


import io.cresco.library.plugin.PluginBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Cresco logger
 * @author V.K. Cody Bumgardner
 * @author Caylin Hickey
 * @since 0.1.0
 */
public class CLogger {

    public enum Level {

        None(-1), Error(0), Warn(1), Info(2), Debug(4), Trace(8);

        private final int level;
        Level(int level) { this.level = level; }
        public int getValue() { return level; }
        public boolean toShow(Level check) {
            return check.getValue() <= this.getValue();
        }
    }

    private Level level;
    private String issuingClassName;
    private String baseClassName;
    private PluginBuilder pluginBuilder;
    private Logger logService;
    //private LogService logService;
    private String source;

    public CLogger(PluginBuilder pluginBuilder, String baseClassName, String issuingClassName, Level level) {
        this.pluginBuilder = pluginBuilder;
        this.baseClassName = baseClassName;
        this.issuingClassName = issuingClassName.substring(baseClassName.length() +1, issuingClassName.length()) ;
        this.level = level;

        if(pluginBuilder.getPluginID() != null) {
            source = pluginBuilder.getPluginID();
        } else {
            source = "agent";
        }

        this.logService = Logger.getLogger(source);
        //logService = pluginBuilder.getLogService();

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

    public void log(String messageBody, Level level) {

        java.util.logging.Level l2 = null;

        //Error(1), Warn(2), Info(3), Debug(4), Trace(4);
        String levelString = level.name();

        switch (levelString) {
            case "Trace":  l2 = java.util.logging.Level.FINER;
                break;
            case "Debug":  l2 = java.util.logging.Level.FINE;
                break;
            case "Info":  l2 = java.util.logging.Level.INFO;
                break;
            case "Warn":  l2 = java.util.logging.Level.WARNING;
                break;
            case "Error":  l2 = java.util.logging.Level.SEVERE;
                break;
            default: l2 = java.util.logging.Level.SEVERE;
                break;
        }

        String logMessage = "[" + source + ": " + baseClassName + "]";
            logMessage = logMessage + "[" + formatClassName(issuingClassName) + "]";
        logMessage = logMessage + " " + messageBody;

        logService.log(l2,logMessage);

    }

    private String formatClassName(String className) {
        String newName = "";
        int lastIndex = 0;
        int nextIndex = className.indexOf(".", lastIndex + 1);
        while (nextIndex != -1) {
            newName = newName + className.substring(lastIndex, lastIndex + 1) + ".";
            lastIndex = nextIndex + 1;
            nextIndex = className.indexOf(".", lastIndex + 1);
        }
        return newName + className.substring(lastIndex);
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