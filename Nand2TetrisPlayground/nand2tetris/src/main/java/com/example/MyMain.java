package com.example;

public class MyMain {
    public static void main(String args[]) {
//        MyParser myParser = new MyParser("/Users/bigern/Projects/nand2tetris/projects/06/max/Max.asm");
//        while (myParser.hasMoreCommands()) {
//            try {
//                myParser.advance();
//            } catch (IOException e) {
//                e.printStackTrace();
//                break;
//            }
//        }

        MyScanner myScanner = new MyScanner("/Users/bigern/Projects/nand2tetris/projects/06/max/Max.asm");
        MyCharacter character;
        while((character = myScanner.get()) != null)
            System.out.println(character.toString());
        }

}
