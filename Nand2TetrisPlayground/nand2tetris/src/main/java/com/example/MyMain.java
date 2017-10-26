package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyMain {

    private static BufferedWriter bufferedWriter;

    public static void main(String args[]) {
        MyScanner myScanner = new MyScanner("/Users/bigern/Projects/nand2tetris/projects/06/add/Add.asm");
        MyTokenizer myTokenizer = new MyTokenizer(myScanner);
        CommandDecoder commandDecoder = new CommandDecoder();
        MyToken myToken;
        File file = new File("/Users/bigern/Projects/nand2tetris/projects/06/output.hack");
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));

            while ((myToken = myTokenizer.getToken()) != null) {
                System.out.println(myToken.toString());
                String output = "";
                switch (myToken.mToken) {
                    case COMMENT:
                        throw new RuntimeException("no comments allowed");
                    case L_COMMAND:

                        break;
                    case C_COMMAND:
                        String cCommand = myToken.getCargo();
                        output = commandDecoder.decodeCCommand(cCommand);
                        break;

                    case A_COMMAND:
                        //strip @ symbol and turn remaining number into a binary number
                        String aCommand = myToken.getCargo();
                        output = commandDecoder.decodeACommand(aCommand);
                        break;

                    default:
                        throw new RuntimeException("token " + myToken.mToken.name() + " not recognized");
                }

                bufferedWriter.append(output);
                bufferedWriter.append('\n');
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}