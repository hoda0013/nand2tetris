package com.example;

/**
 * Created by DJH on 10/25/17.
 */

public class CommandDecoder {

    public String decodeCCommand(String cCommand) {
        String decodedCCommand;
        if (cCommand.contains(";")) {
            //Is a command of the form comp;jump
            String comp = cCommand.substring(0, cCommand.indexOf(';'));
            String jump = cCommand.substring(cCommand.indexOf(';') + 1);

            CandA candA = compToCAndA(comp);
            String jumpNumber = jumpToJumpNumber(jump);
            decodedCCommand = "111" + candA.getA() + candA.getC() + "000" + jumpNumber;
        } else {
            //Is dest=comp
            String dest = cCommand.substring(0, cCommand.indexOf('='));
            String comp = cCommand.substring(cCommand.indexOf('=') + 1);

            CandA candA = compToCAndA(comp);
            String destNumber = destToDestNumber(dest);
            decodedCCommand = "111" + candA.getA() + candA.getC() + destNumber + "000";
        }

        return decodedCCommand;
    }

    public String decodeACommand(String aCommand) {
        aCommand = aCommand.trim();
        aCommand = aCommand.replaceAll("@", "");
        Integer aNumber;
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

        return String.format("%016d", Integer.valueOf(Integer.toBinaryString(aNumber)));
    }

    private CandA compToCAndA(String comp) {
        String cNumber;
        String a = "0";
        switch (comp) {
            case "0":
                cNumber = "101010";
                break;

            case "1":
                cNumber = "111111";
                break;

            case "-1":
                cNumber = "111010";
                break;

            case "D":
                cNumber = "001100";
                break;

            case "A":
                cNumber = "110000";
                break;

            case "!D":
                cNumber = "001101";
                break;

            case "!A":
                cNumber = "110001";
                break;

            case "D+1":
                cNumber = "011111";
                break;

            case "A+1":
                cNumber = "110111";
                break;

            case "D-1":
                cNumber = "001110";
                break;

            case "A-1":
                cNumber = "110010";
                break;

            case "D+A":
                cNumber = "000010";
                break;

            case "D-A":
                cNumber = "010011";
                break;

            case "A-D":
                cNumber = "000111";
                break;

            case "D&A":
                cNumber = "000000";
                break;

            case "D|A":
                cNumber = "010101";
                break;

            case "M":
                cNumber = "110000";
                a = "1";
                break;

            case "!M":
                cNumber = "110001";
                a = "1";
                break;

            case "-M":
                cNumber = "110011";
                a = "1";
                break;

            case "M+1":
                cNumber = "110111";
                a = "1";
                break;

            case "M-1":
                cNumber = "110010";
                a = "1";
                break;

            case "D+M":
                cNumber = "000010";
                a = "1";
                break;

            case "D-M":
                cNumber = "010011";
                a = "1";
                break;

            case "M-D":
                cNumber = "000111";
                a = "1";
                break;

            case "D&M":
                cNumber = "000000";
                a = "1";
                break;

            case "D|M":
                cNumber = "010101";
                a = "1";
                break;
            default:
                throw new RuntimeException("Can't recognize comp command");
        }

        return new CandA(a, cNumber);
    }

    private String jumpToJumpNumber(String jumpPart) {
        String jumpNumber;

        switch (jumpPart) {
            case "null":
                jumpNumber = "000";
                break;

            case "JGT":
                jumpNumber = "001";
                break;

            case "JEQ":
                jumpNumber = "010";
                break;

            case "JGE":
                jumpNumber = "011";
                break;

            case "JLT":
                jumpNumber = "100";
                break;

            case "JNE":
                jumpNumber = "101";
                break;

            case "JLE":
                jumpNumber = "110";
                break;

            case "JMP":
                jumpNumber = "111";
                break;

            default:
                throw new RuntimeException("Jump command not recognized");
        }

        return jumpNumber;
    }

    private String destToDestNumber(String destPart) {
        String destNumber;

        switch (destPart) {
            case "null":
                destNumber = "000";
                break;

            case "M":
                destNumber = "001";
                break;

            case "D":
                destNumber = "010";
                break;

            case "MD":
                destNumber = "011";
                break;

            case "A":
                destNumber = "100";
                break;

            case "AM":
                destNumber = "101";
                break;

            case "AD":
                destNumber = "110";
                break;

            case "AMD":
                destNumber = "111";
                break;

            default:
                throw new RuntimeException("dest not recognized");
        }

        return destNumber;
    }

    class CandA {
        String a;
        String c;

        public CandA(String a, String c) {
            this.a = a;
            this.c = c;
        }

        public String getA() {
            return a;
        }

        public String getC() {
            return c;
        }
    }

}
