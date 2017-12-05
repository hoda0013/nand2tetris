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
                    //pop, pop, add, push result
                    //pop variable off top of stack onto D
                    //point A to SP - 1
                    //D = M+D

                    //@R0
                    //D=M, the current value of SP
                    //D=D-1, decrement SP so it's pointing at top item on stack
                    //M=D, M[R0] = SP - 1
                    //@R0 now set to SP - 1 and D set to SP - 1
                    //D = M   [D] D = value at M[SP -1]
                    //@R12
                    //M=D //M[12] = D, holding temporarily
                    //@R0 //decrement SP
                    //D=M
                    //D=D-1
                    //M=D //set R0 to SP - 1
                    //@R0
                    //D=M, D = M[SP]
                    //@R12
                    //D=D+M
                    //@R0
                    //D=M
                    //D=D+1
                    //M=D

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("D=M-1"); //D = SP - 1
                    writeCommandAndNewline("M=D"); //M[0] = SP

                    //set A = SP, set D = M[SP]
                    writeCommandAndNewline("A=D");
                    writeCommandAndNewline("D=M");

                    //Store value at M[SP] in M[12]
                    writeCommandAndNewline("@R12");
                    writeCommandAndNewline("M=D"); // M[12]=D

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("D=M-1"); //decrement SP
                    writeCommandAndNewline("M=D"); //Store M[0] = SP -1

                    //set value of D to M[SP]
                    writeCommandAndNewline("A=D");
                    writeCommandAndNewline("D=M");

                    //Do addition of M[12] and D
                    writeCommandAndNewline("@R12");
                    writeCommandAndNewline("D=D+M"); //value of addition

                    //set M[SP] = D, the value of addition
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("M=D");

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");

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

                    //but what is the stack? I think this must just be a location in RAM
                    //so I guess we'd want to directly address some RAM and set the value to it
                    //This also means we'll need to keep a stack pointer somewhere

                    //So let's say the SP starts off at some number N then this command would
                    //be something like

                    //@index //load value into A reg
                    //D=A //set value of D reg equal to value in A reg
                    //@SP //Load value of SP register (R0) into A
                    //M=D //Set memory location M[SP] equal to the value in the D register
                    //D=A //load value in A register, the stack pointer value, into the D register
                    //D=D+1 //Increment the value in the D register (the stack pointer) by 1
                    //@R0 //Load the index of R0 in to the A register
                    //M=D //Load the value of D into M[R0], load the incremented SP variable into the SP location
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
