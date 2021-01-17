package com.example.chapter10

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern


class Parser {
    private var tokens: List<Tokenizer.Token> = emptyList()
    private lateinit var currentToken: Tokenizer.Token
    private var tokenPointer: Int = 0
    private var outputFile: File = File("parser_output.txt")
    private var bufferedWriter = BufferedWriter(FileWriter(outputFile))
    private var numTabs = 0

    private fun getNextToken(): Tokenizer.Token? {
        tokenPointer++
        currentToken = tokens[tokenPointer]
        return currentToken
    }

    private fun peekNextToken(): Tokenizer.Token? {
        return tokens[tokenPointer + 1]
    }

    fun setTokens(tokens: List<Tokenizer.Token>) {
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

 /** // // */
    private fun unIndent() {
        if (numTabs == 0) {
            throw Exception("Can't unindent any more")
        }
        numTabs--
    }

    private fun parseKeyword() {
        printTerminalTag(Category.KEYWORD.name.toLowerCase(), currentToken.value)
//        if (Keyword.values().map { it.value }.contains(currentToken)) {
//        } else {
//            throw Exception("cannot parse $currentToken as Keyword")
//        }
    }

    private fun print(value: String) {
        var output = ""
        for(i in 0 until numTabs) {
            output = output.plus("\t")
        }
        bufferedWriter.append(output.plus(value))
    }

    private fun printTerminalTag(tagName: String, value: String) {
        print("<$tagName> $value </$tagName>\n")
    }

    private fun printNonTerminalOpenTag(tagName: String) {
        print("<$tagName>\n")
        indent()
    }

    private fun printNonTerminalCloseTag(tagName: String) {
        unIndent()
        print("</$tagName>\n")
    }

//    private fun isIdentifier(): Boolean {
//        val regex = "[a-zA-Z_][a-zA-Z0-9_]*"
//        val pattern = Pattern.compile(regex)
//
//        return pattern.matcher(currentToken.value).matches()
//    }

    private fun parseIdentifier() {
        if (currentToken.type == Tokenizer.TokenType.IDENTIFIER) {
            printTerminalTag(Category.IDENTIFIER.value.toLowerCase(), currentToken.value)
        } else {
            throw Exception("token: $currentToken not a valid Identifier")
        }
    }

    private fun parseSymbol(expectedSymbol: Char) {
        if (currentToken.type == Tokenizer.TokenType.SYMBOL && expectedSymbol.toString() == currentToken.value) {
            printTerminalTag(Category.SYMBOL.value.toLowerCase(), currentToken.value)
        } else {
            throw Exception("expected: $expectedSymbol got: $currentToken")
        }
    }

    // class is a non-terminal
    private fun parseClass() {
        if (currentToken.type == Tokenizer.TokenType.KEYWORD) {
            // Print open class tag <class>
            printNonTerminalOpenTag(Keyword.CLASS.value)

            parseKeyword()
            getNextToken()

            parseClassName()
            getNextToken()

            parseSymbol('{')
            getNextToken()

            while(currentToken.value == Keyword.STATIC.value
                    || currentToken.value == Keyword.FIELD.value) {
                parseClassVarDec()
                getNextToken()
            }

            while(currentToken.value == Keyword.CONSTRUCTOR.value
                    || currentToken.value == Keyword.METHOD.value
                    || currentToken.value == Keyword.FUNCTION.value) {
                parseSubroutineDec()
            }

            // Print class closing tag
            printNonTerminalCloseTag(Category.CLASS.value)
        } else {
            throwException()
        }
    }

    private fun parseSubroutineDec() {
        printNonTerminalOpenTag(Category.SUBROUTINE_DEC.value)

        // first token must be a "constructor" "function" or "method"
        if (currentToken.value == Keyword.CONSTRUCTOR.value
                || currentToken.value == Keyword.FUNCTION.value
                || currentToken.value == Keyword.METHOD.value) {
            parseKeyword()
            getNextToken()
        } else {
            throwException()
        }

        // next token is void or a valid type or an exception

        if (currentToken.value == Keyword.VOID.value) {
            parseKeyword()
        } else {
            parseType()
        }
        getNextToken()

        // next token is the subroutine name
        parseSubroutineName()
        getNextToken()

        // parse parameter list '(' with param list inside and ending in ')'
        parseSymbol('(')
        getNextToken()

        if (currentToken.value == ")") {
            parseSymbol(')')
            getNextToken()
        } else {
            parseParameterList()
            // We don't need to call getNextToken here because param list does that by default
            parseSymbol(')')
            getNextToken()
        }


        // next is the subroutine body
        parseSubroutineBody()
        getNextToken()

        printNonTerminalCloseTag(Category.SUBROUTINE_DEC.value)
    }

    private fun parseStatements() {
        printNonTerminalOpenTag(Category.STATEMENTS.value)

        while(currentToken.value == Keyword.LET.value
                || currentToken.value == Keyword.IF.value
                || currentToken.value == Keyword.WHILE.value
                || currentToken.value == Keyword.DO.value
                || currentToken.value == Keyword.RETURN.value) {

            parseStatement()
            getNextToken()
        }

        printNonTerminalCloseTag(Category.STATEMENTS.value)
    }

    private fun parseStatement() {
        //TODO: Flesh all of these out

            // classify statement and parse
        if (currentToken.value == Keyword.LET.value
                || currentToken.value == Keyword.IF.value
                || currentToken.value == Keyword.WHILE.value
                || currentToken.value == Keyword.DO.value
                || currentToken.value == Keyword.RETURN.value) {
            when (currentToken.value) {
                Keyword.LET.value -> {
                    printNonTerminalOpenTag(Category.LET_STATEMENT.value)

                    parseKeyword()
                    getNextToken()

                    parseVarName()
                    getNextToken()

                    if (currentToken.value == "[") {
                        // If next token is a "[" then you parse an expression
                        parseSymbol('[')
                        getNextToken()

                        parseExpression()
                        // parse expression
                        parseSymbol(']')
                        getNextToken()
                    }

                    parseSymbol('=')
                    getNextToken()

                    parseExpression()
                    getNextToken()

                    parseSymbol(';')

                    printNonTerminalCloseTag(Category.LET_STATEMENT.value)
                }
                Keyword.IF.value -> {
                    printNonTerminalOpenTag(Category.IF_STATEMENT.value)

                    parseKeyword()

                    printNonTerminalCloseTag(Category.IF_STATEMENT.value)
                }
                Keyword.WHILE.value -> {
                    printNonTerminalOpenTag(Category.WHILE_STATEMENT.value)
                    parseKeyword()
                    printNonTerminalCloseTag(Category.WHILE_STATEMENT.value)
                }
                Keyword.DO.value -> {
                    printNonTerminalOpenTag(Category.DO_STATEMENT.value)
                    parseKeyword()
                    printNonTerminalCloseTag(Category.DO_STATEMENT.value)
                }
                Keyword.RETURN.value -> {
                    printNonTerminalOpenTag(Category.RETURN_STATEMENT.value)
                    parseKeyword()
                    printNonTerminalCloseTag(Category.RETURN_STATEMENT.value)
                }
                else -> {
                    throwException()
                }
            }
        } else {
            throwException()
        }
    }

    private fun parseVarDec() {
        printNonTerminalOpenTag(Category.VAR_DEC.value)

        if (currentToken.value == Keyword.VAR.value) {
            parseKeyword()
            getNextToken()

            parseType()
            getNextToken()

            parseVarName()
            getNextToken()

            while(currentToken.value == ",") {
                parseSymbol(',')
                getNextToken()
                parseVarName()
                getNextToken()
            }

            parseSymbol(';')

        } else {
            throwException()
        }

        printNonTerminalCloseTag(Category.VAR_DEC.value)
    }

    private fun parseSubroutineBody() {
        printNonTerminalOpenTag(Category.SUBROUTINE_BODY.value)

        parseSymbol('{')
        getNextToken()

        // zero or more var decs
        while(currentToken.value == Keyword.VAR.value) {
            parseVarDec()
            getNextToken()
        }

        // parse statements
        parseStatements()
        getNextToken()

        parseSymbol('}')

        printNonTerminalCloseTag(Category.SUBROUTINE_BODY.value)
    }

    private fun parseSubroutineName() {
        parseIdentifier()
    }

    private fun parseParameterList() {
            printNonTerminalOpenTag(Category.PARAMETER_LIST.value)

            parseType()
            getNextToken()

            parseVarName()
            getNextToken()

            while(currentToken.value == ",") {
                parseSymbol(',')
                getNextToken()
                parseVarName()
                getNextToken()
            }

            printNonTerminalCloseTag(Category.PARAMETER_LIST.value)
    }

    private fun parseClassVarDec() {
        printNonTerminalOpenTag(Category.CLASS_VAR_DEC.value)

        if (currentToken.value == Keyword.STATIC.value || currentToken.value == Keyword.FIELD.value) {
            parseKeyword()
            getNextToken()
        } else {
            throwException()
        }

        // parse the type, which is required and is either int, char, bool or something created by the user or an exception
        parseType()

        getNextToken()

        parseVarName()
        getNextToken()

        while(currentToken.value == ",") {
            parseSymbol(',')
            getNextToken()
            parseVarName()
            getNextToken()
        }

        parseSymbol(';')

        printNonTerminalCloseTag(Category.CLASS_VAR_DEC.value)
    }

    private fun parseType() {
        if (currentToken.value == Keyword.INT.value
                || currentToken.value == Keyword.CHAR.value
                || currentToken.value == Keyword.BOOLEAN.value) {
            parseKeyword()
        } else {
            parseIdentifier()
        }
    }

    private fun parseVarName() {
        parseIdentifier()
    }

    private fun parseClassName() {
        parseIdentifier()
    }

    private fun parseExpression() {
        parseTerm()
        getNextToken()

        while(isOp()) {
            parseOp()
            getNextToken()

            parseTerm()
            getNextToken()
        }
    }

    private fun isIntegerConstant(): Boolean {
        return try {
            val number = currentToken.value.toInt()
            number in 0..32767
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isKeywordConstant(): Boolean {
        return currentToken.value == Keyword.TRUE.value
                || currentToken.value == Keyword.FALSE.value
                || currentToken.value == Keyword.NULL.value
                || currentToken.value == Keyword.THIS.value
    }

    private fun isStringConstant(): Boolean {
        return currentToken.value.startsWith("\"")
                && currentToken.value.endsWith("\"")
                && !currentToken.value.trimStart().trimEnd().contains("\"")
                && !currentToken.value.trimStart().trimEnd().contains("\n")
    }

    private fun isVarName(): Boolean {
        return currentToken.type == Tokenizer.TokenType.IDENTIFIER
    }

    private fun isSubroutineCall(): Boolean {
        // subroutines start with a subroutine name which is just an identifier
        return currentToken.type == Tokenizer.TokenType.IDENTIFIER
    }

    private fun isUnaryOp(): Boolean {
        return currentToken.value == "~"
                || currentToken.value == "-"
    }

    private fun isOp(): Boolean {
        return currentToken.value == "+"
                || currentToken.value == "-"
                || currentToken.value == "*"
                || currentToken.value == "/"
                || currentToken.value == "&"
                || currentToken.value == "|"
                || currentToken.value == "<"
                || currentToken.value == ">"
                || currentToken.value == "="
    }

    private fun parseOp() {
        printTerminalTag(Category.OP.value, currentToken.value)
    }

    private fun parseUnaryOp() {
        when (currentToken.value) {
            "-" -> {
                parseSymbol('-')
            }
            "~" -> {
                parseSymbol('~')
            }
            else -> {
                throwException()
            }
        }
    }

    private fun parseExpressionList() {
        // Expression lists have 0 or 1 initial expressions
        // If 1 initial expression exists then there may be 0 to N comma separated expressions
        if (currentToken.value == ")") {
            return
        } else {
            parseExpression()
//            getNextToken()

            while(currentToken.value == ",") {
                parseSymbol(',')
                parseExpression()
//                getNextToken()
            }
        }
    }

    private fun parseTerm() {
        when {

            currentToken.type == Tokenizer.TokenType.INTEGER -> {
                printTerminalTag(Category.INTEGER_CONSTANT.value, currentToken.value)
            }
            currentToken.type == Tokenizer.TokenType.STRING -> {
                printTerminalTag(Category.STRING_CONSTANT.value, currentToken.value)
            }
            currentToken.type == Tokenizer.TokenType.KEYWORD -> {
                parseKeyword()
            }
            isVarName() -> {
                parseIdentifier()
                val nextToken = peekNextToken()

                if (nextToken?.value == "[") {
                    getNextToken()
                    parseSymbol('[')
                    getNextToken()
                    parseExpression()
//                    getNextToken()
                    parseSymbol(']')
                }
            }
            isSubroutineCall() -> {
                //
                if (currentToken.type == Tokenizer.TokenType.IDENTIFIER) {
                    // subroutineCall will either be a subroutineName followed by a "(" or a className or varName followed by a "."
                    parseIdentifier() // takes care of the subRoutineName, className or varName
                    getNextToken()
                    when (currentToken.value) {
                        "(" -> {
                            parseSymbol('(')
                            getNextToken()

                            parseExpressionList()
                            // Don't advance token here

                            parseSymbol(')')
                            getNextToken()
                        }
                        "." -> {
                            parseSymbol('.')
                            getNextToken()

                            parseSubroutineName()
                            getNextToken()

                            parseSymbol('(')
                            getNextToken()

                            parseExpressionList()
                            // Don't advance token here

                            parseSymbol(')')
                            getNextToken()
                        }
                        else -> {
                            throwException()
                        }
                    }

                } else {
                    throwException()
                }
            }
            currentToken.value == "(" -> {
                parseSymbol('(')
                getNextToken()

                parseExpression()
//                getNextToken()

                parseSymbol(')')
            }
            isUnaryOp() -> {
                parseUnaryOp()
                getNextToken()

                parseTerm()
            }
            else -> {
                throwException()
            }
        }
    }

    private fun throwException() {
        throw Exception("Error parsing token: $currentToken.value at tokenIndex: $tokenPointer")
    }

    object Symbol {
        private const val regex = "[\\{\\}\\(\\)\\[\\]\\.\\,\\;\\+\\-\\*\\/\\&\\|<>=~]"
    }

    object IntegerConstant {
        fun isValid(token: String): Boolean {
            return try {
                val number = token.toInt()
                number in 0..32767
            } catch (e: NumberFormatException) {
                false
            }
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
    }
}