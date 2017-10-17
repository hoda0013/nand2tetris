package com.example;

import java.io.IOException;

public class MyMain {
    public static void main(String args[]) {
        MyParser myParser = new MyParser("/Users/bigern/Projects/nand2tetris/projects/06/max/Max.asm");
        while (myParser.hasMoreCommands()) {
            try {
                myParser.advance();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
