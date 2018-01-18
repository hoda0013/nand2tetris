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

    private String mClassName;
    private String mInputFilePath;
    private int mNumEqCalls = 0;
    private int mNumGtCalls = 0;
    private int mNumLtCalls = 0;

    private int mNumLabelCalls = 0;

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
        mInputFilePath = filename;
        mClassName = mInputFilePath.substring(mInputFilePath.lastIndexOf('/'
        ) + 1, mInputFilePath.length() - 3);
    }

    public void writeArithmetic(String command) {
        //writes assembly code of the given arithmetic command
        try {
            switch (command) {
                case "add":
                    //Decrement SP and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //Get value pointed at by SP and store in D register
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //decrement SP
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //Add value pointed at by SP to value in D register and store in D register
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("D=D+M");

                    //Save added value in location pointed to by SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("M=D"); //M[SP] = D = sum

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    break;
                case "sub":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M[0] = M[0] - 1

                    //get value of M[SP]
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //decrement SP
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M[0] = M[0] - 1

                    //Subtract y from x, that is, value pointed at by SP - value in D register
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("D=M-D"); //D = M[A] - D = M[SP] - D

                    //Save added value in location pointed to by SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("M=D"); //M[SP] = D = sub value

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    break;
                case "neg":
                    //2's complement way of making a number have the opposite sign
                    //To make a positive value negative, write out the positive number in binary, invert the digits, add one

                    //Decrement SP
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=-M"); //D = M[SP]

                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=D");

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    break;

                case "eq":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //compute x-y, then if equals zero, jump
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1");
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1}
                    writeCommandAndNewline("@EQ" + "_" + String.valueOf(mNumEqCalls));
                    writeCommandAndNewline("D;JEQ");

                    writeCommandAndNewline("@R0"); //not equal
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=0"); //x-y != 0, false
                    writeCommandAndNewline("@EQ_DONE" + "_" + String.valueOf(mNumEqCalls));
                    writeCommandAndNewline("0;JMP");

                    writeCommandAndNewline("(EQ_" + String.valueOf(mNumEqCalls) + ")");
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("A=M"); //A=M[0] = SP value
                    writeCommandAndNewline("M=-1"); //x-y == 0, M[SP] = true

                    writeCommandAndNewline("(EQ_DONE_" + String.valueOf(mNumEqCalls) + ")");

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    mNumEqCalls++;
                    break;


                case "gt":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP]

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //compute x-y, if positive x is greater than y
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1], if D > 0, then x > y
                    writeCommandAndNewline("@GT_" + String.valueOf(mNumGtCalls));
                    writeCommandAndNewline("D;JGT");

                    //x <= y
                    writeCommandAndNewline("@R0"); //not equal
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=0"); //false
                    writeCommandAndNewline("@GT_DONE_" + String.valueOf(mNumGtCalls));
                    writeCommandAndNewline("0;JMP");

                    //x > y
                    writeCommandAndNewline("(GT_" + String.valueOf(mNumGtCalls) + ")");
                    writeCommandAndNewline("@R0"); //not equal
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=-1"); //x-y > 0, true
                    writeCommandAndNewline("(GT_DONE_" + String.valueOf(mNumGtCalls) + ")");

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    mNumGtCalls++;
                    break;

                case "lt":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP = M[0]
                    writeCommandAndNewline("D=M"); //D = M[SP] = y

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //compute x-y, if negative x is greater than y
                    writeCommandAndNewline("A=M"); //A = M[0] = SP
                    writeCommandAndNewline("D=M-D"); //D = M[SP] - M[SP - 1] = x - y
                    writeCommandAndNewline("@LT_" + String.valueOf(mNumLtCalls));
                    writeCommandAndNewline("D;JLT");
                    //x >= y
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("M=0"); //false M[SP] = 0
                    writeCommandAndNewline("@LT_DONE_" + String.valueOf(mNumLtCalls));
                    writeCommandAndNewline("0;JMP");

                    //x < y
                    writeCommandAndNewline("(LT_" + String.valueOf(mNumLtCalls) + ")");
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=-1"); //true
                    writeCommandAndNewline("(LT_DONE_" + String.valueOf(mNumLtCalls) + ")");

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    mNumLtCalls++;
                    break;

                case "and":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP], y

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //bit-wise and of x & y
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=M&D"); //M = M[SP] & M[SP - 1}

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    break;

                case "or":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D reg
                    writeCommandAndNewline("A=M"); //A = SP
                    writeCommandAndNewline("D=M"); //D = M[SP], y

                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //bit-wise or of x | y
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("A=M");
                    writeCommandAndNewline("M=M|D"); //M = M[SP] | M[SP - 1}

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    break;

                case "not":
                    //get SP, decrement and store in R0
                    writeCommandAndNewline("@R0"); //A=0
                    writeCommandAndNewline("M=M-1"); //M = SP - 1

                    //get value of M[SP], store in D regSt
                    writeCommandAndNewline("A=M"); //A = SP

                    //bit-wise not of y
                    writeCommandAndNewline("M=!M"); //D = M[SP], y //TODO: not sure if this really works

                    //increment SP
                    writeCommandAndNewline("@R0");
                    writeCommandAndNewline("M=M+1");
                    break;
                default:
                    throw new RuntimeException("Arithmetic command " + command + " not recognized");
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: how to handle
        }
    }

    private void decrementStackPointerAndSaveInSpRegister() throws IOException {
        //get SP, decrement and store in R0
        writeCommandAndNewline("@R0"); //A=0
        writeCommandAndNewline("M=M-1"); //M = SP - 1
    }

    private void incrementStackPointer() throws IOException {
        //Increment the SP and save it at R0
        writeCommandAndNewline("@R0");
        writeCommandAndNewline("M=M+1");
    }

    private void loadConstantIntoDRegister(int index) throws IOException {
        writeCommandAndNewline("@" + String.valueOf(index)); //load number in A reg
        writeCommandAndNewline("D=A"); //D = number
    }

    private void writeCommandAndNewline(String command) throws IOException {
        mBufferedWriter.append(command);
        mBufferedWriter.append('\n');
    }

    private void push(int index, String baseSegment) throws IOException {
        //Set D = index
        writeCommandAndNewline("@" + String.valueOf(index)); //load number in A reg
        writeCommandAndNewline("D=A"); //D = number
        // Get base index, add index to it, store in base reg
        writeCommandAndNewline(baseSegment);
        writeCommandAndNewline("D=M+D");
        writeCommandAndNewline("A=D");
        writeCommandAndNewline("D=M");
        //Get value of SP
        writeCommandAndNewline("@SP");
        writeCommandAndNewline("A=M");
        writeCommandAndNewline("M=D");
        //increment SP
        incrementStackPointer();
    }

    public void writePushPop(Parser.CommandType commandType, String segment, int index) {
        if (commandType == Parser.CommandType.C_PUSH) {
            switch (segment) {
                case "argument":
                    try {
                        push(index, "@R2");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "local":
                    try {
                        push(index, "@R1");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "static":
                    try {
                        writeCommandAndNewline("@" + mClassName + "." + String.valueOf(index)); //load number in A reg
                        writeCommandAndNewline("D=M"); //D = value at index

                        writeCommandAndNewline("@SP");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("M=D");

                        incrementStackPointer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case "constant":
                    //the command "push constant value" should push the value onto the stack
                    try {
                        //Load the constant into D reg
                        writeCommandAndNewline("@" + String.valueOf(index)); //load number in A reg
                        writeCommandAndNewline("D=A"); //D = number

                        //Load the value of the SP into register A
                        writeCommandAndNewline("@R0");
                        writeCommandAndNewline("A=M"); //Set A = M[0]

                        //Set M[SP] equal to the constant held in the D register
                        writeCommandAndNewline("M=D"); //Set M[SP] = D = the constant we want to push on the stack

                        incrementStackPointer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case "this":
                    try {
                        push(index, "@R3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "that":
                    try {
                        push(index, "@R4");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "pointer":
                    //push the value of pointer 0 or 1 onto the stack
                    int address = 3 + index;
                    try {
                        //get the address of the pointer and set to D
                        writeCommandAndNewline("@" + String.valueOf(address)); //load number in A reg
                        writeCommandAndNewline("D=M"); //D = value at index

                        writeCommandAndNewline("@SP");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("M=D");

                        incrementStackPointer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "temp":

                    try {
                        //Push value from temp index onto stack

                        //D = index value
                        loadConstantIntoDRegister(index);
                        //Get offset from base (R5)
                        writeCommandAndNewline("@R5");
                        //D = 5 + index
                        writeCommandAndNewline("D=A+D");
                        //A = index of temp pointer
                        writeCommandAndNewline("A=D");
                        //D = value of temp pointer
                        writeCommandAndNewline("D=M");
                        //Set M[SP] = D
                        writeCommandAndNewline("@SP");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("M=D");

                        incrementStackPointer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    throw new RuntimeException("segment: " + segment + " not recognized");

            }
        } else if (commandType == Parser.CommandType.C_POP) {
            switch (segment) {
                case "argument":
                    try {
                        pop(index, "@R2");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case "local":
                    try {
                        pop(index, "@R1");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "static":
                    try {
                        decrementStackPointerAndSaveInSpRegister();

                        //Set D = value at M[SP]
                        writeCommandAndNewline("@SP");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("D=M");

                        writeCommandAndNewline("@" + mClassName + "." + String.valueOf(index)); // in the form Classname.index
                        writeCommandAndNewline("M=D"); //D = value at index

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case "this":
                    try {
                        pop(index, "@R3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "that":
                    try {
                        pop(index, "@R4");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case "pointer":
                    try {
                        decrementStackPointerAndSaveInSpRegister();

                        //Set D = value at M[SP]
                        writeCommandAndNewline("@SP");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("D=M");

                        writeCommandAndNewline("@" + String.valueOf(3 + index)); //load number in A reg
                        writeCommandAndNewline("M=D"); //D = value at index

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case "temp":
                    try {
                        decrementStackPointerAndSaveInSpRegister();

                        writeCommandAndNewline("@SP");
                        writeCommandAndNewline("A=M");
                        writeCommandAndNewline("D=M");

                        int address = 5 + index;
                        writeCommandAndNewline("@" + String.valueOf(address)); //load number in A reg
                        writeCommandAndNewline("M=D");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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

    private void pop(int commandIndex, String baseIndex) throws IOException {
        decrementStackPointerAndSaveInSpRegister();

        loadConstantIntoDRegister(commandIndex);

        //Set A equal to value of arg base
        writeCommandAndNewline(baseIndex);
        //Set base index register equal to the base value plus the offset
        writeCommandAndNewline("M=M+D");

        //Get value of SP
        writeCommandAndNewline("@SP");
        writeCommandAndNewline("A=M");
        //Get value pointed at by SP
        writeCommandAndNewline("D=M");
        //Put value in register pointed to by base index + offset
        writeCommandAndNewline(baseIndex);
        writeCommandAndNewline("A=M");
        writeCommandAndNewline("M=D");
        //reset base index
        loadConstantIntoDRegister(commandIndex);
        writeCommandAndNewline(baseIndex);
        writeCommandAndNewline("M=M-D");
    }

    public void writeInit() {
        //TODO: setup bootstrapping code
        try {
            //set R0 = 256 aka SP = 256
            writeCommandAndNewline("@256");
            writeCommandAndNewline("D=A");
            writeCommandAndNewline("@0");
            writeCommandAndNewline("M=D");

            //reset D and A back to 0
            writeCommandAndNewline("D=A");

            //TODO: eventually the other register values need to be set here
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLabel(String label) {
        try {
            //TODO: Not sure if these need an index added to them to guarantee uniqueness
            writeCommandAndNewline("(" + label + "_" + mClassName + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeGoto(String label) {
        try {
            //Set A equal to the label we want to jump to then jump there
            writeCommandAndNewline("@" + label + "_" + mClassName);
            writeCommandAndNewline("0;JMP");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeIf(String label) {
        try {
            //Get top value of stack and see if it equals zero
            decrementStackPointerAndSaveInSpRegister();

            writeCommandAndNewline("@0");
            writeCommandAndNewline("A=M"); //A = value of SP = M[0]
            writeCommandAndNewline("D=M"); //D = M[SP] = value at top of stack

            //If value pointed to by SP, represented by D, equals zero then jump to label
            writeCommandAndNewline("@" + label + "_" + mClassName);
            writeCommandAndNewline("D;JNE");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCall(String functionName, int numArgs) {
        try {
            writeCommandAndNewline("(RETURN_" + functionName + ")");

            writeCommandAndNewline("@R1");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@SP");
            writeCommandAndNewline("M=D");
            incrementStackPointer();

            writeCommandAndNewline("@R2");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@SP");
            writeCommandAndNewline("M=D");
            incrementStackPointer();

            writeCommandAndNewline("@R3");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@SP");
            writeCommandAndNewline("M=D");
            incrementStackPointer();

            writeCommandAndNewline("@R4");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@SP");
            writeCommandAndNewline("M=D");
            incrementStackPointer();

            writeCommandAndNewline("@SP");
            writeCommandAndNewline("D=M"); //D = value of SP
            writeCommandAndNewline("@" + String.valueOf(5+numArgs));
            writeCommandAndNewline("D=D-A");
            writeCommandAndNewline("@R2");
            writeCommandAndNewline("M=D"); //ARG = SP - 5 - numArgs

            writeCommandAndNewline("@SP");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@R1");
            writeCommandAndNewline("M=D"); //LCL = SP

            writeCommandAndNewline("@FUNCTION_" + functionName);
            writeCommandAndNewline("0;JMP"); //Jump to function label

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeReturn() {
        try {
            //Save address of local to temp variable
            writeCommandAndNewline("@R1");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@R5");
            writeCommandAndNewline("M=D");

            //Get the value of the return address (located at FRAME - 5) and store in temp var
            writeCommandAndNewline("@5");
            writeCommandAndNewline("D=D-A");
            writeCommandAndNewline("A=D");
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@R6");
            writeCommandAndNewline("M=D");

            //Set return value at correct address, Take value pointed at by SP and move it to address stored at @R6
            writeCommandAndNewline("@SP");
            writeCommandAndNewline("A=M-1"); //Set A to value of SP-1
            writeCommandAndNewline("D=M");
            writeCommandAndNewline("@R2");
            writeCommandAndNewline("A=M");
            writeCommandAndNewline("M=D"); //write value of D to value pointed at by R6, the return address

            //Set SP = ARG + 1
            writeCommandAndNewline("@R2");
            writeCommandAndNewline("D=M+1");
            writeCommandAndNewline("@SP");
            writeCommandAndNewline("M=D"); //set value at SP equal to ARG + 1

            //Set THAT = FRAME - 1
            writeCommandAndNewline("@R5");
            writeCommandAndNewline("D=M"); //Get value of FRAME
            writeCommandAndNewline("@1");
            writeCommandAndNewline("A=D-A"); //FRAME = FRAME - 1
            writeCommandAndNewline("D=M"); //Get value at FRAME - 1
            writeCommandAndNewline("@R4");
            writeCommandAndNewline("M=D"); //THAT = FRAME - 1

            //Set THIS = FRAME - 2
            writeCommandAndNewline("@R5");
            writeCommandAndNewline("D=M"); //Get value of FRAME
            writeCommandAndNewline("@2");
            writeCommandAndNewline("A=D-A"); //FRAME = FRAME - 2
            writeCommandAndNewline("D=M"); //Get value at FRAME - 2
            writeCommandAndNewline("@R3");
            writeCommandAndNewline("M=D"); //THIS = FRAME - 2

            //Set ARG = FRAME - 3
            writeCommandAndNewline("@R5");
            writeCommandAndNewline("D=M"); //Get value of FRAME
            writeCommandAndNewline("@3");
            writeCommandAndNewline("A=D-A"); //FRAME = FRAME - 3
            writeCommandAndNewline("D=M"); //Get value at FRAME - 3
            writeCommandAndNewline("@R2");
            writeCommandAndNewline("M=D");

            //Set LCL = FRAME - 4
            writeCommandAndNewline("@R5");
            writeCommandAndNewline("D=M"); //Get value of FRAME
            writeCommandAndNewline("@4");
            writeCommandAndNewline("A=D-A"); //FRAME = FRAME - 4
            writeCommandAndNewline("D=M"); //Get value at FRAME - 4
            writeCommandAndNewline("@R1");
            writeCommandAndNewline("M=D");

            //GOTO Return address
            writeCommandAndNewline("@R6"); //get return address
            writeCommandAndNewline("A=M");
            writeCommandAndNewline("0;JMP");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFunction(String functionName, int numArgs) {
        try {
            writeCommandAndNewline("(FUNCTION_" + functionName + ")");
            for (int i = 0; i < numArgs; i++) {
                //write constant 0 to the stack as many times as needed
                writeCommandAndNewline("@0"); //load number in A reg
                writeCommandAndNewline("D=A"); //D = number

                //Load the value of the SP into register A
                writeCommandAndNewline("@R0");
                writeCommandAndNewline("A=M"); //Set A = M[0]

                //Set M[SP] equal to the constant held in the D register
                writeCommandAndNewline("M=D"); //Set M[SP] = D = the constant we want to push on the stack

                incrementStackPointer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
