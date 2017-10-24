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
        MyToken myToken;
        File file = new File("/Users/bigern/Projects/nand2tetris/projects/06/output.hack");
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while ((myToken = myTokenizer.getToken()) != null) {
            System.out.println(myToken.toString());
            String output = "";
            try {
                switch (myToken.mToken) {
                    case COMMENT:
                        throw new RuntimeException("no comments allowed");
                    case L_COMMAND:

                        break;
                    case C_COMMAND:
                        String cCommand = myToken.getCargo();
                        Integer cNumber = 0;
                        if (cCommand.contains(";")) {
                            //Is comp;jump
                            String comp = cCommand.substring(0, cCommand.indexOf(';'));
                            String jump = cCommand.substring(cCommand.indexOf(';' + 1));
                            Integer a = 0;
                            switch (comp) {
                                case "0":
                                    cNumber = 101010;
                                    break;

                                case "1":
                                    cNumber = 111111;
                                    break;

                                case "-1":
                                    cNumber = 111010;
                                    break;

                                case "D":
                                    cNumber = 001100;
                                    break;

                                case "A":
                                    cNumber = 110000;
                                    break;

                                case "!D":
                                    cNumber = 001101;
                                    break;

                                case "!A":
                                    cNumber = 110001;
                                    break;

                                case "D+1":
                                    cNumber = 011111;
                                    break;

                                case "A+1":
                                    cNumber = 110111;
                                    break;

                                case "D-1":
                                    cNumber = 001110;
                                    break;

                                case "A-1":
                                    cNumber = 110010;
                                    break;

                                case "D+A":
                                    cNumber = 000010;
                                    break;

                                case "D-A":
                                    cNumber = 010011;
                                    break;

                                case "A-D":
                                    cNumber = 000111;
                                    break;

                                case "D&A":
                                    cNumber = 000000;
                                    break;

                                case "D|A":
                                    cNumber = 010101;
                                    break;

                                case "M":
                                    cNumber = 110000;
                                    a = 1;
                                    break;

                                case "!M":
                                    cNumber = 110001;
                                    a = 1;
                                    break;

                                case "-M":
                                    cNumber = 110011;
                                    a = 1;
                                    break;

                                case "M+1":
                                    cNumber = 110111;
                                    a = 1;
                                    break;

                                case "M-1":
                                    cNumber = 110010;
                                    a = 1;
                                    break;

                                case "D+M":
                                    cNumber = 000010;
                                    a = 1;
                                    break;

                                case "D-M":
                                    cNumber = 010011;
                                    a = 1;
                                    break;

                                case "M-D":
                                    cNumber = 000111;
                                    a = 1;
                                    break;

                                case "D&M":
                                    cNumber = 000000;
                                    a = 1;
                                    break;

                                case "D|M":
                                    cNumber = 010101;
                                    a = 1;
                                    break;
                            }

                            Integer jumpNumber = 0;

                            switch (jump) {
                                case "null":
                                    jumpNumber = 000;
                                    break;

                                case "JGT":
                                    jumpNumber = 001;
                                    break;

                                case "JEQ":
                                    jumpNumber = 010;
                                    break;

                                case "JGE":
                                    jumpNumber = 011;
                                    break;

                                case "JLT":
                                    jumpNumber = 100;
                                    break;

                                case "JNE":
                                    jumpNumber = 101;
                                    break;

                                case "JLE":
                                    jumpNumber = 110;
                                    break;

                                case "JMP":
                                    jumpNumber = 111;
                                    break;

                                default:
                                    throw new RuntimeException("Jump command not recognized");
                            }

                            output = "11" + a.toString() + cNumber.toString() + "000" + jumpNumber.toString();
                        } else {
                            //Is dest=comp
                            String dest = cCommand.substring(0, cCommand.indexOf('='));
                            String comp = cCommand.substring(cCommand.indexOf('=') + 1);
                        }
                        break;

                    case A_COMMAND:
                        //strip @ symbol and turn remaining number into a binary number
                        String aCommand = myToken.getCargo();
                        aCommand = aCommand.trim();
                        aCommand = aCommand.replaceAll("@", "");
                        Integer aNumber = 0;
                        switch (aCommand) {
                            case "SP":
                                aNumber = 0;
                                break;
                            case "LCL":
                                aNumber = 1;
                                break;
                            case "ARG":
                                aNumber = 2;
                                break;
                            case "THIS":
                                aNumber = 3;
                                break;
                            case "THAT":
                                aNumber = 4;
                                break;
                            case "R0":
                                aNumber = 0;
                                break;
                            case "R1":
                                aNumber = 1;
                                break;
                            case "R2":
                                aNumber = 2;
                                break;
                            case "R3":
                                aNumber = 3;
                                break;
                            case "R4":
                                aNumber = 4;
                                break;
                            case "R5":
                                aNumber = 5;
                                break;
                            case "R6":
                                aNumber = 6;
                                break;
                            case "R7":
                                aNumber = 7;
                                break;
                            case "R8":
                                aNumber = 8;
                                break;
                            case "R9":
                                aNumber = 9;
                                break;
                            case "R10":
                                aNumber = 10;
                                break;
                            case "R11":
                                aNumber = 11;
                                break;
                            case "R12":
                                aNumber = 12;
                                break;
                            case "R13":
                                aNumber = 13;
                                break;
                            case "R14":
                                aNumber = 14;
                                break;
                            case "R15":
                                aNumber = 15;
                                break;
                            case "SCREEN":
                                aNumber = 16384;
                                break;
                            case "KBD":
                                aNumber = 24576;
                                break;
                            default:
                                aNumber = Integer.valueOf(aCommand);
                        }

                        output = String.format("%016d", Integer.parseInt(Integer.toBinaryString(aNumber)));
                        break;

                    default:
                        throw new RuntimeException("token " + myToken.mToken.name() + " not recognized");
                }

                bufferedWriter.append(output);
                bufferedWriter.append('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}