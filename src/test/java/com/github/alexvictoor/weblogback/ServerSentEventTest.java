package com.github.alexvictoor.weblogback;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerSentEventTest {

    @Test
    public void should_generate_a_string_with_level_and_message_ending_with_an_empty_line() {
        ServerSentEvent event = new ServerSentEvent("DEBUG", "whatever");
        String result = event.toString();
        assertThat(result)
                .hasLineCount(3)
                .contains("event: DEBUG")
                .contains("data: whatever")
                .endsWith("\n\n");
    }

    @Test
    public void should_add_a_data_prefix_on_each_message_line() {
        ServerSentEvent event = new ServerSentEvent("WARN", "winter\nis\ncoming");
        String result = event.toString();
        assertThat(result)
                .hasLineCount(5)
                .contains("event: WARN")
                .contains("data: winter")
                .contains("data: is")
                .contains("data: coming")
                .endsWith("\n\n");
    }

    @Test
    public void should_keep_only_levels_known_by_the_browser() {
        ServerSentEvent event = new ServerSentEvent("FATAL", "oops...");
        String result = event.toString();
        assertThat(result).doesNotContain("type: FATAL");
    }


}