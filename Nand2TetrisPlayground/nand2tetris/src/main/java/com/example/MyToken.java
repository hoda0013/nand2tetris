package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DJH on 10/23/17.
 */

public class MyToken {

    public enum Token {
        NONE,
        COMMENT,
        L_COMMAND,
        C_COMMAND,
        A_COMMAND
    }

    String mCargo;
    Token mToken;
    long mStartLine;
    long mStartColumn;

    public MyToken(String cargo, Token token, long startLine, long startColumn) {
        mCargo = cargo;
        mToken = token;
        mStartLine = startLine;
        mStartColumn = startColumn;
    }

    public String getCargo() {
        return mCargo;
    }

    @Override
    public String toString() {
        return "TOKEN TYPE = " + mToken.name() + '\n'
                + "VALUE = " + mCargo + '\n';
    }
}


