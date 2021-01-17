package com.example.chapter10

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class Tokenizer {

    private lateinit var outputFile: File
    private lateinit var bufferedWriter: BufferedWriter
    private lateinit var bufferedReader: BufferedReader
    private lateinit var inputFile: File

    private var tokenState: TokenState = TokenState.NORMAL

    fun initialize(fileName: String) {
        inputFile = File(fileName)
        outputFile = File(fileName.plus("T.xml"))
        bufferedWriter = BufferedWriter(FileWriter(outputFile))
        bufferedReader = BufferedReader(FileReader(inputFile))
    }

    fun tokenize() {
        // Read line in
        var line = bufferedReader.readLine()

        bufferedWriter.append("<tokens>\n")

        while (line != null) {
            if (tokenState == TokenState.COMMENT) {
                // Does line contain the comment closing pattern?
                if (line.contains("*/")) {
                    // We're in a comment and this line contains the closing
                    tokenState = TokenState.NORMAL
                    line = bufferedReader.readLine()
                    continue
                } else {
                    // We're in a multi line comment and this line doesn't contain the comment terminator
                    line = bufferedReader.readLine()
                    continue
                }
            } else {
                if (line.contains("/*") || line.contains("/**")) {
                    if (line.contains("*/")) {
                        // This is a multi-line comment that is only using one line
                        line = bufferedReader.readLine()
                        continue
                    } else {
                        // This is a multi line comment spread over multiple lines
                        tokenState = TokenState.COMMENT

                        line = bufferedReader.readLine()
                        continue
                    }
                } else if (line.contains("//")) {
                    // This line contains a single line comment, strip out the comment part of the line the process like normal
                    line = line.substringBefore("//")
                }

                // line has now been stripped of any comments

                var tempToken = ""
                var isStringLiteral = false

                // Iterate over each character in the line and split into tokens
                line.toCharArray().forEach {
                    if (it.isWhitespace() && !isStringLiteral) {
                        printToken(tempToken)
                        tempToken = ""
                    } else if (isSymbol(it.toString()) && !isStringLiteral) {
                        // a symbol can be next to an idnetifier or keyword so we need to process the existing token and the symbol is some cases
                        printToken(tempToken)
                        tempToken = ""

                        Token(TokenType.SYMBOL, it.toString()).printTag(bufferedWriter)
                    } else if (it == '"') {
                        if (isStringLiteral) {
                            // This is the closing quotation mark
                            isStringLiteral = false
                            Token(TokenType.STRING, tempToken).printTag(bufferedWriter)
                            tempToken = ""
                        } else {
                            // This is the opening quotation mark
                            isStringLiteral = true
                        }
                    } else {
                        tempToken += it
                    }
                }
            }

            line = bufferedReader.readLine()
        }

        bufferedWriter.append("</tokens>")
        bufferedWriter.close()
    }

    private fun printToken(token: String) {
        if (token.isNotEmpty()) {
            when {
                isKeyword(token) -> {
                    Token(TokenType.KEYWORD, token).printTag(bufferedWriter)
                }
                isSymbol(token) -> {
                    Token(TokenType.SYMBOL, token).printTag(bufferedWriter)
                }
                isIntConstant(token) -> {
                    Token(TokenType.INTEGER, token).printTag(bufferedWriter)
                }
                else -> {
                    Token(TokenType.IDENTIFIER, token).printTag(bufferedWriter)
                }
            }
        }
    }

    private fun isKeyword(value: String): Boolean {
        return Parser.Keyword.values().map{ it.value }.contains(value)
    }

    private fun isSymbol(value: String): Boolean {
        return "{}()[].,;+-*/&|<>=~".contains(value)
    }

    private fun isIntConstant(value: String): Boolean {
        return try {
            value.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    enum class TokenState {
        NORMAL,
        COMMENT;
    }

    enum class TokenType(val value: String) {
        KEYWORD("keyword"),
        SYMBOL("symbol"),
        INTEGER("integerConstant"),
        STRING("stringConstant"),
        IDENTIFIER("identifier");
    }

    data class Token(val type: TokenType, val value: String) {
        fun printTag(bufferedWriter: BufferedWriter) {
            val printValue = if(type == TokenType.SYMBOL) {
                when(value) {
                    "<" -> {"&lt;"}
                    ">" -> {"&gt;"}
                    "\"" -> {"&quote;"}
                    "&" -> {"&amp;"}
                    else -> { value }
                }
            } else {
                value
            }
            bufferedWriter.append("<${type.value}> $printValue </${type.value}>\n")
        }
    }

}