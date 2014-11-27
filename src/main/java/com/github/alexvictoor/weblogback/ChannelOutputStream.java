package com.github.alexvictoor.weblogback;

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

    public ChannelOutputStream(ChannelGroup channel) {
        this.channel = channel;
    }

    @Override
    public void write(int b) throws IOException {
        // should not be called
    }

    @Override
    public void write(byte[] data, int off, int len) throws IOException {
        //data = "coucou".getBytes();
        String msg = new String(data, off, len);
        String filteredMsg = msg.replace("\r", "");
        String[] lines = filteredMsg.split("\n");
        for (String line : lines) {
            ByteBuf buffer = Unpooled.copiedBuffer("data: " + line + "\n\n", Charset.defaultCharset());
            HttpContent content = new DefaultHttpContent(buffer);
            channel.write(content);
        }
        /*List<byte[]> lines = splitLines(Arrays.copyOfRange(data, off, len));
        for (byte[] line : lines) {

            ByteBuf buffer = Unpooled.copiedBuffer("data: ", Charset.defaultCharset());
            buffer = buffer.writeBytes(line, 0, line.length);
            buffer = buffer.writeBytes("\n\n".getBytes(), 0, 2);
            HttpContent content = new DefaultHttpContent(buffer);
            channel.write(content);
        }*/
    }

    @Override
    public void flush() throws IOException {
        channel.flush();
    }

    private List<byte[]> splitLines(byte[] input) {
        List<byte[]> lines = new LinkedList<byte[]>();
        int blockStart = 0;
        for (int i=0; i<input.length; i++) {
            if (input[i] == '\n') {
                lines.add(Arrays.copyOfRange(input, blockStart, i));
                blockStart = i+1;
                i = blockStart;
            }
        }
        lines.add(Arrays.copyOfRange(input, blockStart, input.length));
        return lines;
    }
}