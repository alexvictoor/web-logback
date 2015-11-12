package com.github.alexvictoor.weblogback;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpContent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChannelOutputStreamTest {

    @Mock
    private ChannelGroup channels;

    @Test
    public void should_write_content_to_channel() throws IOException {
        // given
        ChannelOutputStream stream = new ChannelOutputStream(channels, 1);
        // when
        stream.write("hello".getBytes(),0,5);
        // then
        ArgumentCaptor<HttpContent> captor = ArgumentCaptor.forClass(HttpContent.class);
        verify(channels).write(captor.capture());
        String output = new String(captor.getValue().content().array());
        assertThat(output).startsWith("data: hello");
    }

    @Test
    public void should_add_channel_to_group() throws IOException {
        // given
        ChannelOutputStream stream = new ChannelOutputStream(channels, 1);
        Channel ch = mock(Channel.class);
        // when
        stream.addChannel(ch);
        // then
        verify(channels).add(ch);
    }

    @Test
    public void should_replay_last_event_adding_a_channel() throws IOException {
        // given
        ChannelOutputStream stream = new ChannelOutputStream(channels, 1);
        Channel ch = mock(Channel.class);
        stream.write("hello".getBytes(),0,5);
        // when
        stream.addChannel(ch);
        // then
        verify(ch).write(any());
    }

    @Test
    public void should_replay_only_last_events_adding_a_channel() throws IOException {
        // given
        ChannelOutputStream stream = new ChannelOutputStream(channels, 2);
        Channel ch = mock(Channel.class);
        stream.write("hello".getBytes(),0,5);
        stream.write("good".getBytes(),0,4);
        stream.write("bye".getBytes(),0,3);
        // when
        stream.addChannel(ch);
        // then
        verify(ch, times(2)).write(any());
    }

}