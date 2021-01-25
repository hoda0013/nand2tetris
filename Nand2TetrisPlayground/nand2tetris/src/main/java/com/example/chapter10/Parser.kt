package com.example.chapter10

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern


class Parser constructor(val outputDirPath: String, val filename: String) {
    private var tokens: List<Tokenizer.Token> = emptyList()
    private lateinit var currentToken: Tokenizer.Token
    private var tokenPointer: Int = 0
    private var outputFile: File = File("$outputDirPath/$filename.xml")
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
            System.out.println(e)
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
            throw Exception("Can't unindent any more. currentToken = $currentToken")
        }
        numTabs--
    }

    private fun parseKeyword() {
        printTerminalTag(Category.KEYWORD.name.toLowerCase(), currentToken.value)
        getNextToken()
    }

    private fun print(value: String) {
        var output = ""
        for (i in 0 until numTabs) {
            output = output.plus("  ")
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
            getNextToken()
        } else {
            throw Exception("token: $currentToken not a valid Identifier @ Line ${currentToken.line} $filename sed -n ${currentToken.line}p $filename")
        }
    }

    private fun parseSymbol(expectedSymbol: Char) {
        if (currentToken.type == Tokenizer.TokenType.SYMBOL && expectedSymbol.toString() == currentToken.value) {
            printTerminalTag(Category.SYMBOL.value.toLowerCase(), convertSymbolToAmpersandEncoded())
            getNextToken()
        } else {
            throw Exception("expected: $expectedSymbol got: $currentToken @ Line ${currentToken.line} $filename sed -n ${currentToken.line}p $filename")
        }
    }

    // class is a non-terminal
    private fun parseClass() {
        if (currentToken.type == Tokenizer.TokenType.KEYWORD) {
            // Print open class tag <class>
            printNonTerminalOpenTag(Keyword.CLASS.value)

            parseKeyword()

            parseClassName()

            parseSymbol('{')

            while (currentToken.value == Keyword.STATIC.value
                    || currentToken.value == Keyword.FIELD.value) {
                parseClassVarDec()
            }

            while (currentToken.value == Keyword.CONSTRUCTOR.value
                    || currentToken.value == Keyword.METHOD.value
                    || currentToken.value == Keyword.FUNCTION.value) {
                parseSubroutineDec()
            }

            if (currentToken.type == Tokenizer.TokenType.SYMBOL && "}" == currentToken.value) {
                printTerminalTag(Category.SYMBOL.value.toLowerCase(), currentToken.value)
            } else {
                throwException()
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
        } else {
            throwException()
        }

        // next token is void or a valid type or an exception

        if (currentToken.value == Keyword.VOID.value) {
            parseKeyword()
        } else {
            parseType()
        }

        // next token is the subroutine name
        parseSubroutineName()

        // parse parameter list '(' with param list inside and ending in ')'
        parseSymbol('(')

        parseParameterList()

        parseSymbol(')')


        // next is the subroutine body
        parseSubroutineBody()

        printNonTerminalCloseTag(Category.SUBROUTINE_DEC.value)
    }

    private fun parseStatements() {
        printNonTerminalOpenTag(Category.STATEMENTS.value)

        while (currentToken.value == Keyword.LET.value
                || currentToken.value == Keyword.IF.value
                || currentToken.value == Keyword.WHILE.value
                || currentToken.value == Keyword.DO.value
                || currentToken.value == Keyword.RETURN.value) {

            parseStatement()
        }

        printNonTerminalCloseTag(Category.STATEMENTS.value)
    }

    private fun parseStatement() {
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

                    parseVarName()

                    if (currentToken.value == "[") {
                        // If next token is a "[" then you parse an expression
                        parseSymbol('[')

                        parseExpression()
                        // parse expression
                        parseSymbol(']')
                    }

                    parseSymbol('=')

                    parseExpression()

                    parseSymbol(';')

                    printNonTerminalCloseTag(Category.LET_STATEMENT.value)
                }
                Keyword.IF.value -> {
                    printNonTerminalOpenTag(Category.IF_STATEMENT.value)

                    parseKeyword()

                    parseSymbol('(')

                    parseExpression()

                    parseSymbol(')')

                    parseSymbol('{')

                    parseStatements()

                    parseSymbol('}')

                    if (currentToken.value == Keyword.ELSE.value) {
                        parseKeyword()

                        parseSymbol('{')

                        parseStatements()

                        parseSymbol('}')
                    }

                    printNonTerminalCloseTag(Category.IF_STATEMENT.value)
                }
                Keyword.WHILE.value -> {
                    printNonTerminalOpenTag(Category.WHILE_STATEMENT.value)
                    parseKeyword()

                    parseSymbol('(')

                    parseExpression()

                    parseSymbol(')')

                    parseSymbol('{')

                    parseStatements()

                    parseSymbol('}')

                    printNonTerminalCloseTag(Category.WHILE_STATEMENT.value)
                }
                Keyword.DO.value -> {
                    printNonTerminalOpenTag(Category.DO_STATEMENT.value)
                    parseKeyword()

                    parseSubroutineCall()

                    parseSymbol(';')

                    printNonTerminalCloseTag(Category.DO_STATEMENT.value)
                }
                Keyword.RETURN.value -> {
                    printNonTerminalOpenTag(Category.RETURN_STATEMENT.value)
                    parseKeyword()

                    if (currentToken.value == ";") {
                        parseSymbol(';')
                    } else {
                        parseExpression()

                        parseSymbol(';')
                    }

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

            parseType()

            parseVarName()

            while (currentToken.value == ",") {
                parseSymbol(',')
                parseVarName()
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

        // zero or more var decs
        while (currentToken.value == Keyword.VAR.value) {
            parseVarDec()
        }

        // parse statements
        parseStatements()

        parseSymbol('}')

        printNonTerminalCloseTag(Category.SUBROUTINE_BODY.value)
    }

    private fun parseSubroutineName() {
        parseIdentifier()
    }

    private fun parseParameterList() {
        printNonTerminalOpenTag(Category.PARAMETER_LIST.value)

        if (currentToken.value != ")") {
            parseType()

            parseVarName()

            while (currentToken.value == ",") {
                parseSymbol(',')
                parseType()
                parseVarName()
            }
        }

        printNonTerminalCloseTag(Category.PARAMETER_LIST.value)
    }

    private fun parseClassVarDec() {
        printNonTerminalOpenTag(Category.CLASS_VAR_DEC.value)

        if (currentToken.value == Keyword.STATIC.value || currentToken.value == Keyword.FIELD.value) {
            parseKeyword()
        } else {
            throwException()
        }

        // parse the type, which is required and is either int, char, bool or something created by the user or an exception
        parseType()

        parseVarName()

        while (currentToken.value == ",") {
            parseSymbol(',')
            parseVarName()
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
        printNonTerminalOpenTag(Category.EXPRESSION.value)
        parseTerm()

        while (isOp()) {
            parseOp()

            parseTerm()
        }
        printNonTerminalCloseTag(Category.EXPRESSION.value)
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

    private fun parseKeywordConstant() {
        if (currentToken.value == Keyword.TRUE.value
                || currentToken.value == Keyword.FALSE.value
                || currentToken.value == Keyword.NULL.value
                || currentToken.value == Keyword.THIS.value) {

            printTerminalTag(Category.KEYWORD_CONSTANT.value, currentToken.value)
            getNextToken()
        } else {
            throwException()
        }
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
        return (currentToken.type == Tokenizer.TokenType.IDENTIFIER && peekNextToken()!!.value == "(")
                || (currentToken.type == Tokenizer.TokenType.IDENTIFIER && peekNextToken()!!.value == ".")
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

    private fun convertSymbolToAmpersandEncoded() = when(currentToken.value) {
        "<" -> "&lt;"
        ">" -> "&gt;"
        "\"" -> "&quot;"
        "&" -> "&amp;"
        else -> currentToken.value
    }

    private fun parseOp() {
        printTerminalTag(Category.SYMBOL.value, convertSymbolToAmpersandEncoded())

//        printTerminalTag(Category.OP.value, convertSymbolToAmpersandEncoded())

        getNextToken()
    }

    private fun parseUnaryOp() {

        when (currentToken.value) {
            "-" -> {
//                printTerminalTag(Category.UNARY_OP.value, currentToken.value)
                printTerminalTag(Category.SYMBOL.value, currentToken.value)

                getNextToken()
//                parseSymbol('-')
            }
            "~" -> {
//                printTerminalTag(Category.UNARY_OP.value, currentToken.value)

                printTerminalTag(Category.SYMBOL.value, currentToken.value)
                getNextToken()
//                parseSymbol('~')
            }
            else -> {
                throwException()
            }
        }
    }

    private fun parseExpressionList() {
        // Expression lists have 0 or 1 initial expressions
        // If 1 initial expression exists then there may be 0 to N comma separated expressions
//        if (currentToken.value == ")") {
//            return
//        } else {
        printNonTerminalOpenTag(Category.EXPRESSION_LIST.value)
        if (currentToken.value != ")") {
            parseExpression()

            while (currentToken.value == ",") {
                parseSymbol(',')

                parseExpression()
            }
        }
        printNonTerminalCloseTag(Category.EXPRESSION_LIST.value)
//        }
    }

    private fun parseTerm() {
        printNonTerminalOpenTag(Category.TERM.value)
        when {

            currentToken.type == Tokenizer.TokenType.INTEGER -> {
                printTerminalTag(Category.INTEGER_CONSTANT.value, currentToken.value)
                getNextToken()
            }
            currentToken.type == Tokenizer.TokenType.STRING -> {
                printTerminalTag(Category.STRING_CONSTANT.value, currentToken.value)
                getNextToken()
            }
            isKeywordConstant() -> {
                parseKeywordConstant()
            }
            isSubroutineCall() -> {
                parseSubroutineCall()
            }
            isVarOrVarAndExpression() -> {
                // Could be either a var name, a var name the '[' expression ']', or a subroutine call
                parseIdentifier()
//                val nextToken = peekNextToken()

                if (currentToken.value == "[") {
                    parseSymbol('[')

                    parseExpression()

                    parseSymbol(']')
                }
            }

            currentToken.value == "(" -> {
                parseSymbol('(')

                parseExpression()

                parseSymbol(')')
            }
            isUnaryOp() -> {
                parseUnaryOp()

                parseTerm()
            }
            else -> {
                throwException()
            }
        }
        printNonTerminalCloseTag(Category.TERM.value)
    }

    private fun isVarOrVarAndExpression(): Boolean {
        return isVarName() || isVarName() && peekNextToken()!!.value == "["
    }

    private fun parseSubroutineCall() {
//        printNonTerminalOpenTag(Category.SUBROUTINE_CALL.value)
        if (currentToken.type == Tokenizer.TokenType.IDENTIFIER) {
            // subroutineCall will either be a subroutineName followed by a "(" or a className or varName followed by a "."
            parseIdentifier() // takes care of the subRoutineName, className or varName
            when (currentToken.value) {
                "(" -> {
                    parseSymbol('(')

                    parseExpressionList()
                    // Don't advance token here

                    parseSymbol(')')
                }
                "." -> {
                    parseSymbol('.')

                    parseSubroutineName()

                    parseSymbol('(')

                    parseExpressionList()
                    // Don't advance token here

                    parseSymbol(')')
                }
                else -> {
                    throwException()
                }
            }

        } else {
            throwException()
        }
//        printNonTerminalCloseTag(Category.SUBROUTINE_CALL.value)
    }

    private fun throwException() {
        throw Exception("Error parsing token: $currentToken.value at tokenIndex: $tokenPointer @ Line ${currentToken.line} $filename sed -n ${currentToken.line}p $filename")
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
        RETURN_STATEMENT("returnStatement"),
        EXPRESSION("expression"),
        TERM("term"),
        SUBROUTINE_CALL("subroutineCall"),
        EXPRESSION_LIST("expressionList"),
        OP("op"),
        UNARY_OP("unaryOp"),
        KEYWORD_CONSTANT("keyword");

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