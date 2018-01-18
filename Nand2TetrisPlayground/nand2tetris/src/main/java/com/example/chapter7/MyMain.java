package com.example.chapter7;

/**
 * Created by DJH on 12/3/17.
 */

public class MyMain {

    public static void main(String args[]) {
        String baseDirectory = "/Users/bigern/Projects/nand2tetris/projects/08/FunctionCalls/SimpleFunction/SimpleFunction";
        String inputFilename = baseDirectory + ".vm";
        String outputFilename = baseDirectory + ".asm";
        MyVmTranslator vmTranslator = new MyVmTranslator(inputFilename, outputFilename);
        vmTranslator.translate();
    }
}
