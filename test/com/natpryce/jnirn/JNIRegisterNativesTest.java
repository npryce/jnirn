package com.natpryce.jnirn;

import com.natpryce.approvals.IO;
import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class JNIRegisterNativesTest {
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Rule
    public StdoutCapture stdout = new StdoutCapture();

    @Rule
    public ApprovalRule approval = new ApprovalRule("test", "out/test");

    @Test
    public void generatesCCode() throws IOException {
        File cFile = tempDir.newFile();
        JNIRN.main("out/jars/test-input.jar", "-o", cFile.toString());
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void generatesDependencyRules() throws IOException {
        File mkFile = tempDir.newFile();
        File cFile = tempDir.newFile();
        JNIRN.main("out/jars/test-input.jar", "-o", cFile.toString(), "-M", mkFile.toString());

        approval.check(IO.readContents(mkFile));
    }

    @Test
    public void canSpecifyNameOfFunctionThatRegistersNativeMethods() throws IOException {
        File cFile = tempDir.newFile();
        JNIRN.main("out/jars/test-input.jar", "-o", cFile.toString(), "-f", "DoTheDo");
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void reportsUsage() throws IOException {
        JNIRN.main("out/jars/test-input.jar", "-?");
        approval.check(stdout.captured());
    }
}
