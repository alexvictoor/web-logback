package com.github.alexvictoor.weblogback;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerSentEventHandler extends SimpleChannelInboundHandler<Object> {

    public static final Logger logger = LoggerFactory.getLogger(ServerSentEventHandler.class);

    private final ChannelRegistry allChannels;
    private final String jsContent;
    private final String htmlContent;
    private final String welcomeMessage;

    public ServerSentEventHandler(ChannelRegistry allChannels, String host, int port) {
        this.allChannels = allChannels;
        this.jsContent
                = new FileReader().readFileFromClassPath("/logback.js");
        this.htmlContent
                = new FileReader().readFileFromClassPath("/homepage.html")
                    .replace("HOST", host)
                    .replace("PORT", Integer.toString(port)
                    );
        this.welcomeMessage = "Connected successfully on LOG stream from " + host + ":" + port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)  {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if ("/stream".equals(request.getUri())) {
                logger.info("New streaming request");
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
                response.headers().set(CONTENT_TYPE, "text/event-stream; charset=UTF-8");
                response.headers().set(CACHE_CONTROL, "no-cache");
                response.headers().set(CONNECTION, "keep-alive");
                response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                ctx.write(response);
                ServerSentEvent event = new ServerSentEvent("INFO", welcomeMessage);
                ByteBuf buffer = Unpooled.copiedBuffer(event.toString(), Charset.defaultCharset());
                HttpContent content = new DefaultHttpContent(buffer);
                ctx.write(content);
                ctx.flush();
                allChannels.addChannel(ctx.channel());
            } else if ("/logback.js".equals(request.getUri())) {
                ByteBuf content = Unpooled.copiedBuffer(jsContent, Charset.defaultCharset());
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
                response.headers().set(CONTENT_TYPE, "text/javascript");
                HttpHeaders.setContentLength(response, content.readableBytes());
                sendHttpResponse(ctx, request, response);
            } else {
                ByteBuf content = Unpooled.copiedBuffer(htmlContent, Charset.defaultCharset());
                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
                response.headers().set(CONTENT_TYPE, "text/html");
                HttpHeaders.setContentLength(response, content.readableBytes());
                sendHttpResponse(ctx, request, response);
            }
        }
    }

    // from netty http examples
    private static void sendHttpResponse(
            ChannelHandlerContext ctx, HttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
