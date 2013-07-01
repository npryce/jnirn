package com.natpryce.jnirn;

import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class JNIRegisterNativesTest {
    @Rule
    public ApprovalRule approval = new ApprovalRule("src", "out/test", ".c");

    @Test
    public void testNativeDecls() throws IOException {
        StringWriter writer = new StringWriter();

        JNIRN jnirn = new JNIRN();
        jnirn.parseJAR(new File("out/jars/test-input.jar"));
        jnirn.writeCSource(new PrintWriter(writer));

        approval.check(writer.toString());
    }
}

