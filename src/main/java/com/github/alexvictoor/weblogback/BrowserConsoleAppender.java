package com.github.alexvictoor.weblogback;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;

public class BrowserConsoleAppender<E> extends OutputStreamAppender<E> {

    private int port = 8765;
    private boolean active = true;
    private WebServer webServer;
    private ChannelOutputStream stream;

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    protected void writeOut(E event) throws IOException {
        if (event instanceof ILoggingEvent) {
            ILoggingEvent loggingEvent = (ILoggingEvent) event;
            stream.setCurrentLevel(loggingEvent.getLevel());
        }
        super.writeOut(event);
    }

    @Override
    public void start() {
        if (!active) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                waitForSlf4jInitialization();
                webServer = new WebServer(port);
                stream = webServer.start();
                setOutputStream(stream);
                BrowserConsoleAppender.super.start();
            }
        }).start();
        super.start();
    }

    /**
     * Dirty hack but no obvious other way to do it
     */
    private void waitForSlf4jInitialization() {
        try {
            Integer lock = new Integer(0);
            synchronized (lock) {
                while (isSlf4jUninitialized()) {
                    lock.wait(100);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSlf4jUninitialized() {
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        return factory instanceof SubstituteLoggerFactory;
    }

    @Override
    public void stop() {
        if (!active) {
            return;
        }
        webServer.stop();
        super.stop();
    }
}
