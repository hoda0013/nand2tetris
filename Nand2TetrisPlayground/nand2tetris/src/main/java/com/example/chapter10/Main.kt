package com.example.chapter10

import java.util.ArrayList

fun main(args: Array<String>) {

    val tokens: MutableList<String> = ArrayList<String>().apply {
        add("class")
        add("Bar")
        add("{")
        add("field")
        add("int")
        add("var1")
        add(";")
        add("field")
        add("boolean")
        add("var2")
        add(";")
        add("static")
        add("FakeClassName")
        add("var3")
        add(",")
        add("var4")
        add(";")
        add("method")
        add("Fraction")
        add("foo")
        add("(")
        add("int")
        add("y")
        add(")")
        add("{")
        add("var")
        add("int")
        add("temp")
        add(";")
        add("let")
        add("temp")
        add("=")
        add("(")
        add("xxx")
        add("+")
        add("12")
        add(")")
        add("*")
        add("-")
        add("63")
    }

    val parser = Parser()
    parser.setTokens(tokens)
    parser.parse()
}