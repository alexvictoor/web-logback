package com.github.alexvictoor.weblogback;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class BrowserConsoleAppender<E> extends OutputStreamAppender<E> {

    private String host;
    private int port = 8765;
    private int buffer = 1;
    private boolean active = true;
    private WebServer webServer;
    private ChannelOutputStream stream;

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    @Override
    protected void writeOut(E event) throws IOException {
        if (event instanceof ILoggingEvent) {
            ILoggingEvent loggingEvent = (ILoggingEvent) event;
            //
            // dirty hack - not easy to work with logback appenders
            // since the 'encoder' works with an outputstream set at init
            //
            stream.setCurrentLevel(loggingEvent.getLevel());
        }
        super.writeOut(event);
    }

    @Override
    public void start() {
        if (!active) {
            return;
        }

        if ("".equals(host) || host==null) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                host = "localhost";
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                waitForSlf4jInitialization();
                webServer = new WebServer(host, port, buffer);
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
