package com.github.alexvictoor.weblogback;

import ch.qos.logback.classic.Level;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelOutputStream extends OutputStream implements ChannelRegistry {

    private final ChannelGroup channels;
    private final List<ServerSentEvent> buffer;
    private Level currentLevel;
    private int bufferSize;

    public ChannelOutputStream(ChannelGroup channels, int bufferSize) {
        this.channels = channels;
        this.bufferSize = bufferSize;
        this.currentLevel = Level.OFF;
        this.buffer = new ArrayList<>();
    }

    @Override
    public void addChannel(Channel ch) {
        channels.add(ch);
        synchronized (buffer) {
            for (ServerSentEvent event : buffer) {
                ByteBuf buffer = Unpooled.copiedBuffer(event.toString(), Charset.defaultCharset());
                HttpContent content = new DefaultHttpContent(buffer);
                ch.write(content);
            }
            ch.flush();
        }
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
        synchronized (buffer) {
            buffer.add(event);
            if (buffer.size() > bufferSize) {
                buffer.remove(0);
            }
        }
        ByteBuf buffer = Unpooled.copiedBuffer(event.toString(), Charset.defaultCharset());
        HttpContent content = new DefaultHttpContent(buffer);
        channels.write(content);
    }

    @Override
    public void flush() throws IOException {
        channels.flush();
    }


    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }
}