package com.natpryce.jnirn;

import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.*;

public class JNIRegisterNativesTest {
    @Rule
    public ApprovalRule approval = new ApprovalRule("test", "out/test", ".c");

    @Test
    public void testGeneratedCode() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(out));
        try {
            JNIRN.main("out/jars/test-input.jar");
        }
        finally {
            System.setOut(originalOut);
        }

        approval.check(out.toString());
    }
}

