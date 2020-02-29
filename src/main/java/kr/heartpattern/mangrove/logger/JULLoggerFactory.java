package kr.heartpattern.mangrove.logger;

import org.eclipse.aether.spi.log.Logger;
import org.eclipse.aether.spi.log.LoggerFactory;

public class JULLoggerFactory implements LoggerFactory {
    @Override
    public Logger getLogger(String name) {
        return new JULLogger(java.util.logging.Logger.getLogger(name));
    }
}
