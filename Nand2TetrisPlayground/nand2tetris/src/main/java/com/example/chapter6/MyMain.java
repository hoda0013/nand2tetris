package com.example.chapter6;

import java.io.BufferedWriter;

public class MyMain {

    private static BufferedWriter bufferedWriter;

    public static void main(String args[]) {
        CommandDecoder commandDecoder = new CommandDecoder();
        MyParser myParser = new MyParser("/Users/bigern/Projects/nand2tetris/projects/06/pong/PongL.asm", commandDecoder);
        String outputFile = "/Users/bigern/Projects/nand2tetris/projects/06/pong/MyPongL.hack";
        MyAssembler myAssembler = new MyAssembler(outputFile, myParser, commandDecoder);
        myAssembler.assemble();
    }
}