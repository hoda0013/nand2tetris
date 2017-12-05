package com.example.chapter7;

/**
 * Created by DJH on 12/3/17.
 */

public class MyMain {

    public static void main(String args[]) {
        String inputFilename = "/Users/bigern/Projects/nand2tetris/projects/07/StackArithmetic/SimpleAdd/SimpleAdd.vm";
        String outputFilename = "/Users/bigern/Projects/nand2tetris/projects/07/StackArithmetic/SimpleAdd/SimpleAdd.asm";
        MyVmTranslator vmTranslator = new MyVmTranslator(inputFilename, outputFilename);
        vmTranslator.translate();
    }
}
