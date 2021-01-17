package com.example.chapter10

import java.io.File

fun main(args: Array<String>) {
    val tokenizer = Tokenizer()

    val fileOrDirectory = "/Users/bigern/Projects/nand2tetris/projects/10/Square/"
    val file = File(fileOrDirectory)

    if (file.exists()) {
        if (file.isFile) {
            tokenizer.initialize(fileOrDirectory)
            tokenizer.tokenize()
        } else if (file.isDirectory) {
            // get all .jack files in directory and process them
            file.list()
                    ?.filter { it.endsWith(".jack") }
                    ?.forEach {
                        tokenizer.initialize("$fileOrDirectory$it")
                        tokenizer.tokenize()
                    }
        }
    }
}