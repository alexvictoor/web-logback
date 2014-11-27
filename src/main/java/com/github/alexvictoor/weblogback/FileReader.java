package com.github.alexvictoor.weblogback;

import java.util.Scanner;

public class FileReader {

    public String readFileFromClassPath(String classPath) {
        return new Scanner(getClass().getResourceAsStream(classPath)).useDelimiter("\\Z").next();

    }
}
