package com.example.chapter6;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DJH on 11/2/17.
 */

public class MyAssembler {

    private MyParser mMyParser;
    private CommandDecoder mCommandDecoder;
    private static BufferedWriter bufferedWriter;

    File file = new File("/Users/bigern/Projects/nand2tetris/projects/06/output.hack");

    public MyAssembler(MyParser myParser, CommandDecoder commandDecoder) {
        mMyParser = myParser;
        mCommandDecoder = commandDecoder;

    }

    public void assemble() {
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));

            //First pass, find all A an C commands and increase rom counter by one when encoutered
            //L commands get an entry in the symbol table <symbol, rom address of next A or C command>
            long romCounter = 0;
            Map<String, Long> symbolMap = new HashMap<>();
            while (mMyParser.hasMoreCommands()) {
                try {
                    mMyParser.advance();
                    switch (mMyParser.commandType()) {
                        case A_COMMAND:
                        case C_COMMAND:
                            romCounter++;
                            break;

                        case L_COMMAND:
                            symbolMap.put(mMyParser.symbol(), romCounter);
                            break;

                        default:
                            throw new RuntimeException("Unrecognized type: " + mMyParser.commandType().name());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long ramCounter = 16;

            String output = null;

            mMyParser.rewind();
            while (mMyParser.hasMoreCommands()) {
                try {
                    mMyParser.advance();
                    switch (mMyParser.commandType()) {
                        case A_COMMAND:
                            String symbol = mMyParser.symbol();
                            symbol = mCommandDecoder.decodeACommand(symbol);
                            if (symbol.matches("-?\\d+(\\.\\d+)?")) {
                                //symbol is a number
                                output = symbol;
                            } else {
                                if (symbolMap.containsKey(symbol)) {
                                    output = String.format("%016d", Long.valueOf(Long.toBinaryString(symbolMap.get(symbol))));
                                } else {
                                    symbolMap.put(symbol, ramCounter);
                                    output = String.format("%016d", Long.valueOf(Long.toBinaryString(ramCounter)));
                                    ramCounter++;
                                }
                            }
                            bufferedWriter.append(output);
                            bufferedWriter.append('\n');
                            break;

                        case C_COMMAND:
                            output = mMyParser.binaryEncodedCCommand();
                            bufferedWriter.append(output);
                            bufferedWriter.append('\n');
                            break;

                        case L_COMMAND:
//                            output = String.format("%016d", Integer.valueOf(Long.toBinaryString(symbolMap.get(mMyParser.symbol()))));
                            break;

                        default:
                            throw new RuntimeException("Unrecognized type: " + mMyParser.commandType().name());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                bufferedWriter.append(output);
//                bufferedWriter.append('\n');
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            bufferedWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
