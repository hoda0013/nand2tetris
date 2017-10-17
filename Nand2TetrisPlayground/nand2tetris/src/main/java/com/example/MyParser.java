package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Created by DJH on 10/16/17.
 */

public class MyParser {
    private static final String COMMENT = "//";
    private static final String A_COMMAND_PREFIX = "@";
    private static final String C_COMMAND_EQUALS_SIGN = "=";
    private static final String C_COMMAND_SEMICOLON_SIGN = ";";

    private static final int BUFFER_SIZE = 1000;
    String mFilename;
    private BufferedReader mReader;

    private String mCurrentCommand;

    public enum CommandType {
        A_COMMAND,
        C_COMMAND,
        L_COMMAND
    }

    public MyParser(String filename) {
        mFilename = filename;
        init();
    }

    private void init() {
        File file = new File(mFilename);
        try {
            FileReader fileReader = new FileReader(file);
            mReader = new BufferedReader(fileReader, BUFFER_SIZE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMoreCommands() {
        try {
            mReader.mark(BUFFER_SIZE);
            String line = mReader.readLine();
            if (line == null) {
                return false;
            } else {
                mReader.reset();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Should only be called if hasMoreCommands returns true
    public void advance() throws IOException {
        //read line
        //trim line of leading and trailing whitespace
        //if line starts with // then it is a comment
        //if line is blank it will get completely trimmed away
        do {
            mCurrentCommand = mReader.readLine();
            mCurrentCommand = mCurrentCommand.trim();
        } while (mCurrentCommand.length() == 0
                || mCurrentCommand.startsWith(COMMENT));

        //Now we have a line that has at least a command and possibly a comment inline
        //Find the first space and replace everything after with empty
        mCurrentCommand = mCurrentCommand.substring(0, mCurrentCommand.indexOf(" "));
    }

    public CommandType commandType() {
        if (mCurrentCommand.startsWith(A_COMMAND_PREFIX)) {
            return CommandType.A_COMMAND;
        } else if (mCurrentCommand.contains(C_COMMAND_EQUALS_SIGN)
                || mCurrentCommand.contains(C_COMMAND_SEMICOLON_SIGN)) {
            return CommandType.C_COMMAND;
        } else {
            return CommandType.L_COMMAND;
        }
    }

    public String symbol() {
        return null;
    }

    public String dest() {
        return null;
    }

    public String comp() {
        return null;
    }

    public String jump() {
        return null;
    }

}
