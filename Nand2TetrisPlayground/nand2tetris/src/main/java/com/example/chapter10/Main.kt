package com.example.chapter10

fun main(args: Array<String>) {

    val tokenizer = Tokenizer()
    tokenizer.initialize("/Users/bigern/Projects/nand2tetris/projects/10/Square/Main.jack")
    tokenizer.tokenize()
}