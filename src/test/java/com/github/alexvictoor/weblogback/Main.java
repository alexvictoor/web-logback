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
                logger.info("----------------------------------------------------");
                logger.info("With web-logback you can check out your server logs");
                logger.info("in the console of your browser.");
                logger.info("If you are doing web development with a Java backend");
                logger.info("that might be handy");
                logger.info("----------------------------------------------------");
            }
        }, 0, 50, TimeUnit.SECONDS);
    }

}
