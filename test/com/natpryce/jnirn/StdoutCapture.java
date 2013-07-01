package com.natpryce.jnirn;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StdoutCapture extends TestWatcher {
    private PrintStream originalStdout;
    private ByteArrayOutputStream temporaryStdout = new ByteArrayOutputStream();

    @Override
    protected void starting(Description description) {
        originalStdout = System.out;
        System.setOut(new PrintStream(temporaryStdout));
    }

    @Override
    protected void finished(Description description) {
        System.setOut(originalStdout);
    }

    public String captured() {
        return temporaryStdout.toString();
    }
}
