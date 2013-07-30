package com.natpryce.jnirn;

import com.natpryce.approvals.IO;
import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;

public class JNIRegisterNativesTest {
    @Rule
    public TestName testName = new TestName();

    @Rule
    public StdoutCapture stdout = new StdoutCapture();

    @Rule
    public ApprovalRule approval = new ApprovalRule("test", "out/test");

    @Test
    public void generatesCCodeFromJar() throws IOException {
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/jars/test-input.jar", "-o", cFile.toString());
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void generatesCCodeFromDirectoryOfClassFiles() throws IOException {
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/classes/test-input", "-o", cFile.toString());
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void generatesDependencyRulesFromSingleJar() throws IOException {
        File mkFile = fileNameForTest(".mk");
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/jars/test-input.jar", "-o", cFile.toString(), "-M", mkFile.toString());

        approval.check(IO.readContents(mkFile));
    }

    @Test
    public void generatesDependencyRulesFromDirectoryOfClassFiles() throws IOException {
        File mkFile = fileNameForTest(".mk");
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/classes/test-input", "-o", cFile.toString(), "-M", mkFile.toString());

        approval.check(IO.readContents(mkFile));
    }

    @Test
    public void canSpecifyNameOfFunctionThatRegistersNativeMethods() throws IOException {
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/jars/test-input.jar", "-o", cFile.toString(), "-f", "DoTheDo");
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void reportsUsage() throws IOException {
        JNIRN.main("out/jars/test-input.jar", "-?");
        approval.check(stdout.captured());
    }

    public File fileNameForTest(String ext) {
        return new File("out/test/" + testName.getMethodName() + ext);
    }
}
