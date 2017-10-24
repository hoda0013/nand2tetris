package com.example;

public class MyMain {
    public static void main(String args[]) {
        MyScanner myScanner = new MyScanner("/Users/bigern/Projects/nand2tetris/projects/06/max/Max.asm");
        MyTokenizer myTokenizer = new MyTokenizer(myScanner);
        MyToken myToken;
        while ((myToken = myTokenizer.getToken()) != null) {
            System.out.println(myToken.toString());
        }
    }
}