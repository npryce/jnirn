package com.natpryce.approvals;

import com.natpryce.approvals.junit.ApprovalFailure;

import java.io.*;
import java.util.Scanner;

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

    private String readContents(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        try {
            return scanner.useDelimiter("\\A").next();
        }
        finally {
            scanner.close();
        }
    }

    private void writeContents(File file, String contents) throws IOException {
        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        FileWriter out = new FileWriter(file);
        try {
            out.write(contents);
        }
        finally {
            out.close();
        }
    }
}
