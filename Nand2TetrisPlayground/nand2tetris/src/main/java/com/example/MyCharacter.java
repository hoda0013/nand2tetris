package com.example;

/**
 * Created by DJH on 10/17/17.
 */

public class MyCharacter {
    private char mCargo;
    private long mSourceIndex;
    private long mLineIndex;
    private long mColumnIndex;

    public MyCharacter(char cargo, long sourceIndex, long lineIndex, long columnIndex) {
        mCargo = cargo;
        mSourceIndex = sourceIndex;
        mLineIndex = lineIndex;
        mColumnIndex = columnIndex;
    }

    public char getCargo() {
        return mCargo;
    }

    @Override
    public String toString() {
        String value = String.valueOf(mSourceIndex)
                + " "
                + String.valueOf(mLineIndex)
                + " "
                + String.valueOf(mColumnIndex)
                + " ";
        if(mCargo == ' ') {
            return value + "space";
        } else if (mCargo == '\n') {
            return value + "newline";
        } else if (mCargo == '\t') {
            return value + "tab";
        } else {
            return value + String.valueOf(mCargo);
        }
    }
}
