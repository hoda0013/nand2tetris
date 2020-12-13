package com.example.chapter10

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern


class Parser {
    private var tokens: List<String> = emptyList()
    private lateinit var currentToken: String
    private var tokenPointer: Int = 0
    private var outputFile: File = File("parser_output.txt")
    private var bufferedWriter = BufferedWriter(FileWriter(outputFile))
    private var numTabs = 0

    private fun getNextToken(): String? {
        tokenPointer++
        currentToken = tokens[tokenPointer]
        return currentToken
    }

    fun setTokens(tokens: List<String>) {
        this.tokens = tokens
        currentToken = tokens[tokenPointer]
    }

    fun parse() {
        // Assume we are always parsing a class
        parseClass()
        bufferedWriter.close()
    }

    private fun indent() {
        numTabs++
    }

    private fun unIndent() {
        if (numTabs == 0) {
            throw Exception("Can't unindent any more")
        }
        numTabs--
    }

    // class is a non-terminal
    private fun parseClass() {
        if (currentToken.equals(Keyword.CLASS.value, true)) {
            // Print open class tag <class>
            bufferedWriter.append("<class>\n")

            indent()

            KeywordObject.parse(currentToken, bufferedWriter, numTabs)

            getNextToken()

            parseClassName()
            getNextToken()

            Symbol.parse('{', currentToken, bufferedWriter, numTabs)
            getNextToken()

            // TODO Parse 0 to N classVarDec

            while(ClassVarDec.isTokenAClassifier(currentToken)) {
                parseClassVarDec()
            }

            // Print class closing tag
            unIndent()
            bufferedWriter.append("</class>\n")
        } else {
            throwException()
        }
    }

    private fun parseClassVarDec() {
        bufferedWriter.append("${Tag.makeOpenTag(Category.CLASS_VAR_DEC.value, numTabs)}\n")

        indent()
        if (currentToken == Keyword.STATIC.value || currentToken == Keyword.FIELD.value) {
            KeywordObject.parse(currentToken, bufferedWriter, numTabs)
            getNextToken()
        } else {
            throwException()
        }

        parseType()
        getNextToken()

        parseVarName()
        getNextToken()

        while(currentToken.equals(",")) {
            Symbol.parse(',', currentToken, bufferedWriter, numTabs)
            getNextToken()
            parseVarName()
            getNextToken()
        }

        Symbol.parse(';', currentToken, bufferedWriter, numTabs)
        // parse semicolon symbol

        unIndent()
        bufferedWriter.append(Tag.makeCloseTag(Category.CLASS_VAR_DEC.value, numTabs))
    }

    private fun parseVarName() {
        Identifier.parse(currentToken, bufferedWriter, numTabs)
    }

    private fun parseType() {
        if (Keyword.INT.value.equals(currentToken)
                || Keyword.CHAR.value.equals(currentToken)
                || Keyword.BOOLEAN.value.equals(currentToken)
                || Identifier.isValid(currentToken)) {
                    Identifier.parse(currentToken, bufferedWriter, numTabs)
        } else {
            throwException()
        }
    }

    object ClassVarDec {

        fun isTokenAClassifier(token: String): Boolean {
            return token == Keyword.STATIC.value
                    || token == Keyword.FIELD.value
        }
    }

    private fun parseClassName() {
        Identifier.parse(currentToken, bufferedWriter, numTabs)
    }

    private fun throwException() {
        throw Exception("Error parsing token: $currentToken at tokenIndex: $tokenPointer")
    }

    object Symbol {
        private const val regex = "[\\{\\}\\(\\)\\[\\]\\.\\,\\;\\+\\-\\*\\/\\&\\|<>=_]"
        private val pattern: Pattern

        init {
            pattern = Pattern.compile(regex)
        }

        fun isValid(token: String): Boolean {
            return pattern.matcher(token).matches()
        }

        fun parse(expectedSymbol: Char, tokenSymbol: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            if (isValid(tokenSymbol) && expectedSymbol.toString() == tokenSymbol) {
                printTag(tokenSymbol, bufferedWriter, numTabs)
            } else {
                throw Exception()
            }
        }

        private fun printTag(token: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            bufferedWriter.append(Tag.makeOneliner(Category.SYMBOL.name.toLowerCase(), token, numTabs))
        }
    }

    object Identifier {
        private const val regex = "[a-zA-Z_][a-zA-Z0-9_]*"
        private val pattern: Pattern

        init {
            pattern = Pattern.compile(regex)
        }

        fun isValid(token: String): Boolean {
            return Pattern.compile(regex).matcher(token).matches()
        }

        fun parse(token: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            if (isValid(token)) {
                printTag(token, bufferedWriter, numTabs)
            } else {
                throw Exception("token: $token not a valid Identifier")
            }
        }

        private fun printTag(token: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            bufferedWriter.append(Tag.makeOneliner(Category.IDENTIFIER.name.toLowerCase(), token, numTabs))
        }
    }

    object KeywordObject {

        fun isValid(token: String): Boolean {
            return Keyword.values()
                    .map { it.value}
                    .contains(token)
        }

        fun parse(token: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            if (isValid(token)) {
                printTag(token, bufferedWriter, numTabs)
            } else {
                throw Exception()
            }
        }

        private fun printTag(token: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            bufferedWriter.append(Tag.makeOneliner(Category.KEYWORD.name.toLowerCase(), token, numTabs))
        }
    }

    enum class Category(val value: String) {
        KEYWORD("keyword"),
        SYMBOL("symbol"),
        INTEGER_CONSTANT("integerConstant"),
        STRING_CONSTANT("stringConstant"),
        IDENTIFIER("identifier"),
        CLASS("class"),
        CLASS_VAR_DEC("classVarDec"),
        TYPE("type"),
        SUBROUTINE_DEC("subroutineDec"),
        PARAMETER_LIST("parameterList"),
        SUBROUTINE_BODY("subroutineBody"),
        VAR_DEC("varDec"),
        CLASS_NAME("className"),
        SUBROUTINE_NAME("subroutineName"),
        VAR_NAME("varName"),
        STATEMENTS("statements"),
        STATEMENT("statement"),
        LET_STATEMENT("letStatement"),
        IF_STATEMENT("ifStatement"),
        WHILE_STATEMENT("whileStatement"),
        DO_STATEMENT("doStatement"),
        RETURN_STATEMENT("ReturnStatement"),
        EXPRESSION("expression"),
        TERM("term"),
        SUBROUTINE_CALL("subroutinCall"),
        EXPRESSION_LIST("expressionList"),
        OP("op"),
        UNARY_OP("unaryOp"),
        KEYWORD_CONSTANT("KeywordConstant");

    }

    enum class Keyword(val value: String) {
        CLASS("class"),
        CONSTRUCTOR("constructor"),
        FUNCTION("function"),
        METHOD("method"),
        FIELD("field"),
        STATIC("static"),
        VAR("var"),
        INT("int"),
        CHAR("char"),
        BOOLEAN("boolean"),
        VOID("void"),
        TRUE("true"),
        FALSE("false"),
        NULL("null"),
        THIS("this"),
        LET("let"),
        DO("do"),
        IF("if"),
        ELSE("else"),
        WHILE("while"),
        RETURN("return");

        fun isTokenAKeyword(token: String): Boolean = values()
                .map { it.value.toLowerCase() }
                .contains(token.toLowerCase())

        fun parse(token: String, bufferedWriter: BufferedWriter) {
            if (isTokenAKeyword(token)) {
                bufferedWriter.append("")
            } else {
                throw Exception()
            }
        }
    }

    object Tag {
        fun makeOneliner(tagName: String, value: String, numTabs: Int): String {
            return makeTabs(numTabs).plus("<$tagName> $value ${makeCloseTag(tagName, 0)}")
        }

        fun makeOpenTag(tagName: String, numTabs: Int) : String {
            var output = ""
            for(i in 0 until numTabs) {
                output = output.plus("\t")
            }
            return makeTabs(numTabs).plus("<$tagName> ")
        }
        fun makeCloseTag(value: String, numTabs: Int) : String {
            return makeTabs(numTabs).plus("</$value>\n")
        }

        private fun makeTabs(numTabs: Int): String {
            var output = ""
            for(i in 0 until numTabs) {
                output = output.plus("\t")
            }
            return output
        }
    }
}