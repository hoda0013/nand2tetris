package com.example.chapter10

import com.example.chapter6.MyAssembler
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.regex.Pattern


class Parser constructor(){

    private var tokens: List<String> = emptyList()
    private var currentToken: String? = null
    private var tokenPointer: Int = 0
    private var outputFile: File = File("parser_output.txt")
    private var bufferedWriter = BufferedWriter(FileWriter(outputFile))

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

    private fun parseClass() {
        if (currentToken.equals(Keyword.CLASS.value, true)) {
            // Print open class tag <class>
            bufferedWriter.append("<class>\n")

            bufferedWriter.append("<keyword> class </keyword>\n")

            getNextToken()

            parseClassName()

            // Print class closing tag
            bufferedWriter.append("</class>\n")
        } else {
            throwException()
        }
    }

    private fun parseClassName() {
        parseIdentifier()
    }

    private fun parseIdentifier() {
        // Must be a sequence of letters, digits or underscore and cannot start with a number
        if(Identifier.isValid(currentToken)) {
            bufferedWriter.append("<identifier> $currentToken </identifier>\n")
        } else {
            throwException()
        }
    }

    private fun throwException() {
        throw Exception("Error parsing token: $currentToken at tokenIndex: $tokenPointer")
    }

    object Identifier {
        val regex = "[a-zA-Z_][a-zA-Z0-9_]*"
        fun isValid(token: String?): Boolean {
            return Pattern.compile(regex).matcher(token).matches()
//            token?.matches(Pattern.compile(regex))
//            return token?.matches(Regex.fromLiteral("[a-zA-Z_][a-zA-Z0-9_]*")) ?: false
        }
    }

    enum class Category() {
        KEYWORD,
        SYMBOL,
        INTEGER_CONSTANT,
        STRING_CONSTANT,
        IDENTIFIER,
        CLASS,
        CLASS_VAR_DEC,
        TYPE,
        SUBROUTINE_DEC,
        PARAMETER_LIST,
        SUBROUTINE_BODY,
        VAR_DEC,
        CLASS_NAME,
        SUBROUTINE_NAME,
        VAR_NAME,
        STATEMENTS,
        STATEMENT,
        LET_STATEMENT,
        IF_STATEMENT,
        WHILE_STATEMENT,
        DO_STATEMENT,
        RETURN_STATEMENT,
        EXPRESSION,
        TERM,
        SUBROUTINE_CALL,
        EXPRESSION_LIST,
        OP,
        UNARY_OP,
        KEYWORD_CONSTANT;

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

        fun makeTag(): String {
            return "<${this.value.toLowerCase()}>"
        }
    }
}