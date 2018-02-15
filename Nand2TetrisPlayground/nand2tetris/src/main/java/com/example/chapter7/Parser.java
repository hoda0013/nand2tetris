package com.example.chapter7;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by DJH on 11/21/17.
 */

public class Parser {
    private static final String COMMENT = "//";
    private static final char TAB = '\t';
    private static final String POP_PREFIX = "pop";
    private static final String PUSH_PREFIX = "push";
    private static final String ADD_PREFIX = "add";
    private static final String SUB_PREFIX = "sub";
    private static final String NEG_PREFIX = "neg";
    private static final String EQ_PREFIX = "eq";
    private static final String GT_PREFIX = "gt";
    private static final String LT_PREFIX = "lt";
    private static final String AND_PREFIX = "and";
    private static final String OR_PREFIX = "or";
    private static final String NOT_PREFIX = "not";
    private static final String LABEL_PREFIX = "label";
    private static final String GOTO_PREFIX = "goto";
    private static final String IF_GOTO_PREFIX = "if-goto";
    private static final String FUNCTION_PREFIX = "function";
    private static final String CALL_PREFIX = "call";
    private static final String RETURN_PREFIX = "return";

    private static final int BUFFER_SIZE = 1000;

    private String mFilename;
    private File mFile;
    private BufferedReader mReader;
    private String mCurrentCommand;

    public enum CommandType {
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }

    public Parser() {
    }

    public void setFilename(String filename) {
        mFilename = filename;
        init();
    }

    private void init() {
        mFile = new File(mFilename);
        try {
            FileReader mFileReader = new FileReader(mFile);
            mReader = new BufferedReader(mFileReader, BUFFER_SIZE);
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
        if (mCurrentCommand.contains(" ")) {
            if (mCurrentCommand.contains(COMMENT)) {
                mCurrentCommand = mCurrentCommand.substring(0, mCurrentCommand.indexOf("/"));
                mCurrentCommand = mCurrentCommand.trim();
            }
            if (mCurrentCommand.indexOf(TAB) != -1) {
                mCurrentCommand = mCurrentCommand.substring(0, mCurrentCommand.indexOf(TAB));
            }
        }
    }

    public CommandType commandType() {
        if (mCurrentCommand.startsWith(ADD_PREFIX)
                || mCurrentCommand.startsWith(SUB_PREFIX)
                || mCurrentCommand.startsWith(NEG_PREFIX)
                || mCurrentCommand.startsWith(EQ_PREFIX)
                || mCurrentCommand.startsWith(GT_PREFIX)
                || mCurrentCommand.startsWith(LT_PREFIX)
                || mCurrentCommand.startsWith(AND_PREFIX)
                || mCurrentCommand.startsWith(OR_PREFIX)
                || mCurrentCommand.startsWith(NOT_PREFIX)) {
            return CommandType.C_ARITHMETIC;
        } else if (mCurrentCommand.startsWith(PUSH_PREFIX)) {
            return CommandType.C_PUSH;
        } else if (mCurrentCommand.startsWith(POP_PREFIX)) {
            return CommandType.C_POP;
        } else if (mCurrentCommand.startsWith(LABEL_PREFIX)) {
            return CommandType.C_LABEL;
        } else if (mCurrentCommand.startsWith(GOTO_PREFIX)) {
            return CommandType.C_GOTO;
        } else if (mCurrentCommand.startsWith(IF_GOTO_PREFIX)) {
            return CommandType.C_IF;
        } else if (mCurrentCommand.startsWith(FUNCTION_PREFIX)) {
            return CommandType.C_FUNCTION;
        } else if (mCurrentCommand.startsWith(CALL_PREFIX)) {
            return CommandType.C_CALL;
        } else if (mCurrentCommand.startsWith(RETURN_PREFIX)) {
            return CommandType.C_RETURN;
        } else {
            throw new RuntimeException("commandType " + mCurrentCommand + " not recognized");
        }
    }

    public String arg1() {
        if (commandType() == CommandType.C_RETURN) {
            throw new IllegalStateException("commandType is of type C_RETURN which is illegal");
        }

        switch (commandType()) {
            case C_ARITHMETIC:

                return mCurrentCommand;
            case C_PUSH:
            case C_POP:
            case C_LABEL:
            case C_GOTO:
            case C_IF:
            case C_FUNCTION:
            case C_CALL:
                String[] tokens = mCurrentCommand.split(" ");
                return tokens[1];

            default:
                throw new RuntimeException(commandType() + " does not have a first arg");

        }
    }

    public int arg2() {
        if (commandType() == CommandType.C_ARITHMETIC
            || commandType() == CommandType.C_LABEL
            || commandType() == CommandType.C_IF
            || commandType() == CommandType.C_GOTO
                || commandType() == CommandType.C_RETURN) {
            throw new RuntimeException("command does not have a second arg: " + commandType());
        }

        switch (commandType()) {
            case C_PUSH:
            case C_POP:
            case C_FUNCTION:
            case C_CALL:
                String[] tokens = mCurrentCommand.split(" ");
                return Integer.valueOf(tokens[2]);
            default:
                throw new RuntimeException(commandType() + " cannot be handled");

        }
    }
}
