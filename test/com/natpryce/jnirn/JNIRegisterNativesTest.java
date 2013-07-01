package com.natpryce.jnirn;

import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class JNIRegisterNativesTest {
    @Rule
    public ApprovalRule approval = new ApprovalRule("test", "out/test", ".c");

    @Rule
    public StdoutCapture stdout = new StdoutCapture();

    @Test
    public void testGeneratedCode() throws IOException {
        JNIRN.main("out/jars/test-input.jar");
        approval.check(stdout.captured());
    }
}
