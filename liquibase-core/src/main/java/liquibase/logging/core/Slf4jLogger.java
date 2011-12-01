package liquibase.logging.core;

import org.slf4j.LoggerFactory;

import liquibase.logging.LogLevel;
import liquibase.logging.Logger;
public class Slf4jLogger implements Logger {

    private static final org.slf4j.Logger DEFAULT_LOGGER = LoggerFactory.getLogger(Slf4jLogger.class.getName() +
            ".DEFAULT_LOGGER");

    private org.slf4j.Logger logger = DEFAULT_LOGGER;

    public int getPriority() {
        return 2;
    }

    public void setName(String name) {
        logger = null == name ? DEFAULT_LOGGER : LoggerFactory.getLogger(name);
    }

    public void setLogLevel(String level) {
    }

    public void setLogLevel(LogLevel level) {
    }

    public void setLogLevel(String logLevel, String logFile) {
    }

    public LogLevel getLogLevel() {
        return null;
    }

    public void severe(String message) {
        logger.error(message);
    }

    public void severe(String message, Throwable e) {
        logger.error(message, e);
    }

    public void warning(String message) {
        logger.warn(message);
    }

    public void warning(String message, Throwable e) {
        logger.warn(message, e);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, Throwable e) {
        logger.info(message, e);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void debug(String message, Throwable e) {
        logger.debug(message, e);
    }

}
