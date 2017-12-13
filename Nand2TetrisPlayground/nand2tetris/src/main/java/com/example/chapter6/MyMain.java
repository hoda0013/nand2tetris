package com.example.chapter6;

import java.io.BufferedWriter;

public class MyMain {

    private static BufferedWriter bufferedWriter;

    public static void main(String args[]) {
        CommandDecoder commandDecoder = new CommandDecoder();
        MyParser myParser = new MyParser("/Users/bigern/Projects/nand2tetris/projects/06/add/Add.asm", commandDecoder);
        MyAssembler myAssembler = new MyAssembler(myParser, commandDecoder);
        myAssembler.assemble();
    }
}