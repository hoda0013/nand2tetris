package com.example.chapter6;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by DJH on 10/17/17.
 */

public class MyScanner {
    private String mFilename;
    private InputStream mInputStream;
    private Reader mReader;
    private Reader mBuffer;

    public MyScanner(String filename) {
        mFilename = filename;
        init();
    }

    private void init() {
        try {
            mInputStream = new FileInputStream(new File(mFilename));
            mReader = new InputStreamReader(mInputStream);
            mBuffer = new BufferedReader(mReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    long lineCount = 0;
    long columnCount = 0;
    long sourceIndex = 0;

    public MyCharacter get() {
        try {
            int r;

            if ((r = mBuffer.read()) != -1) {
                MyCharacter myCharacter = new MyCharacter((char) r, sourceIndex++, lineCount, columnCount);
                columnCount++;
                if ((char) r == '\n') {
                    lineCount++;
                    columnCount = 0;
                }
                return myCharacter;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
