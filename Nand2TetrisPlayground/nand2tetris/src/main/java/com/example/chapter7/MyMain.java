package com.example.chapter7;

/**
 * Created by DJH on 12/3/17.
 */

public class MyMain {

    public static void main(String args[]) {
        System.out.println("Running...");
        String baseDirectory = "/Users/bigern/Projects/nand2tetris/projects/08/FunctionCalls/FibonacciElement/";
//        String inputFilename = baseDirectory + ".vm";
        String outputFilename = baseDirectory + "FibonacciElement.asm";
        MyVmTranslator vmTranslator = new MyVmTranslator(baseDirectory, outputFilename);
        vmTranslator.translate();
    }
}
