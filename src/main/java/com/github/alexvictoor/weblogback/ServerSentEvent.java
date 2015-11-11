package com.github.alexvictoor.weblogback;


import java.util.Arrays;
import java.util.Collection;

public class ServerSentEvent {

    private final static Collection<String> logLevels = Arrays.asList("DEBUG", "INFO", "WARN", "ERROR");

    private final String type;
    private final String data;


    public ServerSentEvent(String type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        String filteredMsg = data.replace("\r", "");
        String[] lines = filteredMsg.split("\n");

        StringBuilder builder = new StringBuilder();
        if (logLevels.contains(type)) {
            builder.append("event: ").append(type).append("\r\n");
        }
        for (String line : lines) {
            builder.append("data: ").append(line).append("\r\n");
        }
        builder.append("\n");

        return builder.toString();
    }
}