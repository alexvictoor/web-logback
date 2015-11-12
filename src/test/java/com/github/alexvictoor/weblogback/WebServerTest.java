package com.github.alexvictoor.weblogback;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;

public class WebServerTest {

    public static final Logger logger = LoggerFactory.getLogger(WebServerTest.class);
    private WebServer server;
    private int port;
    private OkHttpClient httpClient;
    private int replayBufferSize = 1;

    @Before
    public void launchServer() {
        port = getAvailablePort();
        server = new WebServer("localhost", port, replayBufferSize);
        server.start();
        httpClient = new OkHttpClient();
    }
    @After
    public void shutdownServer() {
        server.stop();
    }

    @Test
    public void should_respond_with_js_content() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + port + "/logback.js")
                .build();
        Response response = httpClient.newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).contains("EventSource");

    }

    @Test
    public void should_respond_with_html_content() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + port)
                .build();
        Response response = httpClient.newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).contains("Get logs!");

    }

    @Test
    public void should_not_fail_stopping_a_server_not_started() throws IOException {
        WebServer server2 = new WebServer("localhost", port, replayBufferSize);
        server2.stop();
    }

    public int getAvailablePort() {
        try {
            ServerSocket s = new ServerSocket(0);
            int result = s.getLocalPort();
            s.close();
            return result;
        } catch (IOException e) {
            logger.error("did not found any free port", e);
            throw new RuntimeException(e);
        }
    }

}