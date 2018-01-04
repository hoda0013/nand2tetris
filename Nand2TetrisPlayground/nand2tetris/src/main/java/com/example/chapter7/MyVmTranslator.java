package com.example.chapter7;

import java.io.IOException;

/**
 * Created by DJH on 12/3/17.
 */

public class MyVmTranslator {
    private Parser mParser;
    private CodeWriter mCodeWriter;

    public MyVmTranslator(String inputFilename, String outputFilename) {
        mParser = new Parser(inputFilename);
        mCodeWriter = new CodeWriter(outputFilename);
        mCodeWriter.setFileName(inputFilename);
    }

    public void translate() {
        while (mParser.hasMoreCommands()) {
            try {
                mParser.advance();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parser.CommandType currentCommand = mParser.commandType();
            switch (currentCommand) {
                case C_RETURN:

                    break;

                case C_ARITHMETIC:
                    String arithmeticCommand = mParser.arg1();
                    mCodeWriter.writeArithmetic(arithmeticCommand);
                    break;

                case C_PUSH:
                case C_POP:

                    String arg1 = mParser.arg1();
                    int arg2 = mParser.arg2();
                    mCodeWriter.writePushPop(currentCommand, arg1, arg2);

                    break;

                default:
                    throw new RuntimeException("Command of type: " + currentCommand.toString() + " is not supported");
            }
        }

        mCodeWriter.close();
    }
}
