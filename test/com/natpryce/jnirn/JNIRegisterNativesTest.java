package com.natpryce.jnirn;

import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class JNIRegisterNativesTest {
    @Rule
    public ApprovalRule approval = new ApprovalRule("test", "out/test");

    @Rule
    public StdoutCapture stdout = new StdoutCapture();

    @Test
    public void generatesCCode() throws IOException {
        JNIRN.main("out/jars/test-input.jar", "-o", "-");
        approval.check(stdout.captured());
    }

    @Test
    public void reportsUsage() throws IOException {
        JNIRN.main("out/jars/test-input.jar", "-?");
        approval.check(stdout.captured());
    }
}
