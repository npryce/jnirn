package com.natpryce.jnirn;

import com.beust.jcommander.ParameterException;
import com.natpryce.approvals.IO;
import com.natpryce.approvals.junit.ApprovalRule;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;

public class JNIRegisterNativesTest {
    @Rule
    public TestName testName = new TestName();

    @Rule
    public StandardOutputStreamLog stdout = new StandardOutputStreamLog();

    @Rule
    public ApprovalRule approval = new ApprovalRule("test");

    @Test
    public void generatesCCodeToRegisterNativeMethods() throws IOException {
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/test/jnirn/", "-o", cFile.toString());
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void canGenerateHeaderDeclaringFunctionToRegisterNativeMethods() throws IOException {
        File cFile = fileNameForTest(".c");
        File headerFile = fileNameForTest(".h");
        JNIRN.main("out/test/jnirn", "-o", cFile.toString(), "-H", headerFile.toString());
        approval.check(IO.readContents(headerFile));
    }

    @Test
    public void theGeneratedSourceHashIncludesTheGeneratedHeader() throws IOException {
        File cFile = fileNameForTest(".c");
        File headerFile = fileNameForTest(".h");
        JNIRN.main("out/test/jnirn", "-o", cFile.toString(), "-H", headerFile.toString());
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void generatesDependencyRules() throws IOException {
        File mkFile = fileNameForTest(".mk");
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/test/jnirn", "-o", cFile.toString(), "-M", mkFile.toString());

        approval.check(IO.readContents(mkFile));
    }

    @Test
    public void canSpecifyNameOfFunctionThatRegistersNativeMethods() throws IOException {
        File cFile = fileNameForTest(".c");
        JNIRN.main("out/test/jnirn", "-o", cFile.toString(), "-f", "DoTheDo");
        approval.check(IO.readContents(cFile));
    }

    @Test
    public void writesCCodeToStdoutByDefault() throws IOException {
        JNIRN.main("out/test/jnirn");
        approval.check(stdout.getLog());
    }

    @Test(expected = ParameterException.class)
    public void cannotGenerateMakefileDependenciesIfOutputFileNameNotSpecified() throws IOException {
        File mkFile = fileNameForTest(".mk");
        JNIRN.main("out/test/jnirn", "-M", mkFile.toString());
    }

    @Test
    public void reportsUsage() throws IOException {
        // Ant interferes with System.setOut and so makes this test fail.
        Assume.assumeFalse(Boolean.getBoolean("jnirn.run-by-ant"));

        JNIRN.main("-?");
        approval.check(stdout.getLog());
    }

    public File fileNameForTest(String ext) {
        return new File("out/test/" + testName.getMethodName() + ext);
    }
}
