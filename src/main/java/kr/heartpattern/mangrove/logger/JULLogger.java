package kr.heartpattern.mangrove.logger;

import org.eclipse.aether.spi.log.Logger;

import java.util.logging.Level;

public class JULLogger implements Logger {
    private java.util.logging.Logger logger;

    public JULLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }


    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        logger.log(Level.FINEST, msg);
    }

    @Override
    public void debug(String msg, Throwable error) {
        logger.log(Level.FINEST, msg, error);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    @Override
    public void warn(String msg, Throwable error) {
        logger.log(Level.WARNING, msg, error);
    }
}
