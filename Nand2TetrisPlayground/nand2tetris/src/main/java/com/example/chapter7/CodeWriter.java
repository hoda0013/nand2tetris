package com.example.chapter7;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by DJH on 11/27/17.
 */

public class CodeWriter {
    private String mOutputFilename;
    private File mFile;
    private BufferedWriter mBufferedWriter;

    private String mInputFilename;

    public CodeWriter(String outputFilename) {
        mOutputFilename = outputFilename;
        init();
    }

    private void init() {
        mFile = new File(mOutputFilename);
        try {
            mBufferedWriter = new BufferedWriter(new FileWriter(mFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFileName(String filename) {
        mInputFilename = filename;
    }

    public void writeArithmetic(String command) {
        //writes assembly code of the given arithmetic command
        try {
            switch (command) {
                case "add":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP]
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //decrement SP and add value it points to to D
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("D=D+M"); //D = M[SP] + M[SP - 1}
                    writeCommandAndNewline("M=D"); //M[SP] = D
                    break;
                case "sub":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP]
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //decrement SP and subtract value it points to from D
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1}
                    writeCommandAndNewline("M=D"); //M[SP] = D
                    break;
                case "neg":
                    //2's complement way of making a number have the opposite sign
                    //To make a positive value negative, write out the positive number in binary, invert the digits, add one

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    writeCommandAndNewline("D=-D");
                    break;

                case "eq":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //compute x-y, then if equals zero, jump
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1}
                    writeCommandAndNewline("@ZERO");
                    writeCommandAndNewline("D;JEQ");
                    writeCommandAndNewline("@R0"); //not equal
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=0"); //x-y != 0, false
                    writeCommandAndNewline("@DONE");
                    writeCommandAndNewline("0;JMP");

                    writeCommandAndNewline("(ZERO)");
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("A=M"); //A=M[0] = SP value
                    writeCommandAndNewline("M=-1"); //x-y == 0, M[SP] = true
                    writeCommandAndNewline("(DONE)");
                    break;


                case "gt":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //compute x-y, if positive x is greater than y
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1}
                    writeCommandAndNewline("@POSITIVE");
                    writeCommandAndNewline("D;JGT");
                    writeCommandAndNewline("@DONE");
                    writeCommandAndNewline("0;JMP");
                    writeCommandAndNewline("M=0"); //x-y <= 0, false
                    writeCommandAndNewline("(POSITIVE)");
                    writeCommandAndNewline("M=-1"); //x-y > 0, true
                    writeCommandAndNewline("(DONE)");

                case "lt":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //compute x-y, if negative x is greater than y
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1}
                    writeCommandAndNewline("@NEGATIVE");
                    writeCommandAndNewline("D;JLT");
                    writeCommandAndNewline("@DONE");
                    writeCommandAndNewline("0;JMP");
                    writeCommandAndNewline("M=0"); //x-y >= 0, false
                    writeCommandAndNewline("(NEGATIVE)");
                    writeCommandAndNewline("M=-1"); //x-y < 0, true
                    writeCommandAndNewline("(DONE)");

                    break;

                case "and":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP], y

                    //bit-wise and of x & y
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("M=M&D"); //M = M[SP] & M[SP - 1}
                    break;

                case "or":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP], y

                    //bit-wise or of x | y
                    writeCommandAndNewline("A=A-1"); //Decrement SP
                    writeCommandAndNewline("M=M|D"); //M = M[SP] | M[SP - 1}
                    break;

                case "not":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP

                    //bit-wise not of y
                    writeCommandAndNewline("M=!M"); //D = M[SP], y //TODO: not sure if this really works
                    break;
                default:
                    throw new RuntimeException("Arithmetic command " + command + " not recognized");
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: how to handle
        }
    }

    private void writeCommandAndNewline(String command) throws IOException{
        mBufferedWriter.append(command);
        mBufferedWriter.append('\n');
    }

    public void writePushPop(Parser.CommandType commandType, String segment, int index) {
        if (commandType == Parser.CommandType.C_PUSH) {
            switch(segment) {
                case "argument":

                    break;

                case "local":

                    break;

                case "static":

                    break;

                case "constant":
                    //the command "push constant value" should push the value onto the stack
                    try {
                        writeCommandAndNewline("@" + String.valueOf(index)); //load number in A reg
                        writeCommandAndNewline("D=A");
                        writeCommandAndNewline("@R0");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("M=D");
                        writeCommandAndNewline("D=A+1");
                        writeCommandAndNewline("@R0");
                        writeCommandAndNewline("M=D");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case "this":

                    break;

                case "that":

                    break;

                case "pointer":

                    break;

                case "temp":

                    break;

                default:
                    throw new RuntimeException("segment: " + segment + " not recognized");

            }
        } else if (commandType == Parser.CommandType.C_POP) {
            switch(segment) {
                case "argument":

                    break;

                case "local":

                    break;

                case "static":

                    break;

                case "constant":

                    break;

                case "this":

                    break;

                case "that":

                    break;

                case "pointer":

                    break;

                case "temp":

                    break;

                default:
                    throw new RuntimeException("segment: " + segment + " not recognized");

            }
        } else {
            throw new RuntimeException("command type must be C_PUSH or C_POP. The command " + commandType.name() + " is not supported");
        }
    }

    public void close() {
        try {
            mBufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
