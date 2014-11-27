package com.github.alexvictoor.weblogback;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FileReaderTest {

    @Test
    public void should_read_file_content() throws Exception {
        // given
        FileReader reader = new FileReader();
        // when
        String content = reader.readFileFromClassPath("/test.file");
        // then
        assertThat(content).isEqualTo("Test Content");
    }
}