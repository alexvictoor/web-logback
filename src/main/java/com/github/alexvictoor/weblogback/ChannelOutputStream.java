package com.github.alexvictoor.weblogback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.net.ssl.SSL;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChannelOutputStream extends OutputStream {

    private final ChannelGroup channel;
    private Level currentLevel;

    public ChannelOutputStream(ChannelGroup channel) {
        this.channel = channel;
        this.currentLevel = Level.OFF;
    }

    @Override
    public void write(int b) throws IOException {
        // should not be called
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        String msg = new String(data, off, len);
        String level = currentLevel.toString();
        ServerSentEvent event = new ServerSentEvent(level, msg);
        ByteBuf buffer = Unpooled.copiedBuffer(event.toString(), Charset.defaultCharset());
        HttpContent content = new DefaultHttpContent(buffer);
        channel.write(content);
    }

    @Override
    public void flush() throws IOException {
        channel.flush();
    }

    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }
}