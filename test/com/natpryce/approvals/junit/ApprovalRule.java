package com.natpryce.approvals.junit;

import com.natpryce.approvals.ApprovalError;
import com.natpryce.approvals.Approver;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;

public class ApprovalRule implements TestRule {
    private final File approvedDir;
    private final File receivedDir;
    private final String suffix;

    private Approver approver = null;

    public ApprovalRule(String approvedDir, String receivedDir, String suffix) {
        this.suffix = suffix;
        this.approvedDir = new File(approvedDir);
        this.receivedDir = new File(receivedDir);
    }

    public void check(String receivedContents) throws IOException {
        if (approver == null) {
            throw new IllegalStateException("cannot perform approval before discovering the name of the test (did you forget to annotate the approver as a @Rule?)");
        }

        try {
            approver.check(receivedContents);
        } catch (ApprovalError e) {
            throw new ApprovalFailure(e);
        }
    }

    public void recordAsApproved(String receivedContents) throws IOException {
        approver.recordAsApproved(receivedContents);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        this.approver = new Approver(testFile(approvedDir, description), testFile(receivedDir, description));
        return base;
    }

    private File testFile(File baseDir, Description testDescription) {
        return new File(baseDir, testDescription.getTestClass().getName().replace(".", "/") + "." + testDescription.getMethodName() + suffix);
    }
}