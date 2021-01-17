package com.example.chapter10

import com.sun.org.apache.xpath.internal.operations.Bool
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.NumberFormatException
import java.util.regex.Pattern


class Tokenizer {

    private lateinit var outputFile: File
    private lateinit var bufferedWriter: BufferedWriter
    private lateinit var bufferedReader: BufferedReader
    private lateinit var inputFile: File

    private val whiteSpaceRegex = Pattern.compile("[\\s]+")

    fun initialize(fileName: String){
        inputFile = File(fileName)
        outputFile = File(fileName.plus("T.xml"))
        bufferedWriter = BufferedWriter(FileWriter(outputFile))
        bufferedReader = BufferedReader(FileReader(inputFile))
    }

    private val tokensSplitOnWhitespaceAndStrippedOfComments = ArrayList<String>()

   enum class TokenState {
       NORMAL,
       COMMENT,
       STRING_LITERAL;
   }

    private var tokenState: TokenState = TokenState.NORMAL
    private var tokens = ArrayList<String>()

    enum class TokenType(val value: String) {
        KEYWORD("keyword"),
        SYMBOL("symbol"),
        INTEGER("integer"),
        STRING("string"),
        IDENTIFIER("identifier");
    }

    data class Token(val type: TokenType, val value: String) {
        fun printTag(bufferedWriter: BufferedWriter) {
            bufferedWriter.append("<${type.value}> $value </${type.value}>\n")
        }
    }

    fun tokenize2() {
        // Read line in
        var line = bufferedReader.readLine()

        bufferedWriter.append("<tokens>\n")
        while(line != null) {
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
                    }  else {
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

                line.toCharArray().forEach {
                    if (it.isWhitespace() && !isStringLiteral) {
                        // tempToken can be processed if it's not empty
                        if (tempToken.isNotEmpty()) {
                           when {
                               isKeyword(tempToken) -> {
                                   Token(TokenType.KEYWORD, tempToken).printTag(bufferedWriter)
                               }
                               isSymbol(tempToken) -> {
                                   Token(TokenType.SYMBOL, tempToken).printTag(bufferedWriter)
                               }
                               isIntConstant(tempToken) -> {
                                   Token(TokenType.INTEGER, tempToken).printTag(bufferedWriter)
                               }
                               else -> {
                                   Token(TokenType.IDENTIFIER, tempToken).printTag(bufferedWriter)
                               }
                           }

                            tempToken = ""
                        }

                    } else if (isSymbol(it.toString()) && !isStringLiteral) {
                        // a symbol can be next to an idnetifier or keyword so we need to process the existing token and the symbol is some cases
                        if (tempToken.isNotEmpty()) {
                            when {
                                isKeyword(tempToken) -> {
                                    Token(TokenType.KEYWORD, tempToken).printTag(bufferedWriter)
                                }
                                isSymbol(tempToken) -> {
                                    Token(TokenType.SYMBOL, tempToken).printTag(bufferedWriter)
                                }
                                isIntConstant(tempToken) -> {
                                    Token(TokenType.INTEGER, tempToken).printTag(bufferedWriter)
                                }
                                else -> {
                                    Token(TokenType.IDENTIFIER, tempToken).printTag(bufferedWriter)
                                }
                            }

                            tempToken = ""
                        }

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

    private fun isKeyword(value: String): Boolean {
        return Parser.Keyword.values().contains(value)
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


    fun tokenize() {
        // go through program line by line
        // for each line, break into tokens based on whitespace
        // categorize each token and print it to file
       var r = bufferedReader.read()

        var line = bufferedReader.readLine()

        var isInMultiLineComment = false

        while (line != null) {
            var tokens = line.split(
                    Regex("((?<=[;\\(\\)\\{\\}\\[\\]\\.\\,\\+\\-\\*\\/&\\|\\<\\>=~])|(?=[;\\(\\)\\{\\}\\[\\]\\.\\,\\+\\-\\*\\/&\\|\\<\\>=~]))")
            )
                    .mapNotNull { token ->
                        if (token == "//" && !isInMultiLineComment) {
                            // if we see this, and it's not contained in another multi-line comment  we can dispose of the rest of this line
                            // break out of for loop, go to next line and continue processing
                            null
                        } else if (token == "/*" || token == "/**") {
                            isInMultiLineComment = true
                            null
                            // discard this token and every other token until we see a */
                        } else if (token == "*/") {
                            // end comment
                            isInMultiLineComment = false
                            null
                        } else if (!isInMultiLineComment) {
                            if (token.isNotEmpty()) {
                                token
//                                tokensSplitOnWhitespaceAndStrippedOfComments.add(token)
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
            tokens
        }

//        var isInMultiLineComment = false
//        while(line != null) {
//        tokensSplitOnWhitespaceAndStrippedOfComments.addAll(
//            line
//                    .split(whiteSpaceRegex)
//                    .mapNotNull { token ->
//                        if (token == "//" && !isInMultiLineComment) {
//                            // if we see this, and it's not contained in another multi-line comment  we can dispose of the rest of this line
//                            // break out of for loop, go to next line and continue processing
//                            null
//                        } else if (token == "/*" || token == "/**") {
//                            isInMultiLineComment = true
//                            null
//                            // discard this token and every other token until we see a */
//                        } else if (token == "*/") {
//                            // end comment
//                            isInMultiLineComment = false
//                            null
//                        } else if (!isInMultiLineComment) {
//                            if (token.isNotEmpty()) {
//                                token
////                                tokensSplitOnWhitespaceAndStrippedOfComments.add(token)
//                            } else {
//                                null
//                            }
//                        } else {
//                            null
//                        }
//                    }
//        )
//            // Split at all whitespace
//            val splitOnWhitespace = line.split(whiteSpaceRegex)
//
//            for(i in splitOnWhitespace.indices) {
//                val token = splitOnWhitespace[i]
//
//                if (token == "//" && !isInMultiLineComment) {
//                    // if we see this, and it's not contained in another multi-line comment  we can dispose of the rest of this line
//                    // break out of for loop, go to next line and continue processing
//                    break
//                } else if (token == "/*" || token == "/**") {
//                    isInMultiLineComment = true
//                    // discard this token and every other token until we see a */
//                } else if (token == "*/") {
//                    // end comment
//                    isInMultiLineComment = false
//                } else if (!isInMultiLineComment) {
//                    if (token.isNotEmpty()) {
//                        tokensSplitOnWhitespaceAndStrippedOfComments.add(token)
//                    }
//                }
//            }

            line = bufferedReader.readLine()
//        }

//        var isInStringConstant = false
//        var stringCollector = ""
//        for (i in 0 until tokensSplitOnWhitespaceAndStrippedOfComments.size) {
//
//            val token = tokensSplitOnWhitespaceAndStrippedOfComments[i]
//
//            if (token == "\"" && isInStringConstant) {
//                isInStringConstant = false
//            } else if (token == "\"" && !isInStringConstant) {
//                isInStringConstant = true
//                // print string tag
//            } else if (isInStringConstant) {
//                stringCollector.plus("$token ")
//            } else {
//                stringCollector += token
//
//                // is token a keyword?
//
//                // is token a symbol?
//
//                // is token an integer?
//
//                // is token an identifier?
//
//            }
//        }

//        val tokens = ArrayList<String>()
//
//        tokensSplitOnWhitespaceAndStrippedOfComments.forEach {
//            val splitted = it.split(
//                    Regex("((?<=[;\\(\\)\\{\\}\\[\\]\\.\\,\\+\\-\\*\\/&\\|\\<\\>=~])|(?=[;\\(\\)\\{\\}\\[\\]\\.\\,\\+\\-\\*\\/&\\|\\<\\>=~]))")
//            )
//            splitted.filter { it.isNotEmpty() }.forEach { tokens.add(it) }
//            System.out.println(splitted.toString())
//        }
//
//        tokens.forEach {
//            bufferedWriter.append("$it\n")
//        }

        bufferedWriter.close()
    }
}