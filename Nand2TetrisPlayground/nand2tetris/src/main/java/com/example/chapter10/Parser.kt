package com.example.chapter10

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.security.Key
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
        try {
            parseClass()
        } catch (e: Exception) {
            bufferedWriter.close()
        }
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

            while(ClassVarDec.isTokenAClassifier(currentToken)) {
                parseClassVarDec()
            }

            while(SubroutinDec.isTokenAClassifier(currentToken)) {
                parseSubroutineDec()
            }

            // Print class closing tag
            unIndent()
            bufferedWriter.append("</class>\n")
        } else {
            throwException()
        }
    }

    private fun parseSubroutineDec() {
        bufferedWriter.append("${Tag.makeOpenTag(Category.SUBROUTINE_DEC.value, numTabs)}\n")
        indent()

        // first token must be a "constructor" "function" or "method"
        if (currentToken == Keyword.CONSTRUCTOR.value
                || currentToken == Keyword.FUNCTION.value
                || currentToken == Keyword.METHOD.value) {
            KeywordObject.parse(currentToken, bufferedWriter, numTabs)
            getNextToken()
        } else {
            throwException()
        }

        // next token is void or a valid type
        when {
            currentToken == Keyword.VOID.value -> {
                KeywordObject.parse(currentToken, bufferedWriter, numTabs)
            }
            Type.isValid(currentToken) -> {
                Type.parse(currentToken, bufferedWriter, numTabs)
            }
            else -> {
                throwException()
            }
        }
        getNextToken()

        // next token is the subroutine name
        parseSubroutineName()
        getNextToken()

        // parse parameter list '(' with param list inside and ending in ')'
        parseParameterList()
        getNextToken()

        // next is the subroutine body
        parseSubroutineBody()
        getNextToken()

        unIndent()
        bufferedWriter.append(Tag.makeCloseTag(currentToken, numTabs))
    }

    private fun parseStatements() {
        bufferedWriter.append("${Tag.makeOpenTag(Category.SUBROUTINE_BODY.value, numTabs)}\n")
        indent()

        while(Statement.isTokenAClassifier(currentToken)) {
            parseStatement()
            getNextToken()
        }

        unIndent()
        bufferedWriter.append(Tag.makeCloseTag(Category.SUBROUTINE_BODY.value, numTabs))
    }

    private fun parseStatement() {
            // classify statement and parse
        if (Statement.isTokenAClassifier(currentToken)) {
            val tag = when {
                currentToken == Keyword.LET.value -> {
                    bufferedWriter.append("${Tag.makeOpenTag(Category.LET_STATEMENT.value, numTabs)}\n")
                    indent()

                    KeywordObject.parse(currentToken, bufferedWriter, numTabs)

                    unIndent()
                    bufferedWriter.append(Tag.makeCloseTag(Category.LET_STATEMENT.value, numTabs))
                }
                currentToken == Keyword.IF.value -> {
                    bufferedWriter.append("${Tag.makeOpenTag(Category.IF_STATEMENT.value, numTabs)}\n")
                    indent()

                    KeywordObject.parse(currentToken, bufferedWriter, numTabs)

                    unIndent()
                    bufferedWriter.append(Tag.makeCloseTag(Category.IF_STATEMENT.value, numTabs))
                }
                currentToken == Keyword.WHILE.value -> {
                    bufferedWriter.append("${Tag.makeOpenTag(Category.WHILE_STATEMENT.value, numTabs)}\n")
                    indent()

                    KeywordObject.parse(currentToken, bufferedWriter, numTabs)

                    unIndent()
                    bufferedWriter.append(Tag.makeCloseTag(Category.WHILE_STATEMENT.value, numTabs))
                }
                currentToken == Keyword.DO.value -> {
                    bufferedWriter.append("${Tag.makeOpenTag(Category.DO_STATEMENT.value, numTabs)}\n")
                    indent()

                    KeywordObject.parse(currentToken, bufferedWriter, numTabs)

                    unIndent()
                    bufferedWriter.append(Tag.makeCloseTag(Category.DO_STATEMENT.value, numTabs))
                }
                currentToken == Keyword.RETURN.value -> {
                    bufferedWriter.append("${Tag.makeOpenTag(Category.RETURN_STATEMENT.value, numTabs)}\n")
                    indent()

                    KeywordObject.parse(currentToken, bufferedWriter, numTabs)

                    unIndent()
                    bufferedWriter.append(Tag.makeCloseTag(Category.RETURN_STATEMENT.value, numTabs))
                }
                else -> {
                    throwException()
                }
            }
        } else {
            throwException()
        }
    }

    private fun parseSubroutineBody() {
        bufferedWriter.append("${Tag.makeOpenTag(Category.SUBROUTINE_BODY.value, numTabs)}\n")
        indent()

        Symbol.parse('{', currentToken, bufferedWriter, numTabs)
        getNextToken()

        // zero or more var decs
        while(ClassVarDec.isTokenAClassifier(currentToken)) {
            parseClassVarDec()
            getNextToken()
        }

        // parse statements
        parseStatements()
        getNextToken()

        Symbol.parse('}', currentToken, bufferedWriter, numTabs)
        unIndent()

        bufferedWriter.append(Tag.makeCloseTag(Category.SUBROUTINE_BODY.value, numTabs))
    }

    private fun parseSubroutineName() {
        Identifier.parse(currentToken, bufferedWriter, numTabs)
    }

    private fun parseParameterList  () {
        Symbol.parse('(', currentToken, bufferedWriter, numTabs)
        getNextToken()

        if (currentToken == ")") {
            // There was no parameter, do nothing
            Symbol.parse(')', currentToken, bufferedWriter, numTabs)
        } else {
            // Since it was not a closing paren, it must be one or more types and param names
            bufferedWriter.append("${Tag.makeOpenTag(Category.PARAMETER_LIST.value, numTabs)}\n")
            indent()

            Type.parse(currentToken, bufferedWriter, numTabs)
            getNextToken()

            parseVarName()
            getNextToken()

            while(currentToken == ",") {
                Symbol.parse(',', currentToken, bufferedWriter, numTabs)
                getNextToken()
                parseVarName()
                getNextToken()
            }

            unIndent()
            bufferedWriter.append(Tag.makeCloseTag(Category.PARAMETER_LIST.value, numTabs))

            Symbol.parse(')', currentToken, bufferedWriter, numTabs)
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

        Type.parse(currentToken, bufferedWriter, numTabs)
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
        getNextToken()
    }

    private fun parseVarName() {
        Identifier.parse(currentToken, bufferedWriter, numTabs)
    }

    object Statement {
        fun isTokenAClassifier(token: String) : Boolean {
            return token == Keyword.LET.value
                    || token == Keyword.IF.value
                    || token == Keyword.WHILE.value
                    || token == Keyword.DO.value
                    || token == Keyword.RETURN.value
        }
    }

    object Type {
        fun isValid(token: String): Boolean {
            return Keyword.INT.value.equals(token)
                    || Keyword.CHAR.value.equals(token)
                    || Keyword.BOOLEAN.value.equals(token)
                    || Identifier.isValid(token)
        }

        private fun isKeyword(token: String): Boolean {
            return Keyword.INT.value.equals(token)
                    || Keyword.CHAR.value.equals(token)
                    || Keyword.BOOLEAN.value.equals(token)
        }

        fun parse(token: String, bufferedWriter: BufferedWriter, numTabs: Int) {
            if (isValid(token)) {
                if (isKeyword(token)) {
                    KeywordObject.parse(token, bufferedWriter, numTabs)
                } else {
                    Identifier.parse(token, bufferedWriter, numTabs)
                }
            } else {
                throw Exception("token: $token is not a Type")
            }
        }
    }

    object SubroutinDec {
        fun isTokenAClassifier(token: String): Boolean {
            return token == Keyword.CONSTRUCTOR.value
                    || token == Keyword.METHOD.value
                    || token == Keyword.FUNCTION.value
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
                throw Exception("expected: $expectedSymbol got: $tokenSymbol")
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