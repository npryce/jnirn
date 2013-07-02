package com.natpryce.approvals;

import java.io.File;
import java.io.IOException;

import static com.natpryce.approvals.IO.readContents;
import static com.natpryce.approvals.IO.writeContents;

public class Approver {
    private final File approvedFile;
    private final File receivedFile;

    public Approver(File approvedFile, File receivedFile) {
        this.approvedFile = approvedFile;
        this.receivedFile = receivedFile;
    }

    public void check(String receivedContents) throws IOException {
        writeContents(receivedFile, receivedContents);

        if (!approvedFile.exists()) {
            throw new ApprovalError(approvedFile, receivedFile, receivedContents);
        }

        String approvedContents = readContents(approvedFile);

        if (! receivedContents.equals(approvedContents)) {
            throw new ApprovalError(approvedFile, approvedContents, receivedFile, receivedContents);
        }
    }

    public void recordAsApproved(String receivedContents) throws IOException {
        writeContents(approvedFile, receivedContents);
    }

}
