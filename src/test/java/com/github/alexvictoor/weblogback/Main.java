package com.github.alexvictoor.weblogback;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        System.setProperty("logback.configurationFile", "logback-web.xml");
        final Logger logger = LoggerFactory.getLogger(Main.class);
        /*for (int i=0; i<10; i++) {
            logger.info("Hello!");
        }
        try {
            System.in.read();
            logger.info("Hello2!");
            logger.info("He\"); alert(\"llo2");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("Hello");
            }
        }, 0, 50, TimeUnit.SECONDS);
    }

}
