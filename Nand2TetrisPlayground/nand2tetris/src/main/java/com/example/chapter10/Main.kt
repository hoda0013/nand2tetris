package com.example.chapter10

import java.util.ArrayList

fun main(args: Array<String>) {

    val tokens: MutableList<String> = ArrayList<String>().apply {
        add("Class")
        add("Bar")
        add("{")
        add("method")
        add("Fraction")
        add("foo")
        add("(")
        add("int")
        add("y")
        add(")")
    }

    val parser = Parser()
    parser.setTokens(tokens)
    parser.parse()
}