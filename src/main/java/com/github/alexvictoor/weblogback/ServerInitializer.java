package com.github.alexvictoor.weblogback;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelGroup allChannels;
    private final String host;
    private final int port;

    public ServerInitializer(ChannelGroup allChannels, String host, int port) {
        this.allChannels = allChannels;
        this.host = host;
        this.port = port;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpResponseEncoder());
        p.addLast(new ServerSentEventHandler(allChannels, host, port));
    }
}
