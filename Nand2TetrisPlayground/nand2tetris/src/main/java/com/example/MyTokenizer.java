package com.example;

/**
 * Created by DJH on 10/17/17.
 */

public class MyTokenizer {
    //rules: Comments start with // and are ignored. Some comments are at the beginning of a line, others may be on the same line as a command
    //One command per line
    //Commands start with an @, A,D or M and end with a whitespace
    //TokenTypes: COMMENT, L COMMAND (XXX), C COMMAND, A COMMAND

    private MyScanner mScanner;

    public MyTokenizer(MyScanner scanner) {
        mScanner = scanner;
    }

    public MyToken getToken() {
        MyCharacter currentCharacter;
        MyToken.Token currentToken = MyToken.Token.NONE;
        String tokenText = "";

        while ((currentCharacter = mScanner.get()) != null) {
            char thisChar = currentCharacter.getCargo();
            if (currentToken == MyToken.Token.NONE) {
                if (thisChar == '/') {
                    currentToken = MyToken.Token.COMMENT;
                    tokenText += thisChar;
                    //ignore comments
                } else if (thisChar == '(') {
                    currentToken = MyToken.Token.L_COMMAND;
                    tokenText += thisChar;
                } else if (thisChar == '@') {
                    currentToken = MyToken.Token.A_COMMAND;
                    tokenText += thisChar;
                } else if (thisChar == ' '
                        || thisChar == '\n'
                        || thisChar == '\t'
                        || thisChar == '\r') {
                    //ignore whitespaces
                } else {
                    //TODO: Could probably be an else if specifically looking for valid values that a C command starts with and then the else case would throw an exception
                    currentToken = MyToken.Token.C_COMMAND;
                    tokenText += thisChar;
                }
            } else {
                if (currentToken == MyToken.Token.COMMENT) {
                    //Comments end on a newline
                    if (thisChar == '\n'
                            || thisChar == '\r') {
                        tokenText = "";
                        currentToken = MyToken.Token.NONE;
//                        return new MyToken(tokenText, currentToken, 0, 0);
                    }
                    tokenText += thisChar;
                } else if (currentToken == MyToken.Token.L_COMMAND
                        || currentToken == MyToken.Token.A_COMMAND
                        || currentToken == MyToken.Token.C_COMMAND) {
                    //These commands end on any whitespace characters
                    if (thisChar == ' '
                            || thisChar == '\n'
                            || thisChar == '\t'
                            || thisChar == '\r') {
                        return new MyToken(tokenText, currentToken, 0, 0);
                    }
                    tokenText += thisChar;
                } else {
                    throw new RuntimeException("token not recognized");
                }
            }
        }

        return null;
    }
}
