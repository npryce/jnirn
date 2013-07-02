package com.natpryce.approvals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class IO {
    public static String readContents(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        try {
            return scanner.useDelimiter("\\A").next();
        }
        finally {
            scanner.close();
        }
    }

    public static void writeContents(File file, String contents) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        FileWriter out = new FileWriter(file);
        try {
            out.write(contents);
        }
        finally {
            out.close();
        }
    }
}
