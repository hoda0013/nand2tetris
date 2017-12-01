package com.example.chapter7;

/**
 * Created by DJH on 11/27/17.
 */

public class CodeWriter {
    private String mOutputFilename;

    public CodeWriter() {
    }

    public void setFileName(String filename) {
        mOutputFilename = filename;
    }

    public void writeArithmetic(String command) {
        //writes assembly code of the given arithmetic command
        switch (command) {
            case "add":
                //pop, pop, add, push result

                //@SP //A reg = SP value
                //A=A-1, point SP at previous entry on stack, store in A register
                //D=M //take value at top of stack and store in D register
                //@SP
                //A=A-1 //point to the next element on the stack
                //D=D+M D = current value plus value at M[A] which is M[SP]
                //@SP //Load SP into A reg
                //M=D //Set M[SP] equal to sum

                break;
        }
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

}
