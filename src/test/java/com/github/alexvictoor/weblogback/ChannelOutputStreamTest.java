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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ChannelOutputStreamTest {

    @Mock
    private ChannelGroup channel;

    @Test
    public void should_write_content_to_channel() throws IOException {
        // given
        ChannelOutputStream stream = new ChannelOutputStream(channel);
        // when
        stream.write("hello".getBytes(),0,5);
        // then
        ArgumentCaptor<HttpContent> captor = ArgumentCaptor.forClass(HttpContent.class);
        verify(channel).write(captor.capture());
        String output = new String(captor.getValue().content().array());
        assertThat(output).startsWith("data: hello");
    }

}