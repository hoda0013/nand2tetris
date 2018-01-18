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
        //TODO: uncomment at some point or get the init code in the correct spot
//        mCodeWriter.writeInit();

        while (mParser.hasMoreCommands()) {
            try {
                mParser.advance();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parser.CommandType currentCommand = mParser.commandType();
            switch (currentCommand) {
                case C_RETURN:
                    mCodeWriter.writeReturn();
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

                case C_LABEL:
                    String label = mParser.arg1();
                    mCodeWriter.writeLabel(label);
                    break;

                case C_IF:
                    String ifLabel = mParser.arg1();
                    mCodeWriter.writeIf(ifLabel);
                    break;

                case C_GOTO:
                    String gotoLabel = mParser.arg1();
                    mCodeWriter.writeGoto(gotoLabel);
                    break;

                case C_FUNCTION:
                    String functionLabel = mParser.arg1();
                    int functionNumLocals = mParser.arg2();
                    mCodeWriter.writeFunction(functionLabel, functionNumLocals);
                    break;

                case C_CALL:
                    mCodeWriter.writeCall(mParser.arg1(), mParser.arg2());
                    break;

                default:
                    throw new RuntimeException("Command of type: " + currentCommand.toString() + " is not supported");
            }
        }

        mCodeWriter.close();
    }
}
