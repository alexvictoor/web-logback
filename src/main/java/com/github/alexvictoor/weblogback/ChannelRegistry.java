package com.github.alexvictoor.weblogback;


import io.netty.channel.Channel;

public interface ChannelRegistry {

    void addChannel(Channel ch);

}
